package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Omair Soomro (u7517790)
 * @Edited: Divyesh Srivastava (u7726856) and Aditya Iyengar (u7670692)
 * Created: 15-May-2024
 * Comments: This is the page to create the new user
 */
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.g11_group_application.R;
import com.example.g11_group_application.firebase_connection_DAO.*;
import com.example.g11_group_application.Service_layer.*;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateNewUser extends AppCompatActivity {
    MainActivity mainActivity = new MainActivity();
    firebase_service_layer firebaseService = new firebase_service_layer();
    String TAG = "CreateNewUser";
    PasswordEncryption passwordEncryption = new PasswordEncryption();
    ArrayList<String> questionlist = new ArrayList();
    ArrayList<String> originalQuestionList = new ArrayList<>();
    ArrayList<String> questionList1 = new ArrayList<>();
    ArrayList<String> questionList2 = new ArrayList<>();
    ArrayList<String> questionList3 = new ArrayList<>();
    ArrayAdapter<String> securityAdapter2;
    ArrayAdapter<String> securityAdapter3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MyApplication), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Configuration configuration = new Configuration();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            configuration.orientation = Configuration.ORIENTATION_PORTRAIT;
        }
        onConfigurationChanged(configuration);

        // Spinner Security Question Initialization
        Spinner spinnerSecurityPortrait1 = findViewById(R.id.spinnerSecurityPortrait1);

        questionlist.add("Select Security Question");

        for (int question_id = 1; question_id <= 20; question_id++) {
            questionlist.add(mainActivity.SecQuesAVLTree.search(question_id).getData().getAttributeValue(FirestoreSchema.SecurityQuestionMaster.ASQM_SEC_QUESTION.getAttributeName()).toString());
        }
        spinnerSecurityPortrait1.setSelection(0);

        originalQuestionList = new ArrayList<>(questionlist);
        questionList1 = new ArrayList<>(originalQuestionList);
        questionList2 = new ArrayList<>(originalQuestionList);
        questionList3 = new ArrayList<>(originalQuestionList);

        ArrayAdapter<String> securityAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, R.id.spinner_item_text, questionList1);
        spinnerSecurityPortrait1.setAdapter(securityAdapter1);


        Spinner spinnerSecurityPortrait2 = findViewById(R.id.spinnerSecurityPortrait2);
        spinnerSecurityPortrait2.setSelection(0);
        securityAdapter2 = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, R.id.spinner_item_text, questionList2);
        spinnerSecurityPortrait2.setAdapter(securityAdapter2);


        Spinner spinnerSecurityPortrait3 = findViewById(R.id.spinnerSecurityPortrait3);
        spinnerSecurityPortrait3.setSelection(0);
        securityAdapter3 = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, R.id.spinner_item_text, questionList3);
        spinnerSecurityPortrait3.setAdapter(securityAdapter3);

        Spinner spinnerSecurityLandscape1 = findViewById(R.id.spinnerSecurityLandscape1);
        spinnerSecurityLandscape1.setSelection(0);
        spinnerSecurityLandscape1.setAdapter(securityAdapter1);

        Spinner spinnerSecurityLandscape2 = findViewById(R.id.spinnerSecurityLandscape2);
        spinnerSecurityLandscape2.setSelection(0);
        spinnerSecurityLandscape2.setAdapter(securityAdapter2);

        Spinner spinnerSecurityLandscape3 = findViewById(R.id.spinnerSecurityLandscape3);
        spinnerSecurityLandscape3.setSelection(0);
        spinnerSecurityLandscape3.setAdapter(securityAdapter3);

        // Find the Spinner in your layout
        Spinner spinnerGender = findViewById(R.id.spinnerGender);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayList<CharSequence> genderlist = new ArrayList();
        genderlist.add("Select gender");
        genderlist.add("Male");
        genderlist.add("Female");
        genderlist.add("Others");
        spinnerGender.setSelection(0);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                R.layout.spinner_dropdown_item, R.id.spinner_item_text, genderlist);
        spinnerGender.setAdapter(adapter);
        // Find the Spinner in your layout
        Spinner spinnerGenderLandscape = findViewById(R.id.spinnerGenderLandscape);

        spinnerGender.setSelection(0);
        ArrayAdapter<CharSequence> adapterLandscape = new ArrayAdapter<CharSequence>(this,
                R.layout.spinner_dropdown_item, R.id.spinner_item_text, genderlist);
        spinnerGenderLandscape.setAdapter(adapterLandscape);
        try {
            Log.d("AVL Tree onCreate", mainActivity.userAVLTreeMain.display());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        // Portrait widgets
        // Switches
        Switch switchFirstNationPortrait = (Switch) findViewById(R.id.firstNationPortrait);
        // Spinners
        Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        Spinner spinnerSecurityPortrait1 = (Spinner) findViewById(R.id.spinnerSecurityPortrait1);
        Spinner spinnerSecurityPortrait2 = (Spinner) findViewById(R.id.spinnerSecurityPortrait2);
        Spinner spinnerSecurityPortrait3 = (Spinner) findViewById(R.id.spinnerSecurityPortrait3);

        // TextViews
        TextView lblCreateNewUserPortrait = (TextView) findViewById(R.id.lblCreateNewUserPortrait);
        TextView txtFirstNamePortrait = (TextView) findViewById(R.id.txtFirstNamePortrait);

        TextView txtMiddleNamePortrait = (TextView) findViewById(R.id.txtMiddleNamePortrait);
        TextView txtLastNamePortrait = (TextView) findViewById(R.id.txtLastNamePortrait);
        TextView txtAddress1Portrait = (TextView) findViewById(R.id.txtAddress1Portrait);
        TextView txtAddress2Portrait = (TextView) findViewById(R.id.txtAddress2Portrait);
        TextView txtAddress3Portrait = (TextView) findViewById(R.id.txtAddress3Portrait);
        TextView txtSelectGenderPortrait = (TextView) findViewById(R.id.txtSelectGenderPortrait);
        TextView txtEmailPortrait = (TextView) findViewById(R.id.txtEmailPortrait);
        TextView txtPass1Portrait = (TextView) findViewById(R.id.txtPass1Portrait);
        TextView txtPass2Portrait = (TextView) findViewById(R.id.txtPass2Portrait);
        TextView txtMobileNumberPortrait = (TextView) findViewById(R.id.txtMobileNumberPortrait);
        TextView txtDOBPortrait = (TextView) findViewById(R.id.txtDOBPortrait);
        TextView txtCaptionPortrait = (TextView) findViewById(R.id.txtCaptionPortrait);
        TextView txtSecurityQuestionPortrait = (TextView) findViewById(R.id.txtSecurityQuestionPortrait);

        // Edittexts
        EditText txtBarLastNamePortrait = (EditText) findViewById(R.id.txtBarLastNamePortrait);
        EditText txtBarFirstNamePortrait = (EditText) findViewById(R.id.txtBarFirstNamePortrait);
        EditText txtbarAddressLine1Portrait = (EditText) findViewById(R.id.txtbarAddressLine1Portrait);
        EditText txtBarAddressLine2Portrait = (EditText) findViewById(R.id.txtBarAddressLine2Portrait);
        EditText txtBarAddressLine3Portrait = (EditText) findViewById(R.id.txtBarAddressLine3Portrait);
        EditText txtBarEmailPortrait = (EditText) findViewById(R.id.txtBarEmailPortrait);
        EditText txtBarPass1Portrait = (EditText) findViewById(R.id.txtBarPass1Portrait);
        EditText txtBarPass2Portrait = (EditText) findViewById(R.id.txtBarPass2Portrait);
        EditText txtBarMobileNumberPortrait = (EditText) findViewById(R.id.txtBarMobileNumberPortrait);
        EditText txtBarDOBPortrait = (EditText) findViewById(R.id.txtBarDOBPortrait);
        EditText txtBarCaptionPortrait = (EditText) findViewById(R.id.txtBarCaptionPortrait);
        EditText txtBarMiddleNamePortrait = (EditText) findViewById(R.id.txtBarMiddleNamePortrait);
        EditText txtSec1AnsPortrait = (EditText) findViewById(R.id.txtSec1AnswerPortrait);
        EditText txtSec2AnsPortrait = (EditText) findViewById(R.id.txtSec2AnsPortrait);
        EditText txtSec3AnsPortrait = (EditText) findViewById(R.id.txtSec3AnsPortrait);

        // Buttons
        Button SubmitPortrait = (Button) findViewById(R.id.SubmitPortrait);
        Button CancelPortrait = (Button) findViewById(R.id.CancelPortrait);

        spinnerSecurityPortrait1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestion = questionList1.get(position);

                // Refresh the question lists for spinner 2 and 3
                questionList2.clear();
                questionList2.addAll(originalQuestionList);
                questionList2.remove(selectedQuestion);
                securityAdapter2.notifyDataSetChanged();


                questionList3.clear();
                questionList3.addAll(originalQuestionList);
                questionList3.remove(selectedQuestion);
                securityAdapter3.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerSecurityPortrait2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestion = questionList2.get(position);


                questionList3.clear();
                questionList3.addAll(questionList2);
                questionList3.remove(selectedQuestion);
                securityAdapter3.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        txtBarDOBPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,
                        spinnerGender,
                        spinnerSecurityPortrait1,
                        spinnerSecurityPortrait2,
                        spinnerSecurityPortrait3,
                        txtSec1AnsPortrait,
                        txtSec2AnsPortrait,
                        txtSec3AnsPortrait,
                        txtBarLastNamePortrait,
                        txtBarFirstNamePortrait,
                        txtbarAddressLine1Portrait,
                        txtBarAddressLine2Portrait,
                        txtBarAddressLine3Portrait,
                        txtBarEmailPortrait,
                        txtBarPass1Portrait,
                        txtBarPass2Portrait,
                        txtBarMobileNumberPortrait,
                        txtBarDOBPortrait,
                        txtBarCaptionPortrait,
                        txtBarMiddleNamePortrait,
                        switchFirstNationPortrait);
            }
        });

        CancelPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,
                        spinnerGender,
                        spinnerSecurityPortrait1,
                        spinnerSecurityPortrait2,
                        spinnerSecurityPortrait3,
                        txtSec1AnsPortrait,
                        txtSec2AnsPortrait,
                        txtSec3AnsPortrait,
                        txtBarLastNamePortrait,
                        txtBarFirstNamePortrait,
                        txtbarAddressLine1Portrait,
                        txtBarAddressLine2Portrait,
                        txtBarAddressLine3Portrait,
                        txtBarEmailPortrait,
                        txtBarPass1Portrait,
                        txtBarPass2Portrait,
                        txtBarMobileNumberPortrait,
                        txtBarDOBPortrait,
                        txtBarCaptionPortrait,
                        txtBarMiddleNamePortrait,
                        switchFirstNationPortrait);
            }
        });


        SubmitPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,
                        spinnerGender,
                        spinnerSecurityPortrait1,
                        spinnerSecurityPortrait2,
                        spinnerSecurityPortrait3,
                        txtSec1AnsPortrait,
                        txtSec2AnsPortrait,
                        txtSec3AnsPortrait,
                        txtBarLastNamePortrait,
                        txtBarFirstNamePortrait,
                        txtbarAddressLine1Portrait,
                        txtBarAddressLine2Portrait,
                        txtBarAddressLine3Portrait,
                        txtBarEmailPortrait,
                        txtBarPass1Portrait,
                        txtBarPass2Portrait,
                        txtBarMobileNumberPortrait,
                        txtBarDOBPortrait,
                        txtBarCaptionPortrait,
                        txtBarMiddleNamePortrait,
                        switchFirstNationPortrait);
            }
        });

        // Landscape widgets
        // Switches
        Switch switchFirstNationLandscape = (Switch) findViewById(R.id.firstNationLandscape);
        // Spinners
        Spinner spinnerGenderLandscape = (Spinner) findViewById(R.id.spinnerGenderLandscape);
        Spinner spinnerSecurityLandscape1 = (Spinner) findViewById(R.id.spinnerSecurityLandscape1);
        Spinner spinnerSecurityLandscape2 = (Spinner) findViewById(R.id.spinnerSecurityLandscape2);
        Spinner spinnerSecurityLandscape3 = (Spinner) findViewById(R.id.spinnerSecurityLandscape3);

        // TextViews
        TextView lblCreateNewUserLandscape = (TextView) findViewById(R.id.lblCreateNewUserLandscape);
        TextView txtFirstNamelandscape = (TextView) findViewById(R.id.txtFirstNameLandscape);
        TextView txtMiddleNameLandscape = (TextView) findViewById(R.id.txtMiddleNameLandscape);
        TextView txtLastNameLandscape = (TextView) findViewById(R.id.txtLastNameLandscape);
        TextView txtAddress1Landscape = (TextView) findViewById(R.id.txtAddress1LandScape);
        TextView txtAddress2Landscape = (TextView) findViewById(R.id.txtAddress2Landscape);
        TextView txtAddress3landscape = (TextView) findViewById(R.id.txtAddress3Landscape);
        TextView txtSelectGenderLandscape = (TextView) findViewById(R.id.txtSelectGenderLandscape);
        TextView txtEmailLandscape = (TextView) findViewById(R.id.txtEmailLandscape);
        TextView txtPass1Landscape = (TextView) findViewById(R.id.txtPass1Landscape);
        TextView txtPass2Landscape = (TextView) findViewById(R.id.txtPass2Landscape);
        TextView txtMobileNumberLandscape = (TextView) findViewById(R.id.txtMobileNumberLandscape);
        TextView txtDOBLandscape = (TextView) findViewById(R.id.txtDOBLandscape);
        TextView txtCaptionLandscape = (TextView) findViewById(R.id.txtCaptionLandscape);
        TextView txtSecurityQuestionLandscape = (TextView) findViewById(R.id.txtSecurityQuestionLandscape);

        // Edittexts
        EditText txtBarFirstNameLandscape = (EditText) findViewById(R.id.txtBarFirstNameLandscape);
        EditText txtBarMiddleNameLandscape = (EditText) findViewById(R.id.txtBarMiddleNameLandscape);
        EditText txtBarLastNameLandscape = (EditText) findViewById(R.id.txtBarLastNameLandscape);
        EditText txtbarAddressLine1Landscape = (EditText) findViewById(R.id.txtBarAddressLine1Landscape);
        EditText txtBarAddressLine2Landscape = (EditText) findViewById(R.id.txtBarAddressLine2Landscape);
        EditText txtBarAddressLine3Landscape = (EditText) findViewById(R.id.txtBarAddressLine3Landscape);
        EditText txtBarEmailLandscape = (EditText) findViewById(R.id.txtBarEmailLandscape);
        EditText txtBarPass1Landscape = (EditText) findViewById(R.id.txtBarPass1Landscape);
        EditText txtBarPass2Landscape = (EditText) findViewById(R.id.txtBarPass2Landscape);
        EditText txtBarMobileNumberLandscape = (EditText) findViewById(R.id.txtBarMobileNumberLandscape);
        EditText txtBarDOBLandscape = (EditText) findViewById(R.id.txtBarDOBLandscape);
        EditText txtBarCaptionLandscape = (EditText) findViewById(R.id.txtBarCaptionLandscape);
        EditText txtSec1AnsLandscape = (EditText) findViewById(R.id.txtSec1AnswerLandscape);
        EditText txtSec2AnsLandscape = (EditText) findViewById(R.id.txtSec2AnsLandscape);
        EditText txtSec3AnsLandscape = (EditText) findViewById(R.id.txtSec3AnsLandscape);

        // Buttons
        Button SubmitLandscape = (Button) findViewById(R.id.SubmitLandscape);
        Button CancelLandscape = (Button) findViewById(R.id.CancelLandscape);

        spinnerSecurityLandscape1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestion = questionList1.get(position);

                // Refresh the question lists for spinner 2 and 3
                questionList2.clear();
                questionList2.addAll(originalQuestionList);
                questionList2.remove(selectedQuestion);
                securityAdapter2.notifyDataSetChanged();


                questionList3.clear();
                questionList3.addAll(originalQuestionList);
                questionList3.remove(selectedQuestion);
                securityAdapter3.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerSecurityLandscape2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestion = questionList2.get(position);


                questionList3.clear();
                questionList3.addAll(questionList2);
                questionList3.remove(selectedQuestion);
                securityAdapter3.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        txtBarDOBLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,
                        spinnerGenderLandscape,
                        spinnerSecurityLandscape1,
                        spinnerSecurityLandscape2,
                        spinnerSecurityLandscape3,
                        txtSec1AnsLandscape,
                        txtSec2AnsLandscape,
                        txtSec3AnsLandscape,
                        txtBarLastNameLandscape,
                        txtBarFirstNameLandscape,
                        txtbarAddressLine1Landscape,
                        txtBarAddressLine2Landscape,
                        txtBarAddressLine3Landscape,
                        txtBarEmailLandscape,
                        txtBarPass1Landscape,
                        txtBarPass2Landscape,
                        txtBarMobileNumberLandscape,
                        txtBarDOBLandscape,
                        txtBarCaptionLandscape,
                        txtBarMiddleNameLandscape,
                        switchFirstNationLandscape);
            }
        });
        CancelLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,
                        spinnerGenderLandscape,
                        spinnerSecurityLandscape1,
                        spinnerSecurityLandscape2,
                        spinnerSecurityLandscape3,
                        txtSec1AnsLandscape,
                        txtSec2AnsLandscape,
                        txtSec3AnsLandscape,
                        txtBarLastNameLandscape,
                        txtBarFirstNameLandscape,
                        txtbarAddressLine1Landscape,
                        txtBarAddressLine2Landscape,
                        txtBarAddressLine3Landscape,
                        txtBarEmailLandscape,
                        txtBarPass1Landscape,
                        txtBarPass2Landscape,
                        txtBarMobileNumberLandscape,
                        txtBarDOBLandscape,
                        txtBarCaptionLandscape,
                        txtBarMiddleNameLandscape,
                        switchFirstNationLandscape);
            }
        });

        SubmitLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickFunction(v,
                        spinnerGenderLandscape,
                        spinnerSecurityLandscape1,
                        spinnerSecurityLandscape2,
                        spinnerSecurityLandscape3,
                        txtSec1AnsLandscape,
                        txtSec2AnsLandscape,
                        txtSec3AnsLandscape,
                        txtBarLastNameLandscape,
                        txtBarFirstNameLandscape,
                        txtbarAddressLine1Landscape,
                        txtBarAddressLine2Landscape,
                        txtBarAddressLine3Landscape,
                        txtBarEmailLandscape,
                        txtBarPass1Landscape,
                        txtBarPass2Landscape,
                        txtBarMobileNumberLandscape,
                        txtBarDOBLandscape,
                        txtBarCaptionLandscape,
                        txtBarMiddleNameLandscape,
                        switchFirstNationLandscape);
            }
        });

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the orientation is in landscape, show only the landscape widgets and hide the portrait widgets

            // Show landscape Widgets
            lblCreateNewUserLandscape.setVisibility(View.VISIBLE);
            txtFirstNamelandscape.setVisibility(View.VISIBLE);
            txtBarFirstNameLandscape.setVisibility(View.VISIBLE);
            txtMiddleNameLandscape.setVisibility(View.VISIBLE);
            txtBarMiddleNameLandscape.setVisibility(View.VISIBLE);
            txtLastNameLandscape.setVisibility(View.VISIBLE);
            txtBarLastNameLandscape.setVisibility(View.VISIBLE);
            txtAddress1Landscape.setVisibility(View.VISIBLE);
            txtbarAddressLine1Landscape.setVisibility(View.VISIBLE);
            txtAddress2Landscape.setVisibility(View.VISIBLE);
            txtBarAddressLine2Landscape.setVisibility(View.VISIBLE);
            txtAddress3landscape.setVisibility(View.VISIBLE);
            txtBarAddressLine3Landscape.setVisibility(View.VISIBLE);
            txtSelectGenderLandscape.setVisibility(View.VISIBLE);
            spinnerGenderLandscape.setVisibility(View.VISIBLE);
            spinnerSecurityLandscape1.setVisibility(View.VISIBLE);
            spinnerSecurityLandscape2.setVisibility(View.VISIBLE);
            spinnerSecurityLandscape3.setVisibility(View.VISIBLE);
            txtEmailLandscape.setVisibility(View.VISIBLE);
            txtBarEmailLandscape.setVisibility(View.VISIBLE);
            txtPass1Landscape.setVisibility(View.VISIBLE);
            txtBarPass1Landscape.setVisibility(View.VISIBLE);
            txtPass2Landscape.setVisibility(View.VISIBLE);
            txtBarPass2Landscape.setVisibility(View.VISIBLE);
            txtMobileNumberLandscape.setVisibility(View.VISIBLE);
            txtBarMobileNumberLandscape.setVisibility(View.VISIBLE);
            txtDOBLandscape .setVisibility(View.VISIBLE);
            txtBarDOBLandscape.setVisibility(View.VISIBLE);
            txtCaptionLandscape .setVisibility(View.VISIBLE);
            txtBarCaptionLandscape.setVisibility(View.VISIBLE);
            txtSecurityQuestionLandscape.setVisibility(View.VISIBLE);
            SubmitLandscape .setVisibility(View.VISIBLE);
            CancelLandscape .setVisibility(View.VISIBLE);
            txtbarAddressLine1Landscape.setText(txtbarAddressLine1Portrait.getText());
            txtBarAddressLine2Landscape.setText(txtBarAddressLine2Portrait.getText());
            txtBarAddressLine3Landscape.setText(txtBarAddressLine3Portrait.getText());
            txtBarCaptionLandscape.setText(txtBarCaptionPortrait.getText());
            txtBarDOBLandscape.setText(txtBarDOBPortrait.getText());
            txtBarEmailLandscape.setText(txtBarEmailPortrait.getText());
            txtBarFirstNameLandscape.setText(txtBarFirstNamePortrait.getText());
            txtBarLastNameLandscape.setText(txtBarLastNamePortrait.getText());
            txtBarMiddleNameLandscape.setText(txtBarMiddleNamePortrait.getText());
            txtBarMobileNumberLandscape.setText(txtBarMobileNumberPortrait.getText());
            txtBarPass1Landscape.setText(txtBarPass1Portrait.getText());
            txtBarPass2Landscape.setText(txtBarPass2Portrait.getText());
            switchFirstNationLandscape.setVisibility(View.VISIBLE);
            txtSec1AnsLandscape.setVisibility(View.VISIBLE);
            txtSec2AnsLandscape.setVisibility(View.VISIBLE);
            txtSec3AnsLandscape.setVisibility(View.VISIBLE);
            txtSec1AnsLandscape.setText(txtSec1AnsPortrait.getText());
            txtSec2AnsLandscape.setText(txtSec2AnsPortrait.getText());
            txtSec3AnsLandscape.setText(txtSec3AnsPortrait.getText());


            // Hide Portrait Widgets
            lblCreateNewUserPortrait.setVisibility(View.INVISIBLE);
            txtFirstNamePortrait.setVisibility(View.INVISIBLE);
            txtBarFirstNamePortrait.setVisibility(View.INVISIBLE);
            txtMiddleNamePortrait.setVisibility(View.INVISIBLE);
            txtBarMiddleNamePortrait.setVisibility(View.INVISIBLE);
            txtLastNamePortrait.setVisibility(View.INVISIBLE);
            txtBarLastNamePortrait.setVisibility(View.INVISIBLE);
            txtAddress1Portrait.setVisibility(View.INVISIBLE);
            txtbarAddressLine1Portrait.setVisibility(View.INVISIBLE);
            txtAddress2Portrait.setVisibility(View.INVISIBLE);
            txtBarAddressLine2Portrait.setVisibility(View.INVISIBLE);
            txtAddress3Portrait.setVisibility(View.INVISIBLE);
            txtBarAddressLine3Portrait.setVisibility(View.INVISIBLE);
            txtSelectGenderPortrait.setVisibility(View.INVISIBLE);
            spinnerGender.setVisibility(View.INVISIBLE);
            spinnerSecurityPortrait1.setVisibility(View.INVISIBLE);
            spinnerSecurityPortrait2.setVisibility(View.INVISIBLE);
            spinnerSecurityPortrait3.setVisibility(View.INVISIBLE);
            txtEmailPortrait.setVisibility(View.INVISIBLE);
            txtBarEmailPortrait.setVisibility(View.INVISIBLE);
            txtPass1Portrait.setVisibility(View.INVISIBLE);
            txtBarPass1Portrait.setVisibility(View.INVISIBLE);
            txtPass2Portrait.setVisibility(View.INVISIBLE);
            txtBarPass2Portrait.setVisibility(View.INVISIBLE);
            txtMobileNumberPortrait.setVisibility(View.INVISIBLE);
            txtBarMobileNumberPortrait.setVisibility(View.INVISIBLE);
            txtDOBPortrait .setVisibility(View.INVISIBLE);
            txtBarDOBPortrait.setVisibility(View.INVISIBLE);
            txtCaptionPortrait .setVisibility(View.INVISIBLE);
            txtBarCaptionPortrait.setVisibility(View.INVISIBLE);
            txtSecurityQuestionPortrait.setVisibility(View.INVISIBLE);
            SubmitPortrait.setVisibility(View.INVISIBLE);
            CancelPortrait.setVisibility(View.INVISIBLE);
            switchFirstNationPortrait.setVisibility(View.INVISIBLE);
            txtSec1AnsPortrait.setVisibility(View.INVISIBLE);
            txtSec2AnsPortrait.setVisibility(View.INVISIBLE);
            txtSec3AnsPortrait.setVisibility(View.INVISIBLE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // If the orientation is in portrait, show only the portrait widgets and hide the landscape widgets

            // Hide landscape Widgets
            lblCreateNewUserLandscape.setVisibility(View.INVISIBLE);
            txtFirstNamelandscape.setVisibility(View.INVISIBLE);
            txtBarFirstNameLandscape.setVisibility(View.INVISIBLE);
            txtMiddleNameLandscape.setVisibility(View.INVISIBLE);
            txtBarMiddleNameLandscape.setVisibility(View.INVISIBLE);
            txtLastNameLandscape.setVisibility(View.INVISIBLE);
            txtBarLastNameLandscape.setVisibility(View.INVISIBLE);
            txtAddress1Landscape.setVisibility(View.INVISIBLE);
            txtbarAddressLine1Landscape.setVisibility(View.INVISIBLE);
            txtAddress2Landscape.setVisibility(View.INVISIBLE);
            txtBarAddressLine2Landscape.setVisibility(View.INVISIBLE);
            txtAddress3landscape.setVisibility(View.INVISIBLE);
            txtBarAddressLine3Landscape.setVisibility(View.INVISIBLE);
            txtSelectGenderLandscape.setVisibility(View.INVISIBLE);
            spinnerGenderLandscape.setVisibility(View.INVISIBLE);
            spinnerSecurityLandscape1.setVisibility(View.INVISIBLE);
            spinnerSecurityLandscape2.setVisibility(View.INVISIBLE);
            spinnerSecurityLandscape3.setVisibility(View.INVISIBLE);
            txtEmailLandscape.setVisibility(View.INVISIBLE);
            txtBarEmailLandscape.setVisibility(View.INVISIBLE);
            txtPass1Landscape.setVisibility(View.INVISIBLE);
            txtBarPass1Landscape.setVisibility(View.INVISIBLE);
            txtPass2Landscape.setVisibility(View.INVISIBLE);
            txtBarPass2Landscape.setVisibility(View.INVISIBLE);
            txtMobileNumberLandscape.setVisibility(View.INVISIBLE);
            txtBarMobileNumberLandscape.setVisibility(View.INVISIBLE);
            txtDOBLandscape .setVisibility(View.INVISIBLE);
            txtBarDOBLandscape.setVisibility(View.INVISIBLE);
            txtCaptionLandscape .setVisibility(View.INVISIBLE);
            txtBarCaptionLandscape.setVisibility(View.INVISIBLE);
            txtSecurityQuestionLandscape.setVisibility(View.INVISIBLE);
            SubmitLandscape .setVisibility(View.INVISIBLE);
            CancelLandscape .setVisibility(View.INVISIBLE);
            switchFirstNationLandscape.setVisibility(View.INVISIBLE);
            txtSec1AnsLandscape.setVisibility(View.INVISIBLE);
            txtSec2AnsLandscape.setVisibility(View.INVISIBLE);
            txtSec3AnsLandscape.setVisibility(View.INVISIBLE);

            // Show Portrait Widgets
            lblCreateNewUserPortrait.setVisibility(View.VISIBLE);
            txtFirstNamePortrait.setVisibility(View.VISIBLE);
            txtBarFirstNamePortrait.setVisibility(View.VISIBLE);
            txtMiddleNamePortrait.setVisibility(View.VISIBLE);
            txtBarMiddleNamePortrait.setVisibility(View.VISIBLE);
            txtLastNamePortrait.setVisibility(View.VISIBLE);
            txtBarLastNamePortrait.setVisibility(View.VISIBLE);
            txtAddress1Portrait.setVisibility(View.VISIBLE);
            txtbarAddressLine1Portrait.setVisibility(View.VISIBLE);
            txtAddress2Portrait.setVisibility(View.VISIBLE);
            txtBarAddressLine2Portrait.setVisibility(View.VISIBLE);
            txtAddress3Portrait.setVisibility(View.VISIBLE);
            txtBarAddressLine3Portrait.setVisibility(View.VISIBLE);
            txtSelectGenderPortrait.setVisibility(View.VISIBLE);
            spinnerGender.setVisibility(View.VISIBLE);
            spinnerSecurityPortrait1.setVisibility(View.VISIBLE);
            spinnerSecurityPortrait2.setVisibility(View.VISIBLE);
            spinnerSecurityPortrait3.setVisibility(View.VISIBLE);
            txtEmailPortrait.setVisibility(View.VISIBLE);
            txtBarEmailPortrait.setVisibility(View.VISIBLE);
            txtPass1Portrait.setVisibility(View.VISIBLE);
            txtBarPass1Portrait.setVisibility(View.VISIBLE);
            txtPass2Portrait.setVisibility(View.VISIBLE);
            txtBarPass2Portrait.setVisibility(View.VISIBLE);
            txtMobileNumberPortrait.setVisibility(View.VISIBLE);
            txtBarMobileNumberPortrait.setVisibility(View.VISIBLE);
            txtDOBPortrait .setVisibility(View.VISIBLE);
            txtBarDOBPortrait.setVisibility(View.VISIBLE);
            txtCaptionPortrait .setVisibility(View.VISIBLE);
            txtBarCaptionPortrait.setVisibility(View.VISIBLE);
            txtSecurityQuestionPortrait.setVisibility(View.VISIBLE);
            SubmitPortrait.setVisibility(View.VISIBLE);
            CancelPortrait.setVisibility(View.VISIBLE);
            txtbarAddressLine1Portrait.setText(txtbarAddressLine1Landscape.getText());
            txtBarAddressLine2Portrait.setText(txtBarAddressLine2Landscape.getText());
            txtBarAddressLine3Portrait.setText(txtBarAddressLine3Landscape.getText());
            txtBarCaptionPortrait.setText(txtBarCaptionLandscape.getText());
            txtBarDOBPortrait.setText(txtBarDOBLandscape.getText());
            txtBarEmailPortrait.setText(txtBarEmailLandscape.getText());
            txtBarFirstNamePortrait.setText(txtBarFirstNameLandscape.getText());
            txtBarLastNamePortrait.setText(txtBarLastNameLandscape.getText());
            txtBarMiddleNamePortrait.setText(txtBarMiddleNameLandscape.getText());
            txtBarMobileNumberPortrait.setText(txtBarMobileNumberLandscape.getText());
            txtBarPass1Portrait.setText(txtBarPass1Landscape.getText());
            txtBarPass2Portrait.setText(txtBarPass2Landscape.getText());
            switchFirstNationPortrait.setVisibility(View.VISIBLE);
            txtSec1AnsPortrait.setVisibility(View.VISIBLE);
            txtSec2AnsPortrait.setVisibility(View.VISIBLE);
            txtSec3AnsPortrait.setVisibility(View.VISIBLE);
            txtSec1AnsPortrait.setText(txtSec1AnsLandscape.getText());
            txtSec2AnsPortrait.setText(txtSec2AnsLandscape.getText());
            txtSec3AnsPortrait.setText(txtSec3AnsLandscape.getText());
        }
    }

    /**
     * This function is used to handle the button click events
     * @param viewid: The view id of the button clicked
     * @param spinnerGender: The spinner
     * @param txtBarLastName: The last name edit text
     * @param txtBarFirstName: The first name edit text
     * @param txtbarAddressLine1: The address line 1 edit text
     * @param txtBarAddressLine2: The address line 2 edit text
     * @param txtBarAddressLine3: The address line 3 edit text
     * @param txtBarEmail: The email edit text
     * @param txtBarPass1: The password edit text
     * @param txtBarPass2: The re-enter password edit text
     * @param txtBarMobileNumber: The mobile number edit text
     * @param txtBarDOB: The date of birth edit text
     * @param txtBarCaption: The caption edit text
     * @param txtBarMiddleName: The middle name edit text
     */
    public void buttonClickFunction(View viewid,
                                    Spinner spinnerGender,
                                    Spinner spinnerSecurityPortrait1,
                                    Spinner spinnerSecurityPortrait2,
                                    Spinner spinnerSecurityPortrait3,
                                    EditText txtSec1Ans,
                                    EditText txtSec2Ans,
                                    EditText txtSec3Ans,
                                    EditText txtBarLastName,
                                    EditText txtBarFirstName,
                                    EditText txtbarAddressLine1,
                                    EditText txtBarAddressLine2,
                                    EditText txtBarAddressLine3,
                                    EditText txtBarEmail,
                                    EditText txtBarPass1,
                                    EditText txtBarPass2,
                                    EditText txtBarMobileNumber,
                                    EditText txtBarDOB,
                                    EditText txtBarCaption,
                                    EditText txtBarMiddleName,
                                    Switch switchFirstNation) {
        // Cancel Button handling
        if (viewid.getId() == R.id.CancelLandscape || viewid.getId() == R.id.CancelPortrait){
            Intent intent = new Intent(CreateNewUser.this, MainActivity.class);
            startActivity(intent);
        }
        // Submit Button handling
        else if (viewid.getId() == R.id.SubmitLandscape || viewid.getId() == R.id.SubmitPortrait){
            if (txtBarFirstName.getText().toString().isEmpty() || txtBarFirstName.getText().toString().equals(" ")){
                txtBarFirstName.setError("First Name is required");
                Toast.makeText(CreateNewUser.this, "First Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (txtbarAddressLine1.getText().toString().isEmpty() || txtbarAddressLine1.getText().toString().equals(" ")){
                txtbarAddressLine1.setError("Address 1 is required");
                Toast.makeText(CreateNewUser.this, "Address 1 is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerGender.getSelectedItem().toString().isEmpty() || spinnerGender.getSelectedItem().toString().equals("Select gender")){
                Toast.makeText(CreateNewUser.this, "Select your preferred gender", Toast.LENGTH_SHORT).show();
                return;
            }
            if(spinnerSecurityPortrait1.getSelectedItem().toString().isEmpty() || spinnerSecurityPortrait1.getSelectedItem().toString().equals("Select Security Question")){
                Toast.makeText(CreateNewUser.this, "Select a security question", Toast.LENGTH_SHORT).show();
                return;
            }
            if(spinnerSecurityPortrait2.getSelectedItem().toString().isEmpty() || spinnerSecurityPortrait2.getSelectedItem().toString().equals("Select Security Question")){
                Toast.makeText(CreateNewUser.this, "Select a security question", Toast.LENGTH_SHORT).show();
                return;
            }
            if(spinnerSecurityPortrait3.getSelectedItem().toString().isEmpty() || spinnerSecurityPortrait3.getSelectedItem().toString().equals("Select Security Question")){
                Toast.makeText(CreateNewUser.this, "Select a security question", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtBarDOB.getText().toString().isEmpty() || txtBarDOB.getText().toString().equals(" ")){
                txtBarDOB.setError("Date of Birth is required");
                Toast.makeText(CreateNewUser.this, "Date of Birth is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtBarPass1.getText().toString().isEmpty() || txtBarPass1.getText().toString().equals(" ")){
                txtBarPass1.setError("Password is required");
                Toast.makeText(CreateNewUser.this, "Password is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtBarPass2.getText().toString().isEmpty() || txtBarPass2.getText().toString().equals(" ")){
                txtBarPass1.setError("Re-Enter Password is required");
                Toast.makeText(CreateNewUser.this, "Re-Enter Password is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!txtBarPass1.getText().toString().equals(txtBarPass2.getText().toString())){
                txtBarPass1.setError("Passwords do not match");
                txtBarPass2.setError("Passwords do not match");
                Toast.makeText(CreateNewUser.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            String defaultCaption = "";
            if (txtBarCaption.getText().toString().isEmpty() || txtBarCaption.getText().toString().equals(" ")){
                defaultCaption = "Hello Everyone! I am new here!";
            }else {
                defaultCaption = txtBarCaption.getText().toString();
            }

            String encryptedpassword = passwordEncryption.encryptPassword(txtBarPass1.getText().toString());

            if (txtSec1Ans.getText().toString().isEmpty() || txtSec1Ans.getText().toString().equals(" ")){
                txtSec1Ans.setError("Answer for security Question 1 is required");
                Toast.makeText(CreateNewUser.this, "Answer for security Question 1 is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtSec2Ans.getText().toString().isEmpty() || txtSec2Ans.getText().toString().equals(" ")){
                txtSec2Ans.setError("Answer for security Question 1 is required");
                Toast.makeText(CreateNewUser.this, "Answer for security Question 1 is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (txtSec3Ans.getText().toString().isEmpty() || txtSec3Ans.getText().toString().equals(" ")){
                txtSec3Ans.setError("Answer for security Question 1 is required");
                Toast.makeText(CreateNewUser.this, "Answer for security Question 1 is required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isSwitchOn = switchFirstNation.isChecked();
            String firstNation = "";
            if (isSwitchOn) {
                firstNation = "Y";
            } else {
                firstNation = "N";
            }

            try {
                Log.d("AVL Tree before insert", mainActivity.userAVLTreeMain.display());
            } catch (Exception e) {
                e.printStackTrace();
            }

            AVLNode maxAVLNode = mainActivity.userAVLTreeMain.maxDataNode(mainActivity.userAVLTreeMain.getRoot());
            String MaxUserId = maxAVLNode.getData().getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_USER_ID.getAttributeName()).toString();
            String newUserId = "ANCW" + Integer.toString(Integer.parseInt(MaxUserId.substring(4,MaxUserId.length())) + 1);

            // Example 1
            mainActivity.userAVLTreeMain.insert(newUserId, mainActivity.userAVLTreeMain.createNodeData(
                    FirestoreSchema.FireStoreCollection.ANCW_LOGIN_USER_MASTER,
                    firstNation,
                    txtbarAddressLine1.getText().toString(),
                    txtBarAddressLine2.getText().toString(),
                    txtBarAddressLine3.getText().toString(),
                    defaultCaption,
                    txtBarEmail.getText().toString(),
                    txtBarFirstName.getText().toString(),
                    spinnerGender.getSelectedItem().toString(),
                    txtBarLastName.getText().toString(),
                    txtBarDOB.getText().toString(),
                    newUserId,
                    "I",
                    txtBarMiddleName.getText().toString(),
                    txtBarMobileNumber.getText().toString(),
                    encryptedpassword,
                    encryptedpassword,
                    "N",
                    "U",
                    newUserId
            ));

            int UserSecid1 = 0;
            int UserSecid2 = 0;
            int UserSecid3 = 0;

            for (String question: originalQuestionList){
                if(question.equals(spinnerSecurityPortrait1.getSelectedItem().toString())){
                    UserSecid1 = originalQuestionList.indexOf(question);
                }
                if(question.equals(spinnerSecurityPortrait2.getSelectedItem().toString())){
                    UserSecid2 = originalQuestionList.indexOf(question);
                }
                if(question.equals(spinnerSecurityPortrait3.getSelectedItem().toString())){
                    UserSecid3 = originalQuestionList.indexOf(question);
                }
            }

            mainActivity.userSeqAVLTreeMain.insert(newUserId, mainActivity.userSeqAVLTreeMain.createNodeData(
                    FirestoreSchema.FireStoreCollection.ANCW_USER_SECURITY_QUESTION,
                    txtSec1Ans.getText().toString(),
                    UserSecid1,
                    txtSec2Ans.getText().toString(),
                    UserSecid2,
                    txtSec3Ans.getText().toString(),
                    UserSecid3,
                    newUserId
            ));

            try {
                Log.d("AVL Tree after Insert", mainActivity.userAVLTreeMain.display());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(TAG, mainActivity.userAVLTreeMain.serializeToJson().toString());

                firebaseService.writeJSONFile(CreateNewUser.this,
                        firebase_filenames.user_login_master_json.getComponent_name(),
                        firebase_filenames.user_login_master_csv.getComponent_name(),
                        mainActivity.userAVLTreeMain.serializeToJson());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error while creating JSON file", e);
            }

            try {

                Log.d(TAG, mainActivity.userAVLTreeMain.serializeToJson().toString());

                firebaseService.writeJSONFile(CreateNewUser.this,
                        firebase_filenames.user_security_questions_json.getComponent_name(),
                        firebase_filenames.user_security_questions_csv.getComponent_name(),
                        mainActivity.userSeqAVLTreeMain.serializeToJson());

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

            firebaseService.firebase_connector(getApplicationContext(),
                    firebase_filenames.user_security_questions_json.getComponent_name(),
                    firebase_backend_Components.user_security_questions.getComponent_name(),
                    firebase_primary_key_attributes.user_security_questions.getComponent_name(),
                    firebaseService.write_data_into_db,
                    false,
                    "",
                    "");

            String message = "User added successfully. Your login ID is " + newUserId + ".";
            new AlertDialog.Builder(CreateNewUser.this)
                    .setTitle("User Created")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with any other actions you want to perform after the user acknowledges the dialog
                            Intent intent = new Intent(CreateNewUser.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        // Date of Birth handling
        else if (viewid.getId() == R.id.txtBarDOBPortrait || viewid.getId() == R.id.txtBarDOBLandscape){
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(CreateNewUser.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String day = String.valueOf(dayOfMonth);
                            String month = String.valueOf(monthOfYear + 1);
                            txtBarDOB.setText(year + "-" + (month.length() == 1? "0" + month : month) + "-" + ((day.length() == 1)? "0" + day : day));
                        }
                    }, year, month, day);
            picker.show();
        }
    }
}
