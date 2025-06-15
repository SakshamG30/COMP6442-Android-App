package com.example.g11_group_application.firebase_connection_DAO;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * Created: 19-April-2024
 * Comments:  This class is the service layer class that connects to the firebase database
 * It performs the read and write operations on the database
 * It first checks if the user is already signed in
 * If not, it signs in the user and then performs the activity
 * If the user is already signed in, it directly performs the activity
 * The activity can be either read or write
 * If the activity is write, it uploads the data to the firebase database
 * If the activity is read, it fetches the data from the firebase database
 *
 */
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.g11_group_application.Service_layer.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class firebase_service_layer extends Activity {
    private static firebase_backend_Components backendComponents;

    private static final String TAG = "Service_Layer";
    private static FirebaseAuth auth;
    private FirebaseFirestore db;

    public String read_data_from_db = "Read";
    public String write_data_into_db = "Write";
    public final JSONObject userjsonObject = new JSONObject();
    public AVLTree userAVLTree = new AVLTree<>();
    public AVLTree userSecQsAVLTree = new AVLTree<>();
    public AVLTree SecQuesAVLTree = new AVLTree<>();

    PasswordEncryption passwordEncryption = new PasswordEncryption();

    public firebase_service_layer() {
        this.auth = firebaseDAO.getInstance().getFirebaseAuth();
        this.db = firebaseDAO.getInstance().getFirebaseFirestore();
    }

    /**
     * @Author: Divyesh Srivastava (u7726856)
     * Created: 22-April-2024
     * Comments:  This method is used to connect to the firebase database
     * This method first checks if the user is already signed in
     * If not, it signs in the user and then performs the activity
     * If the user is already signed in, it directly performs the activity
     * The activity can be either read or write
     * If the activity is write, it uploads the data to the firebase database
     * If the activity is read, it fetches the data from the firebase database
     *  @param context: The context of the activity
     *  @param fileName: The name of the file to be uploaded
     *  @param tableName: The name of the table in the database
     *  @param PrimaryKeyAttribute: The primary key attribute of the table
     *  @param activity: The activity to be performed (Read or Write)
     */
    public void firebase_connector(Context context, String fileName, String tableName, String PrimaryKeyAttribute, String activity, boolean uploadfromasset, String CSVFilename, String JSONFilename) {
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "Already signed in");
            fetchDataFromFirebase(context,tableName,CSVFilename,JSONFilename,PrimaryKeyAttribute);
            if (activity == write_data_into_db){
                uploadDataToFirebase(context,
                        fileName,
                        tableName,
                        PrimaryKeyAttribute,
                        uploadfromasset,
                        CSVFilename,
                        JSONFilename);
            } else if (activity == read_data_from_db){
                fetchDataFromFirebase(context,
                        tableName,
                        CSVFilename,
                        JSONFilename,
                        PrimaryKeyAttribute);
            }
        } else {
            // Get Email id and Password
            String email = PasswordEncryption.TripleDES_EncodingDecoding_Key(backendComponents.firebase_email.getComponent_name(),-15);
            String password = "";
            try {
                password = PasswordEncryption.TripleDES_decrypt(backendComponents.firebase_password.getComponent_name(),PasswordEncryption.get3DESKey(PasswordEncryption.TripleDES_EncodingDecoding_Key(backendComponents.firebase_3DES_Decryption_Key.getComponent_name(), -15)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Sign in the user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                Log.d(TAG, "Sign in successful");
                                if (activity == write_data_into_db){
                                    uploadDataToFirebase(context,
                                            fileName,
                                            tableName,
                                            PrimaryKeyAttribute,
                                            uploadfromasset,
                                            CSVFilename,
                                            JSONFilename);
                                } else if (activity == read_data_from_db){
                                    fetchDataFromFirebase(context,
                                            tableName,
                                            CSVFilename,
                                            JSONFilename,
                                            PrimaryKeyAttribute);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "Sign in failed", task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * @Author: Divyesh Srivastava (u7726856)
     * Created: 22-April-2024
     * Comments: This method is used to upload data from JSON file to firebase application
     *  It first deletes all the fields from the document and then inserts new data
     *  The primary key attribute is used to uniquely identify each record
     *  If the document does not exist, it creates a new document
     *  If the document exists, it inserts the data into the existing document
     *  @param context: The context of the activity
     *  @param fileName: The name of the file to be uploaded
     *  @param documentName: The name of the document in the database
     *  @param PrimaryKeyAttribute: The primary key attribute of the table
     */
    private void uploadDataToFirebase(Context context, String fileName, String documentName, String PrimaryKeyAttribute, boolean uploadfromasset, String CSVFilename, String JSONFilename) {
        // Get the reference to the document
        DocumentReference docRef = db.collection(backendComponents.collection_name.getComponent_name()).document(documentName);
        Log.d(TAG, "Start delete-insert operation");
        // First read the document to delete all fields
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                WriteBatch batch = db.batch();  // Use a batch to delete fields
                Map<String, Object> docData = documentSnapshot.getData();
                if (docData != null) {
                    Log.d(TAG, "Start with delete operation");
                    for (String key : docData.keySet()) {
                        batch.update(docRef, key, FieldValue.delete());  // Prepare to delete each field
                    }
                    Log.d(TAG, "Start with delete-commit operation");
                    // Commit deletion
                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "All fields deleted successfully");
                        // Proceed with inserting new data after all fields are cleared
                        Log.d(TAG, "Insert new data");
                        uploadNewData(context, fileName, docRef, PrimaryKeyAttribute, uploadfromasset);
                        fetchDataFromFirebase(context,
                                documentName,
                                CSVFilename,
                                JSONFilename,
                                PrimaryKeyAttribute);
                        firebase_signOut();
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error deleting fields", e);
                        firebase_signOut();
                    });
                }
            } else {
                Log.d(TAG, "Document does not exist, creating new one with data");
                // If the document does not exist, treat this as a new document creation
                uploadNewData(context, fileName, docRef, PrimaryKeyAttribute, uploadfromasset);  // Separate method to handle new data upload
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error reading document", e);
            firebase_signOut();
        });
        Log.d(TAG, "Done with delete-insert operation");
    }

    /**
     *  This method is used to upload new data to the firebase database
     *  It reads the JSON file and inserts the data into the document
     *  The primary key attribute is used to uniquely identify each record
     *  If the document does not exist, it creates a new document
     *  If the document exists, it inserts the data into the existing document
     * @param context: The context of the activity
     * @param fileName: The name of the file to be uploaded
     * @param docRef: The reference to the document
     * @param primaryKeyAttribute:  The primary key attribute of the table
     */
    private void uploadNewData(Context context, String fileName, DocumentReference docRef, String primaryKeyAttribute, boolean uploadFromAsset) {
        try {
            String json = "";
            if (!uploadFromAsset) {
                File filePath = new File(context.getExternalFilesDir(null), fileName);
                if (filePath.exists()) {
                    json = new String(Files.readAllBytes(filePath.toPath()), StandardCharsets.UTF_8);
                } else {
                    Log.e(TAG, "File does not exist: " + filePath.getAbsolutePath());
                    return; // Exit if file does not exist
                }
            } else {
                InputStream is = context.getAssets().open(fileName);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                json = new String(buffer, StandardCharsets.UTF_8);
            }

            // Checking the type of JSON data and processing accordingly
            Object jsonData = new JSONTokener(json).nextValue();
            Map<String, Object> allData = new HashMap<>();
            if (jsonData instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) jsonData;
                jsonObject.keys().forEachRemaining(key -> {
                    try {
                        JSONObject individualData = jsonObject.getJSONObject(key);
                        Map<String, Object> data = new HashMap<>();
                        individualData.keys().forEachRemaining(dataKey -> {
                            try {
                                data.put(dataKey, individualData.get(dataKey));
                            } catch (JSONException e) {
                                Log.e(TAG, "Error reading key from JSONObject", e);
                            }
                        });
                        allData.put("Data_" + individualData.getString(primaryKeyAttribute), data);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error processing JSON object", e);
                    }
                });
            } else if (jsonData instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) jsonData;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String, Object> data = new HashMap<>();
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        data.put(key, jsonObject.get(key));
                    }
                    allData.put("Data_" + jsonObject.getString(primaryKeyAttribute), data);
                }
            } else {
                throw new IllegalArgumentException("JSON data is neither an array nor an object");
            }

            // Insert new data into a new document
            docRef.set(allData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Document created and data added successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding data to new document", e);
                        firebase_signOut();
                    });
        } catch (Exception e) {
            firebase_signOut();
            Log.e(TAG, "Error processing JSON for new document", e);
        }
    }

    /**
     *  This method is used to fetch data from the firebase database
     *  It reads the data from the document and writes it to a CSV file
     * @param context: The context of the activity
     * @param documentName: The name of the document to be fetched
     */
    private void fetchDataFromFirebase(Context context, String documentName, String CSVFilename, String JSONFilename, String primarykeyAttribute) {
        try {

            db.collection(backendComponents.collection_name.getComponent_name())
                    .document(documentName)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Writing data to CSV started");
                            // Dynamically extract data from document
                            Map<String, Object> dataMap = documentSnapshot.getData();
                            if (dataMap != null) {
                                List<String[]> dataLines = new ArrayList<>();
                                // Create headers dynamically
                                Set<String> headersSet = new HashSet<>();

                                // Collect all possible headers and prepare data rows
                                List<Map<String, Object>> allDataMaps = new ArrayList<>();

                                // Assuming dataMap values could be Maps themselves
                                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                                    if (entry.getValue() instanceof Map) {
                                        Map<String, Object> valueMap = (Map<String, Object>) entry.getValue();
                                        allDataMaps.add(valueMap);
                                        headersSet.addAll(valueMap.keySet());
                                    }
                                }

                                // Sort headers to maintain order in CSV
                                List<String> headers = new ArrayList<>(headersSet);
                                Collections.sort(headers);
                                dataLines.add(headers.toArray(new String[0]));

                                // Create data lines
                                for (Map<String, Object> map : allDataMaps) {
                                    List<String> values = new ArrayList<>();
                                    for (String header : headers) {
                                        Object value = map.get(header);
                                        values.add(value != null ? value.toString() : "null"); // Handling null values
                                    }
                                    dataLines.add(values.toArray(new String[0]));
                                }

                                // Write to CSV
                                writeDataToCSV(context, dataLines, CSVFilename);
                                if (CSVFilename.equals(firebase_filenames.security_question_master_csv.getComponent_name())){
                                    sortCSVInteger(context, CSVFilename, primarykeyAttribute);
                                }else{
                                    sortCSV(context, CSVFilename, primarykeyAttribute);
                                }
                                Log.d(TAG, "Data Written successfully to CSV file");
                                csv_to_json_convertor.csvToJson(context, CSVFilename, JSONFilename);
                                createJSONObjects(context,JSONFilename);
                                firebase_signOut();
                            }
                        } else {
                            Log.d(TAG, "No such document" + documentName);
                            firebase_signOut();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch data for " + documentName + ": ", e);
                        if (e instanceof FirebaseFirestoreException) {
                            FirebaseFirestoreException ex = (FirebaseFirestoreException) e;
                            Log.e(TAG, "Error Code: " + ex.getCode().name());
                            firebase_signOut();
                        }
                    });
        } catch (Exception e){
            Log.e(TAG, "Error processing JSON", e);
        }
    }

    /**
     * This method is used to write data to a CSV file
     * @param context: The context of the activity
     * @param dataLines: The data to be written to the CSV file
     */
    private void writeDataToCSV(Context context, List<String[]> dataLines, String CSVFilename) {
        File csvFilePath = new File(context.getExternalFilesDir(null), CSVFilename);
        String startenclosure = "\"";
        String endenclosure = "\"";

        try (FileWriter writer = new FileWriter(csvFilePath)) {
            for (String[] line : dataLines) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    if (CSVFilename.equals(firebase_filenames.security_question_master_csv.getComponent_name())){
                        sb.append(line[i]);
                    } else {
                        sb.append(startenclosure + line[i] + endenclosure);
                    }
                    // Append comma only if it's not the last element
                    if (i < line.length - 1) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing CSV", e);
        }
    }

    /**
     * This method is used to sort the CSV file based on the first column
     * @param context: The context of the activity
     * @param CSVFilename: The name of the CSV file
     */

    public void sortCSVInteger(Context context, String CSVFilename, String columnHeader) {
        try {
            // Define the file path using the context and CSV filename
            File csvFilePath = new File(context.getExternalFilesDir(null), CSVFilename);
            Path path = csvFilePath.toPath();
            String startenclosure = "\"";
            String endenclosure = "\"";
            // Read all lines from the CSV file
            List<String> lines = Files.readAllLines(path);

            if (lines.isEmpty()) {
                return;
            }

            // Extract the header
            String header = lines.remove(0);
            String[] headers = header.split(",");

            // Find the index of the column to sort by
            int columnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (CSVFilename.equals(firebase_filenames.security_question_master_csv.getComponent_name())){
                    if (headers[i].trim().equalsIgnoreCase(columnHeader )) {
                        columnIndex = i;
                        break;
                    }
                }else {
                    if (headers[i].trim().equalsIgnoreCase(startenclosure + columnHeader + endenclosure)) {
                        Log.d(TAG, "Column found: " + columnHeader);
                        columnIndex = i;
                        break;
                    }
                }
            }

            if (columnIndex == -1) {
                Log.e(TAG,"Column '" + columnHeader + "' not found");
                return; // Column not found, exit the method
            }

            // Sort lines based on the specified column
            int finalColumnIndex = columnIndex;
            Collections.sort(lines, new Comparator<String>() {
                @Override
                public int compare(String line1, String line2) {
                    Integer firstColumn1 = Integer.parseInt(line1.split(",")[finalColumnIndex].trim());
                    Integer firstColumn2 = Integer.parseInt(line2.split(",")[finalColumnIndex].trim());
                    return firstColumn1.compareTo(firstColumn2);
                }
            });

            // Reinsert the header at the beginning
            List<String> sortedLines = new ArrayList<>();
            sortedLines.add(header);
            sortedLines.addAll(lines);

            // Optionally, write the sorted lines back to the file
            Files.write(path, sortedLines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sortCSV(Context context, String CSVFilename, String columnHeader) {
        File csvFile = new File(context.getExternalFilesDir(null), CSVFilename);
        Path path = csvFile.toPath();
        String startenclosure = "\"";
        String endenclosure = "\"";

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> allRows = reader.readAll();
            if (allRows.isEmpty()) return;

            // Extract headers and create header map
            String[] headers = allRows.get(0);
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            // Finding the index of the column to sort by
            Integer columnIndex = headerMap.get(columnHeader);
            if (columnIndex == null) {
                System.out.println("Column '" + columnHeader + "' not found");
                return; // Column not found, exit the method
            }

            // Exclude the header for sorting
            List<String[]> sortableRows = new ArrayList<>(allRows.subList(1, allRows.size()));
            Integer finalColumnIndex = columnIndex;
            {
                Collections.sort(sortableRows, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] row1, String[] row2) {
                        return row1[finalColumnIndex].compareToIgnoreCase(row2[finalColumnIndex]);
                    }
                });
            }

            // Reinsert the header at the beginning
            sortableRows.add(0, headers);

            // Write the sorted data back to the CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                writer.writeAll(sortableRows);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to sign out from the firebase database
     * It checks if the user is already signed in
     * If the user is signed in, it signs out the user
     * If the user is not signed in, it logs the user out
     */
    private void firebase_signOut(){
        if (auth.getCurrentUser() != null) {
            auth.signOut();
            Log.d(TAG, "Signed out");
        }
    }

    /**
     * @Author: Divyesh Srivastava (u7726856)
     * This method is used to create JSON objects
     * It reads the JSON file and creates JSON objects
     * @param context: The context of the activity
     * @param JSONFilename: The name of the JSON file
     */
    public void createJSONObjects(Context context,String JSONFilename) {
        Log.d(TAG, "JSON Object start");
        JSONObject userjsonObject = null;
        userjsonObject = jsonObject(context, JSONFilename);

        // Deserialize from JSON
        try {
            if (JSONFilename.equals(firebase_filenames.user_login_master_json.getComponent_name())){
                userAVLTree.deserializeFromJson(userjsonObject);
            } else if (JSONFilename.equals(firebase_filenames.user_security_questions_json.getComponent_name())) {
                userSecQsAVLTree.deserializeFromJson(userjsonObject);
            } else if (JSONFilename.equals(firebase_filenames.security_question_master_json.getComponent_name())) {
                SecQuesAVLTree.deserializeFromJson(userjsonObject);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject jsonObject(Context context, String fileName) {
        try {
            String json = "";
            File filePath = new File(context.getExternalFilesDir(null), fileName);
            if (filePath.exists()) {
                json = new String(Files.readAllBytes(filePath.toPath()), StandardCharsets.UTF_8);
            } else {
                Log.e(TAG, "File does not exist: " + filePath.getAbsolutePath());
                return null;
            }
            return new JSONObject(json);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error reading JSON file", e);
            return null;
        }
    }

    /**
     * This method is used to convert JSON to CSV
     * It reads the JSON file and converts it to CSV
     * @param context: The context of the activity
     * @param jsonObject: The JSON object to be converted
     * @param CSVFilename: The name of the CSV file
     */
    private void jsonToCSV(Context context, JSONObject jsonObject, String CSVFilename) {

        try {
            List<String[]> dataLines = new ArrayList<>();
            List<String> headerList = new ArrayList<>();
            boolean headerAdded = false;

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject obj = jsonObject.getJSONObject(key);
                List<String> dataList = new ArrayList<>();

                if (!headerAdded) {
                    Iterator<String> innerKeys = obj.keys();
                    while (innerKeys.hasNext()) {
                        String header = innerKeys.next();
                        headerList.add(header);
                        dataList.add(obj.getString(header));
                    }
                    dataLines.add(headerList.toArray(new String[0]));
                    headerAdded = true;
                } else {
                    for (String header : headerList) {
                        dataList.add(obj.getString(header));
                    }
                }
                dataLines.add(dataList.toArray(new String[0]));
            }

            writeDataToCSV(context, dataLines, CSVFilename);
        } catch (Exception e) {
            Log.e("JSON_TO_CSV", "Error converting JSON to CSV", e);
        }
    }
    /**
     * This method is used to write JSON to a file
     * @param context: The context of the activity
     * @param jsonFileName: The name of the JSON file
     * @param csvFileName: The name of the CSV file
     * @param jsonObject: The JSON object to be written
     * @return: True if the JSON file is written successfully, false otherwise
     */
    public boolean writeJSONFile(Context context, String jsonFileName, String csvFileName,JSONObject jsonObject) {
        Log.d(TAG, "Inside writeJSONFile");
        Log.d(TAG, "JSON Object to write:" + jsonObject.toString());
        Log.d(TAG, "File Name:" + jsonFileName);

        // Convert JSON to CSV
        jsonToCSV(context, jsonObject, csvFileName);
        // Convert CSV back to JSON using existing csv_to_json_convertor
        csv_to_json_convertor.csvToJson(context, csvFileName, jsonFileName);
        return true;
    }
}
