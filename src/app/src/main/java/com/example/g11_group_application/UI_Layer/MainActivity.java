package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * @Editted: Aditya Iyengar (u7670692), Omair Soomro (u7517790)
 * Created: 12-April-2024
 * Comments: This is the main activity class. It will be used for login screen.
 * From this screen, user can pass in the email or user id and password to login.
 * In case the user has forgotten the password, they can click on Forgot Password? link to
 * reset the password.
 *
 */

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.*;
import com.example.g11_group_application.firebase_connection_DAO.*;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityClass";
    firebase_service_layer serviceLayer = new firebase_service_layer();
    PasswordEncryption passwordEncryption = new PasswordEncryption();
    public static AVLTree userAVLTreeMain = new AVLTree<>();
    public static AVLTree userSeqAVLTreeMain = new AVLTree<>();
    public static AVLTree SecQuesAVLTree = new AVLTree<>();
    public static boolean initialize_done = false;
    public static boolean mainActivityFlag = false;

    /**
     * @Author: Divyesh Srivastava (u7726856)
     * Created: 12-April-2024
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the default orientation to Portrait
        Configuration configuration = new Configuration();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            configuration.orientation = Configuration.ORIENTATION_PORTRAIT;
        }
        onConfigurationChanged(configuration);

        Log.d(TAG, "Start createInitialAVlTrees");
        Log.d(TAG,"initialize_done: " + initialize_done);

        // Just an emergency part of code to insert initial data into the database
        /*serviceLayer.firebase_connector(getApplicationContext(),
                firebase_filenames.user_login_master_json.getComponent_name(),
                firebase_backend_Components.user_login_master.getComponent_name(),
                firebase_primary_key_attributes.user_login_master.getComponent_name(),
                serviceLayer.write_data_into_db,
                true,
                "",
                "");*/
        /*serviceLayer.firebase_connector(getApplicationContext(),
                firebase_filenames.user_login_master_json.getComponent_name(),
                firebase_backend_Components.user_login_master.getComponent_name(),
                firebase_primary_key_attributes.user_login_master.getComponent_name(),
                serviceLayer.write_data_into_db,
                false,
                "",
                "");*/

        // Create all required AVL trees for the application
        if (!initialize_done) {
            Log.d(TAG,"Just before createInitialAVlTrees");
            createInitialAVlTrees();
        }
    }

    /**
     * @Author: Divyesh Srivastava (u7726856)
     * Created: 03-May-2024
     * Comments: This method is used to create initial AVL trees for the application.
     * This method will be called only once when the application is started.
     * This method will create AVL trees for all the tables in the database.
     * This method will be called from the onCreate method.
     * This method will call the firebase_connector method from the firebase_service_layer class.
     * @return: Boolean
     *  1. True: If the AVL trees are created successfully.
     *  2. False: If the AVL trees are not created successfully.
     */
    private Boolean createInitialAVlTrees() {
        Log.d(TAG,"start createInitialAVlTrees");
        // Create AVL tree for user_login_master
        try {
            serviceLayer.firebase_connector(getApplicationContext(),
                    firebase_filenames.user_login_master_json.getComponent_name(),
                    firebase_backend_Components.user_login_master.getComponent_name(),
                    firebase_primary_key_attributes.user_login_master.getComponent_name(),
                    serviceLayer.read_data_from_db,
                    true,
                    firebase_filenames.user_login_master_csv.getComponent_name(),
                    firebase_filenames.user_login_master_json.getComponent_name());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // Create AVL tree for user security questions
        try {
            serviceLayer.firebase_connector(getApplicationContext(),
                    firebase_filenames.user_security_questions_json.getComponent_name(),
                    firebase_backend_Components.user_security_questions.getComponent_name(),
                    firebase_primary_key_attributes.user_security_questions.getComponent_name(),
                    serviceLayer.read_data_from_db,
                    true,
                    firebase_filenames.user_security_questions_csv.getComponent_name(),
                    firebase_filenames.user_security_questions_json.getComponent_name());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Create AVL tree for security questions
        try {
            serviceLayer.firebase_connector(getApplicationContext(),
                    firebase_filenames.security_question_master_json.getComponent_name(),
                    firebase_backend_Components.security_question_master.getComponent_name(),
                    firebase_primary_key_attributes.security_question_master.getComponent_name(),
                    serviceLayer.read_data_from_db,
                    true,
                    firebase_filenames.security_question_master_csv.getComponent_name(),
                    firebase_filenames.security_question_master_json.getComponent_name());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        initialize_done = true;
        return true;
    }

    /**
     * @Author: Divyesh Srivastava (u7726856)
     * Created: 12-April-2024
     * This is a separate handling for different orientations.
     * @param newConfig The new device configuration.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Landscape widgets
        ImageView imgLoginLogoLandscape = (ImageView) findViewById(R.id.imgLoginLogoLandscape);
        ImageView imgLoginWelcomeLandscape = (ImageView) findViewById(R.id.imgLoginWelcomeLandscape);
        EditText txtEmailLandscape = (EditText) findViewById(R.id.txtEmailLandscape);
        EditText txtPasswordLandscape = (EditText) findViewById(R.id.txtPasswordLandscape);
        Button cmdLoginLandscape = (Button) findViewById(R.id.cmdLoginLandscape);
        TextView lblForgotPasswordLandscape = (TextView) findViewById(R.id.lblForgotPasswordLandscape);
        TextView lblCreateNewUserLandscape = (TextView) findViewById(R.id.lblCreateNewUserLandscape);

        cmdLoginLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                buttonClickFunction(v,txtEmailLandscape, txtPasswordLandscape);
            }
        });

        lblCreateNewUserLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,txtEmailLandscape, txtPasswordLandscape);
            }
        });
        lblForgotPasswordLandscape.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonClickFunction(v,txtEmailLandscape, txtPasswordLandscape);
            }
        });

        // Portrait widgets
        ImageView imgLoginLogoPortrait = (ImageView) findViewById(R.id.imgLoginLogoPortrait);
        ImageView imgLoginWelcomePortrait = (ImageView) findViewById(R.id.imgLoginWelcomePortrait);
        TextView lblEmailUNamePortrait = (TextView) findViewById(R.id.lblEmailUNamePortrait);
        EditText txtEmailPortrait = (EditText) findViewById(R.id.txtEmailPortrait);
        TextView lblPasswordPortrait = (TextView) findViewById(R.id.lblPasswordPortrait);
        EditText txtPasswordPortrait = (EditText) findViewById(R.id.txtPasswordPortrait);
        Button cmdLoginPortrait = (Button) findViewById(R.id.cmdLoginPortrait);
        TextView lblForgotPasswordPortrait = (TextView) findViewById(R.id.lblForgotPasswordPortrait);
        TextView lblCreateNewUserPortrait = (TextView) findViewById(R.id.lblCreateNewUserPortrait);
        cmdLoginPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                buttonClickFunction(v,txtEmailPortrait, txtPasswordPortrait);
            }
        });

        lblCreateNewUserPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,txtEmailPortrait, txtPasswordPortrait);
            }
        });

        lblForgotPasswordPortrait.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickFunction(v,txtEmailPortrait, txtPasswordPortrait);
        }
        });

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the orientation is in landscape, show only the landscape widgets and hide the portrait widgets

            // Show landscape Widgets
            imgLoginLogoLandscape.setVisibility(View.VISIBLE);
            imgLoginWelcomeLandscape.setVisibility(View.VISIBLE);
            txtEmailLandscape.setVisibility(View.VISIBLE);
            txtPasswordLandscape.setVisibility(View.VISIBLE);
            cmdLoginLandscape.setVisibility(View.VISIBLE);
            lblForgotPasswordLandscape.setVisibility(View.VISIBLE);
            lblCreateNewUserLandscape.setVisibility(View.VISIBLE);
            txtEmailLandscape.setText(txtEmailPortrait.getText());
            txtPasswordLandscape.setText(txtPasswordPortrait.getText());

            // Hide Portrait Widgets
            imgLoginLogoPortrait.setVisibility(View.INVISIBLE);
            imgLoginWelcomePortrait.setVisibility(View.INVISIBLE);
            lblEmailUNamePortrait.setVisibility(View.INVISIBLE);
            txtEmailPortrait.setVisibility(View.INVISIBLE);
            lblPasswordPortrait.setVisibility(View.INVISIBLE);
            txtPasswordPortrait.setVisibility(View.INVISIBLE);
            cmdLoginPortrait.setVisibility(View.INVISIBLE);
            lblForgotPasswordPortrait.setVisibility(View.INVISIBLE);
            lblCreateNewUserPortrait.setVisibility(View.INVISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // If the orientation is in portrait, show only the portrait widgets and hide the landscape widgets

            // hide landscape Widgets
            imgLoginLogoLandscape.setVisibility(View.INVISIBLE);
            imgLoginWelcomeLandscape.setVisibility(View.INVISIBLE);
            txtEmailLandscape.setVisibility(View.INVISIBLE);
            txtPasswordLandscape.setVisibility(View.INVISIBLE);
            cmdLoginLandscape.setVisibility(View.INVISIBLE);
            lblForgotPasswordLandscape.setVisibility(View.INVISIBLE);
            lblCreateNewUserLandscape.setVisibility(View.INVISIBLE);
            txtEmailPortrait.setText(txtEmailLandscape.getText());
            txtPasswordPortrait.setText(txtPasswordLandscape.getText());

            // Show Portrait Widgets
            imgLoginLogoPortrait.setVisibility(View.VISIBLE);
            imgLoginWelcomePortrait.setVisibility(View.VISIBLE);
            lblEmailUNamePortrait.setVisibility(View.VISIBLE);
            txtEmailPortrait.setVisibility(View.VISIBLE);
            lblPasswordPortrait.setVisibility(View.VISIBLE);
            txtPasswordPortrait.setVisibility(View.VISIBLE);
            cmdLoginPortrait.setVisibility(View.VISIBLE);
            lblForgotPasswordPortrait.setVisibility(View.VISIBLE);
            lblCreateNewUserPortrait.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("App Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity(); // Close all activites and return to home screen (i.e., exit the app)
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    /**
     * This is a helper function which handles the button click events.
     * This function will be called when the user clicks on the Login, Create New User or Forgot Password buttons.
     * @param viewid: The view id of the button clicked.
     * @param txtEmail: The email or user id entered by the user.
     * @param txtPassword: The password entered by the user.
     */
    public void buttonClickFunction(View viewid,
                                    EditText txtEmail,
                                    EditText txtPassword){

        serviceLayer.createJSONObjects(getApplicationContext(), firebase_filenames.user_login_master_json.getComponent_name());
        serviceLayer.createJSONObjects(getApplicationContext(), firebase_filenames.user_security_questions_json.getComponent_name());
        serviceLayer.createJSONObjects(getApplicationContext(), firebase_filenames.security_question_master_json.getComponent_name());
        userAVLTreeMain = serviceLayer.userAVLTree;
        userSeqAVLTreeMain = serviceLayer.userSecQsAVLTree;
        SecQuesAVLTree = serviceLayer.SecQuesAVLTree;
        String EmailId = "";
        if (txtEmail.getText().toString().equals("comp2100@anu.edu.au")) {
            EmailId = "ANCW1";
        } else if (txtEmail.getText().toString().equals("comp6442@anu.edu.au")){
            EmailId = "ANCW2";
        } else {
            EmailId = txtEmail.getText().toString();
        }
        // Login
        if (viewid.getId() == R.id.cmdLoginPortrait || viewid.getId() == R.id.cmdLoginLandscape) {
            String defaultEmail = getResources().getString(R.string.src_Email_Username);
            String defaultPassword = getResources().getString(R.string.src_Enter_Password);

            if (EmailId.isBlank() || EmailId.isEmpty() || EmailId.equals(defaultEmail) || txtPassword.getText().toString().isBlank() || txtPassword.getText().toString().isEmpty() || txtPassword.getText().toString().equals(defaultPassword) || txtPassword.getText().toString().isBlank() || txtPassword.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter valid username or password", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Log.d(TAG, "Start createInitialAVlTrees" + userAVLTreeMain.display());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (userAVLTreeMain.find(EmailId)) {
                    String userID = EmailId;
                    String password = txtPassword.getText().toString();
                    NodeData nodeData = userAVLTreeMain.search(userID).getData();
                    String serverPwd = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_NEW_PASSWORD.getAttributeName());
                    try {
                        if (passwordEncryption.verifyPassword(password, serverPwd)) {
                            Intent intent = new Intent(MainActivity.this, AppMainPage.class);
                            intent.putExtra("serverPwd", serverPwd);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Please enter valid username or password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Please enter valid username or password", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    txtEmail.setError("Invalid Username");
                }
            }
        }
        // Create new user
        else if (viewid.getId() == R.id.lblCreateNewUserPortrait  || viewid.getId() == R.id.lblCreateNewUserLandscape) {
            try{
                Log.d(TAG, "Start createInitialAVlTrees" + userAVLTreeMain.display());
            }catch (JSONException e){
                e.printStackTrace();
            }
            Intent intent = new Intent(MainActivity.this, CreateNewUser.class);
            startActivity(intent);
        }
        // Forgot password
        else if (viewid.getId() == R.id.lblForgotPasswordPortrait || viewid.getId() == R.id.lblForgotPasswordLandscape) {
            String defaultEmail = getResources().getString(R.string.src_Email_Username);
            if (EmailId.isBlank() || EmailId.equals(defaultEmail)) {
                txtEmail.setError("Please enter the email address");
                return;

            } else {
                try {
                    Log.d(TAG, "Start createInitialAVlTrees" + userAVLTreeMain.display());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(serviceLayer.userAVLTree.find(EmailId)){
                    mainActivityFlag = true;
                    String userID = EmailId;
                    Log.d(TAG, "UserID: " + userID);
                    NodeData nodeData = serviceLayer.userAVLTree.search(userID).getData();
                    NodeData usersecquData = serviceLayer.userSecQsAVLTree.search(userID).getData();

                    // Searching for security question id and answer set by the user
                    Random random = new Random();
                    String sec_id = "";
                    String sec_id_answer = "";
                    int randomNumber = random.nextInt(3) + 1;
                    Log.d(TAG, "Random Number: " + randomNumber);
                    if (randomNumber == 1){
                        Log.d(TAG, "Inside Random Number 1");
                        sec_id = usersecquData.getAttributeValue(FirestoreSchema.UserSecurityQuestion.AUSQ_SEC_1_ID.getAttributeName()).toString();
                        sec_id_answer = usersecquData.getAttributeValue(FirestoreSchema.UserSecurityQuestion.AUSQ_SEC_1_ANS.getAttributeName()).toString();
                    }
                    else if (randomNumber == 2){
                        Log.d(TAG, "Inside Random Number 2");
                        sec_id = usersecquData.getAttributeValue(FirestoreSchema.UserSecurityQuestion.AUSQ_SEC_2_ID.getAttributeName()).toString();
                        sec_id_answer = usersecquData.getAttributeValue(FirestoreSchema.UserSecurityQuestion.AUSQ_SEC_2_ANS.getAttributeName()).toString();
                    }
                    else if (randomNumber == 3){
                        Log.d(TAG, "Inside Random Number 3");
                        sec_id = usersecquData.getAttributeValue(FirestoreSchema.UserSecurityQuestion.AUSQ_SEC_3_ID.getAttributeName()).toString();
                        sec_id_answer = usersecquData.getAttributeValue(FirestoreSchema.UserSecurityQuestion.AUSQ_SEC_3_ANS.getAttributeName()).toString();
                    }

                    // Searching for security question from the security question master
                    int secidint = Integer.parseInt(sec_id);
                    NodeData secquData = SecQuesAVLTree.search(secidint).getData();
                    String SeqQues = secquData.getAttributeValue(FirestoreSchema.SecurityQuestionMaster.ASQM_SEC_QUESTION.getAttributeName()).toString();

                    String oldPwd = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_OLD_PASSWORD.getAttributeName());
                    Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                    intent.putExtra("oldPwd", oldPwd);
                    intent.putExtra("userID", userID);
                    intent.putExtra("SeqQues", SeqQues);
                    intent.putExtra("sec_id_answer", sec_id_answer);
                    startActivity(intent);
                }
                else{
                    txtEmail.setError("Invalid Username");
                }
            }
        }
    }
}