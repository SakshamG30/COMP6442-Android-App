package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Aditya Iyengar (u7670692)
 * @Editted: Divyesh Srivastava (u7726856) and Omair Soomro (u7517790)
 * Created: 27-April-2024
 * Comments: This is the Forgot Password page for resetting password.
 */
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.*;
import com.example.g11_group_application.firebase_connection_DAO.*;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    public firebase_service_layer firebaseService = new firebase_service_layer();
    String TAG = "ForgotPasswordActivity";
    public String userID;
    public String oldPwd;
    public String SeqQues;
    public String sec_id_answer;
    MainActivity mainActivity = new MainActivity();

    PasswordEncryption passwordEncryption = new PasswordEncryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MyApplication), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        oldPwd = intent.getStringExtra("oldPwd");
        userID = intent.getStringExtra("userID");
        SeqQues = intent.getStringExtra("SeqQues");
        sec_id_answer = intent.getStringExtra("sec_id_answer");

        Configuration configuration = new Configuration();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            configuration.orientation = Configuration.ORIENTATION_PORTRAIT;
        }
        onConfigurationChanged(configuration);
            }
    private void createJsonFileWithNewPassword(String jsonFileName, String newPassword, String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FirestoreSchema.LoginUserMaster.ALUM_NEW_PASSWORD.getAttributeName(), newPassword);
            jsonObject.put(firebase_primary_key_attributes.user_login_master.getComponent_name(), userId);
            String jsonString = jsonObject.toString();
            FileOutputStream fileOutputStream = openFileOutput(jsonFileName, MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    private void createCSVFileWithNewPassword(String csvFileName, String newPassword, String userId) {
        String data = userId + "," + newPassword + "\n";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(csvFileName, MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            Log.d(TAG, "CSV file created successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error creating CSV file", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing file output stream", e);
                }
            }
        }
    }
    private Map<String, String> readDataFromCSV() {
        String csvFilePath = getExternalFilesDir(null) + "/security_questions.csv";
        String line = "";
        String cvsSplitBy = ",";
        Map<String, String> data = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String[] headers = br.readLine().split(cvsSplitBy);
            String[] values = br.readLine().split(cvsSplitBy);
            for (int i = 0; i < headers.length; i++) {
                data.put(headers[i], values[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Portrait widgets
        ImageView imgLoginLogoPortrait = (ImageView) findViewById(R.id.imgLoginLogoPortrait);
        ImageView imgLoginWelcomePortrait = (ImageView) findViewById(R.id.imgLoginWelcomePortrait);
        TextView lblResetPasswordPortrait = (TextView) findViewById(R.id.lblResetPasswordPortrait);
        EditText txtPass1Portrait = (EditText) findViewById(R.id.txtPass1Portrait);
        EditText txtPass2Portrait = (EditText) findViewById(R.id.txtPass2Portrait);
        Button SubmitPasswordResetPortrait = (Button) findViewById(R.id.SubmitPasswordResetPortrait);
        Button CancelPortrait = (Button) findViewById(R.id.CancelPortrait);
        TextView txtSecurityQuestionPortrait = (TextView) findViewById(R.id.txtSecurityQuestionPortrait);
        EditText txtSecAnswerPortrait = (EditText) findViewById(R.id.txtSecAnswerPortrait);
        txtSecurityQuestionPortrait.setText(SeqQues);
         SubmitPasswordResetPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickFunction(view, txtPass1Portrait, txtPass2Portrait, txtSecAnswerPortrait);
            }
        });

        CancelPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v, txtPass1Portrait, txtPass2Portrait, txtSecAnswerPortrait);
            }
        });
        // Landscape widgets
        ImageView imgLoginLogoLandscape = (ImageView) findViewById(R.id.imgLoginLogoLandscape);
        ImageView imgLoginWelcomeLandscape = (ImageView) findViewById(R.id.imgLoginWelcomeLandscape);
        TextView lblResetPasswordLandscape = (TextView) findViewById(R.id.lblResetPasswordLandscape);
        EditText txtPass1Landscape = (EditText) findViewById(R.id.txtPass1Landscape);
        EditText txtPass2Landscape = (EditText) findViewById(R.id.txtPass2Landscape);
        Button SubmitPasswordResetLandscape = (Button) findViewById(R.id.SubmitPasswordResetLandscape);
        Button CancelLandscape = (Button) findViewById(R.id.CancelLandscape);
        TextView txtSecurityQuestionLandscape = (TextView) findViewById(R.id.txtSecurityQuestionLandscape);
        EditText txtSecAnswerLandscape = (EditText) findViewById(R.id.txtSecAnswerLandscape);
        txtSecurityQuestionLandscape.setText(SeqQues);
        SubmitPasswordResetLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickFunction(view, txtPass1Landscape, txtPass2Landscape, txtSecAnswerLandscape);
            }
        });

        CancelLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v, txtPass1Landscape, txtPass2Landscape, txtSecAnswerLandscape);
            }
        });

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the orientation is in landscape, show only the landscape widgets and hide the portrait widgets

            // Show landscape Widgets
            imgLoginLogoLandscape.setVisibility(View.VISIBLE);
            imgLoginWelcomeLandscape.setVisibility(View.VISIBLE);
            lblResetPasswordLandscape.setVisibility(View.VISIBLE);
            txtPass1Landscape.setVisibility(View.VISIBLE);
            txtPass2Landscape.setVisibility(View.VISIBLE);
            SubmitPasswordResetLandscape.setVisibility(View.VISIBLE);
            CancelLandscape.setVisibility(View.VISIBLE);
            txtSecurityQuestionLandscape.setVisibility(View.VISIBLE);
            txtSecAnswerLandscape.setVisibility(View.VISIBLE);
            txtPass1Landscape.setText(txtPass1Portrait.getText());
            txtPass2Landscape.setText(txtPass2Portrait.getText());
            txtPass2Landscape.setText(txtPass2Portrait.getText());
            txtSecurityQuestionLandscape.setText(txtSecurityQuestionPortrait.getText());
            txtSecAnswerLandscape.setText(txtSecAnswerLandscape.getText());

            // Hide Portrait Widgets
            imgLoginLogoPortrait.setVisibility(View.INVISIBLE);
            imgLoginWelcomePortrait.setVisibility(View.INVISIBLE);
            lblResetPasswordPortrait.setVisibility(View.INVISIBLE);
            txtPass1Portrait.setVisibility(View.INVISIBLE);
            txtPass2Portrait.setVisibility(View.INVISIBLE);
            SubmitPasswordResetPortrait.setVisibility(View.INVISIBLE);
            CancelPortrait.setVisibility(View.INVISIBLE);
            txtSecurityQuestionPortrait.setVisibility(View.INVISIBLE);
            txtSecAnswerPortrait.setVisibility(View.INVISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // If the orientation is in portrait, show only the portrait widgets and hide the landscape widgets

            // Show Portrait Widgets
            imgLoginLogoPortrait.setVisibility(View.VISIBLE);
            imgLoginWelcomePortrait.setVisibility(View.VISIBLE);
            lblResetPasswordPortrait.setVisibility(View.VISIBLE);
            txtPass1Portrait.setVisibility(View.VISIBLE);
            txtPass2Portrait.setVisibility(View.VISIBLE);
            SubmitPasswordResetPortrait.setVisibility(View.VISIBLE);
            CancelPortrait.setVisibility(View.VISIBLE);
            txtSecurityQuestionPortrait.setVisibility(View.VISIBLE);
            txtSecAnswerPortrait.setVisibility(View.VISIBLE);
            txtPass1Portrait.setText(txtPass1Landscape.getText());
            txtPass2Portrait.setText(txtPass2Landscape.getText());
            txtSecurityQuestionPortrait.setText(txtSecurityQuestionLandscape.getText());
            txtSecAnswerPortrait.setText(txtSecAnswerLandscape.getText());

            // Hide Landscape Widgets
            imgLoginLogoLandscape.setVisibility(View.INVISIBLE);
            imgLoginWelcomeLandscape.setVisibility(View.INVISIBLE);
            lblResetPasswordLandscape.setVisibility(View.INVISIBLE);
            txtPass1Landscape.setVisibility(View.INVISIBLE);
            txtPass2Landscape.setVisibility(View.INVISIBLE);
            SubmitPasswordResetLandscape.setVisibility(View.INVISIBLE);
            CancelLandscape.setVisibility(View.INVISIBLE);
            txtSecurityQuestionLandscape.setVisibility(View.INVISIBLE);
            txtSecAnswerLandscape.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * This function is used to handle the button click events
     * @param viewid: The view id of the button clicked
     * @param txtPass1: The new password entered by the user
     * @param txtPass2: The confirm password entered by the user
     * @param txtSecAnswer: The security question answer entered by the user
     */
    public void buttonClickFunction(View viewid,
                                    EditText txtPass1,
                                    EditText txtPass2,
                                    EditText txtSecAnswer){

        // Cancel button
        if (viewid.getId() == R.id.CancelLandscape || viewid.getId() == R.id.CancelPortrait){
            if (mainActivity.mainActivityFlag){
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(ForgotPasswordActivity.this, ProfilePage.class);
                intent.putExtra("username", userID);
                startActivity(intent);
            }
        }
        // Submit Password button
        else if (viewid.getId() == R.id.SubmitPasswordResetLandscape || viewid.getId() == R.id.SubmitPasswordResetPortrait){

            boolean flag = false;
            String newPwd = txtPass1.getText().toString();
            if(oldPwd!=null){
                if(oldPwd.contains("|")){
                    String[] passwordArray = oldPwd.split("\\|");
                    if(passwordArray.length == 5){
                        oldPwd = oldPwd.replaceFirst(passwordArray[0].trim() + "\\s\\|","");
                        Log.d(TAG, "onClick: "+ oldPwd);
                        passwordArray[0] = "";
                    }
                    try {
                        for (int i = 0; i < passwordArray.length; i++) {
                            passwordArray[i] = passwordArray[i].trim();
                            if (/*passwordArray[i].equals(newPwd)*/passwordEncryption.verifyPassword(newPwd, passwordArray[i])) {
                                Toast.makeText(ForgotPasswordActivity.this, "New Password should not be the same as your 4 old passwords", Toast.LENGTH_SHORT).show();
                                flag = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error while verifying password", e);
                        return;
                    }
                    if (flag){
                        txtPass1.setError("New Password should not be the same as your 4 old passwords");
                    }
                    else{
                        String newPwd2 = txtPass2.getText().toString();
                        if(newPwd.equals(newPwd2)){
                            oldPwd += " | " + passwordEncryption.encryptPassword(newPwd);
                        }
                    }
                }
                else{
                    try {
                        if(/*oldPwd.equals(newPwd)*/passwordEncryption.verifyPassword(newPwd, oldPwd)){
                            Toast.makeText(ForgotPasswordActivity.this, "Old password and new password are the same. Try again", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String newPwd2 = txtPass2.getText().toString();
                            if(newPwd.equals(newPwd2)){
                                oldPwd += " | " + passwordEncryption.encryptPassword(newPwd);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error while verifying password", e);
                        return;
                    }
                }
            }

            if (!txtSecAnswer.getText().toString().equals(sec_id_answer)) {
                txtSecAnswer.setError("Incorrect answer");
                return;
            }
            Log.d(TAG, "User ID: " + userID);
            try {
                Log.d(TAG, "User Main Tree" + mainActivity.userAVLTreeMain.display());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error while displaying AVL Tree", e);
            }

            if (mainActivity.userAVLTreeMain.find(userID) && !flag){
                NodeData nodeData = mainActivity.userAVLTreeMain.search(userID).getData();
                nodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_NEW_PASSWORD.getAttributeName(), newPwd);
                nodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_OLD_PASSWORD.getAttributeName(), oldPwd);
                mainActivity.userAVLTreeMain.updateNodeData(userID, nodeData);
                try {
                    Log.d(TAG, "User Main Updated Tree" + mainActivity.userAVLTreeMain.display());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error while displaying AVL Tree", e);
                }
                Log.d(TAG, "JSON file Creation started");


                try {

                    Log.d(TAG, mainActivity.userAVLTreeMain.serializeToJson().toString());

                    firebaseService.writeJSONFile(ForgotPasswordActivity.this,
                            firebase_filenames.user_login_master_json.getComponent_name(),
                            firebase_filenames.user_login_master_csv.getComponent_name(),
                            mainActivity.userAVLTreeMain.serializeToJson());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error while creating JSON file", e);
                }
                firebaseService.firebase_connector(getApplicationContext(),
                        firebase_filenames.user_login_master_json.getComponent_name(),
                        firebase_backend_Components.user_login_master.getComponent_name(),
                        firebase_primary_key_attributes.user_login_master.getComponent_name(),
                        firebaseService.write_data_into_db,
                        false,
                        "",
                        "");

                Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Log.e(TAG, "User not found in AVL Tree");
            }
        }

    }
}