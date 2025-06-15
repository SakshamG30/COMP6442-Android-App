/**
 * @Author: Aditya Iyengar (u7670692)
 * Created: 18-April-2024
 * Comments: This is the csv to json conversion class. It will be used for converting
 * csv dataset to json for firebase reading.
 */

package com.example.g11_group_application.firebase_connection_DAO;
import android.content.Context;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
public class csv_to_json_convertor {

    public static void csvToJson(Context context, String csvFile, String jsonFile) {
        try {
            File csvFilePath = new File(context.getExternalFilesDir(null), csvFile);
            File jsonFilePath = new File(context.getExternalFilesDir(null), jsonFile);

            JSONObject indexedJson = new JSONObject(); // New JSON Object to hold indexed items
            try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
                String[] headers = reader.readNext(); // Read headers first
                if (headers != null) {
                    String[] nextLine;
                    int index = 1; // Initialize index for naming JSON objects
                    while ((nextLine = reader.readNext()) != null) { // Read each line after headers
                        JSONObject jsonObject = new JSONObject();
                        for (int j = 0; j < headers.length; j++) {
                            jsonObject.put(headers[j], nextLine[j]);
                        }
                        if (jsonFile.equals(firebase_filenames.security_question_master_json.getComponent_name())){
                            indexedJson.put(String.valueOf(index++), jsonObject); // Put with an index
                        }else {
                            indexedJson.put("ANCW" + String.valueOf(index++), jsonObject); // Put with an index
                        }
                    }
                }

                // Write JSON to file
                try (FileWriter file = new FileWriter(jsonFilePath)) {
                    file.write(indexedJson.toString(4)); // Writing JSON with indentation
                    file.flush();
                }
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}