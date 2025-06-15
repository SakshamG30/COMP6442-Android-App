package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * Created: 08-May-2024
 * Comments: This code is responsible for the Profile Page of the application.
 *
 */
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.*;
import com.example.g11_group_application.firebase_connection_DAO.*;
import com.google.firebase.database.collection.BuildConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ProfilePage extends AppCompatActivity {
    String TAG = "ProfilePage";
    String username;
    MainActivity mainActivity = new MainActivity();
    firebase_service_layer firebaseService = new firebase_service_layer();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference userImageRef;
    ImageView profileImageView;
    private Bitmap tempImageBitmap = null;
    private Uri photoURI;

    NodeData usersecquData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileImageView = findViewById(R.id.imgProfilePhotoPortrait);
        profileImageView.setOnClickListener(v -> selectImage());

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.d(TAG, "Username: " + username);
        fetchImage();
        Configuration configuration = new Configuration();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            configuration.orientation = Configuration.ORIENTATION_PORTRAIT;
        }
        onConfigurationChanged(configuration);

    }

    private void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            userImageRef = storageRef.child("images/" + username + ".jpg");
            UploadTask uploadTask = userImageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                Log.e(TAG, "Upload failed: " + exception.getMessage());
            }).addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Image upload successful");
            });
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = Math.min(width, height);
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (width - min) / 2, (height - min) / 2, min, min);

        Bitmap output = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, min, min);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(croppedBitmap, rect, rect, paint);

        return output;
    }

    private void fetchImage() {
        userImageRef = storageRef.child("images/" + username + ".jpg");
        userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap circularBitmap = getCircularBitmap(resource);
                            profileImageView.setImageBitmap(circularBitmap);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error fetching image: " + exception.getMessage());
            Toast.makeText(ProfilePage.this, "Error fetching image: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


    @SuppressLint("RestrictedApi")
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePage.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Photo file creation failed", ex);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 0);
                    }
                }
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap selectedImage = null;
            try {
                if (requestCode == 1) { // From gallery
                    Uri selectedImageUri = data.getData();
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } else if (requestCode == 0) { // From camera
                    Bundle extras = data.getExtras();
                    selectedImage = (Bitmap) extras.get("data");
                }
                if (selectedImage != null) {
                    Bitmap circularBitmap = getCircularBitmap(selectedImage);
                    profileImageView.setImageBitmap(circularBitmap);
                    tempImageBitmap = circularBitmap; // Store the circular image temporarily
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        tempImageBitmap = null; // Clear the temporary image
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        NodeData nodeData = mainActivity.userAVLTreeMain.search(username).getData();
        String firstName = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_FIRST_NAME.getAttributeName());
        String middleName = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_MIDDLE_NAME.getAttributeName());
        String lastName = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_LAST_NAME.getAttributeName());
        String MobileNumber = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_MOBILE_NUMBER.getAttributeName());
        String EmailAddress = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_EMAIL_ADDRESS.getAttributeName());
        String Address1 = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_ADDRESS_1.getAttributeName());
        String Address2 = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_ADDRESS_2.getAttributeName());
        String Address3 = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_ADDRESS_3.getAttributeName());
        String DOB = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_DOB.getAttributeName());
        String gender = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_GENDER.getAttributeName());

        TextView txtNamePortrait = findViewById(R.id.lblProfileNameDisplayPortrait);
        EditText txtCaption = findViewById(R.id.txtProfileCaptionPortrait);
        EditText txtMobilePortrait = findViewById(R.id.txtProfileMobilePhonePortrait);
        EditText txtEmailPortrait = findViewById(R.id.txtProfileEmailAddressPortrait);
        ListView listView = findViewById(R.id.lstProfileLocationPortrait);
        EditText txtGenderPortrait = findViewById(R.id.txtProfileGenderPortrait);
        TextView txtLoginIDPortrait = findViewById(R.id.lblLoginUserIdView);
        EditText txtDOBPortrait = findViewById(R.id.txtProfileDOBPortrait);
        Button cancelButton = findViewById(R.id.cmdProfileCancelPortrait);
        Button saveButton = findViewById(R.id.cmdProfileSavePortrait);
        Button changePasswordButton = findViewById(R.id.cmdProfileChangePasswordPortrait);

        if (middleName.isEmpty() || middleName.equals(" ")){
            txtNamePortrait.setText(firstName + " " + lastName);
        } else {
            txtNamePortrait.setText(firstName + " " + middleName + " " + lastName);
        }

        txtCaption.setText((String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_CAPTION.getAttributeName()));

        txtMobilePortrait.setText(MobileNumber);
        txtEmailPortrait.setText(EmailAddress);
        txtGenderPortrait.setText(gender);
        txtLoginIDPortrait.setText(username);
        txtDOBPortrait.setText(DOB);

        // Sample data for the ListView
        List<String> locationList = new ArrayList<>();
        locationList.add(Address1);
        locationList.add(Address2);
        locationList.add(Address3);

        // Set up the ListView

        CustomAdapter adapter = new CustomAdapter(this, locationList);
        listView.setAdapter(adapter);

        txtDOBPortrait.setOnClickListener(v -> buttonClickFunction(v,
                txtDOBPortrait,
                txtCaption,
                txtMobilePortrait,
                txtEmailPortrait,
                listView,
                txtGenderPortrait));

        cancelButton.setOnClickListener(v -> buttonClickFunction(v,
                txtDOBPortrait,
                txtCaption,
                txtMobilePortrait,
                txtEmailPortrait,
                listView,
                txtGenderPortrait));
        saveButton.setOnClickListener(v -> buttonClickFunction(v,
                txtDOBPortrait,
                txtCaption,
                txtMobilePortrait,
                txtEmailPortrait,
                listView,
                txtGenderPortrait));
        changePasswordButton.setOnClickListener(v -> buttonClickFunction(v,
                txtDOBPortrait,
                txtCaption,
                txtMobilePortrait,
                txtEmailPortrait,
                listView,
                txtGenderPortrait));
    }

    public void buttonClickFunction(View viewid,
                                    EditText txtDOB,
                                    EditText txtCaption,
                                    EditText txtMobile,
                                    EditText txtEmail,
                                    ListView listView,
                                    EditText txtGender) {
        if (viewid.getId() == R.id.txtProfileDOBPortrait) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(ProfilePage.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String day = String.valueOf(dayOfMonth);
                            String month = String.valueOf(monthOfYear + 1);
                            txtDOB.setText(year + "-" + (month.length() == 1? "0" + month : month) + "-" + ((day.length() == 1)? "0" + day : day));
                        }
                    }, year, month, day);
            picker.show();
        } else if (viewid.getId() == R.id.cmdProfileCancelPortrait){
            tempImageBitmap = null; // Clear the temporary image
            Intent intent = new Intent(this, AppMainPage.class);
            intent.putExtra("userID", username);
            startActivity(intent);
        } else if (viewid.getId() == R.id.cmdProfileSavePortrait){
            // Save the data
            NodeData newnodeData = mainActivity.userAVLTreeMain.search(username).getData();
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_CAPTION.getAttributeName(), txtCaption.getText().toString());
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_DOB.getAttributeName(),txtDOB.getText().toString());
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_EMAIL_ADDRESS.getAttributeName(), txtEmail.getText().toString());
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_MOBILE_NUMBER.getAttributeName(), txtMobile.getText().toString());
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_GENDER.getAttributeName(), txtGender.getText().toString());
            String AddressLine1 = listView.getAdapter().getItem(0).toString();
            String AddressLine2 = listView.getAdapter().getItem(1).toString();
            String AddressLine3 = listView.getAdapter().getItem(2).toString();
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_ADDRESS_1.getAttributeName(), AddressLine1);
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_ADDRESS_2.getAttributeName(), AddressLine2);
            newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_ADDRESS_3.getAttributeName(), AddressLine3);

            if (tempImageBitmap != null) {
                uploadImage(tempImageBitmap); // Upload the image
                newnodeData.addAttribute(FirestoreSchema.LoginUserMaster.ALUM_PHOTO_ADDED_FLAG.getAttributeName(), "Y");
            }

            mainActivity.userAVLTreeMain.updateNodeData(username, newnodeData);

            try {

                Log.d(TAG, mainActivity.userAVLTreeMain.serializeToJson().toString());

                firebaseService.writeJSONFile(ProfilePage.this,
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

            String message = "Your profile has been updated successfully.";
            new AlertDialog.Builder(ProfilePage.this)
                    .setTitle("User Created")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with any other actions you want to perform after the user acknowledges the dialog
                            Intent intent = new Intent(ProfilePage.this, AppMainPage.class);
                            intent.putExtra("userID", username);
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } else if (viewid.getId() == R.id.cmdProfileChangePasswordPortrait){
            mainActivity.mainActivityFlag = false;
            String userID = username;
            NodeData nodeData = mainActivity.userAVLTreeMain.search(userID).getData();
            if(mainActivity.userSeqAVLTreeMain.find(userID)){
                usersecquData = mainActivity.userSeqAVLTreeMain.search(userID).getData();
            }
            else{
                Log.e(TAG, "No security questions for userID " + userID);
                return;
            }
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
            NodeData secquData = mainActivity.SecQuesAVLTree.search(secidint).getData();
            String SeqQues = secquData.getAttributeValue(FirestoreSchema.SecurityQuestionMaster.ASQM_SEC_QUESTION.getAttributeName()).toString();

            String oldPwd = (String) nodeData.getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_OLD_PASSWORD.getAttributeName());
            Intent intent = new Intent(ProfilePage.this, ForgotPasswordActivity.class);
            intent.putExtra("oldPwd", oldPwd);
            intent.putExtra("userID", userID);
            intent.putExtra("SeqQues", SeqQues);
            intent.putExtra("sec_id_answer", sec_id_answer);
            startActivity(intent);
        }
    }
}