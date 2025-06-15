package com.example.g11_group_application.UI_Layer;

/**
 * @Author: Omair Soomro (u7517790)
 * @Edited: Saksham Gupta (u7726995), Aditya Iyengar (u7670692) and Divyesh Srivastava (u7726856)
 * Created: 15-May-2024
 * Comments: This is the main page after login to the application
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.User;
import com.example.g11_group_application.UI_Layer.ui.dashboard.DashboardFragment;
import com.example.g11_group_application.UI_Layer.ui.home.HomeFragment;
import com.example.g11_group_application.databinding.ActivityAppMainPageBinding;
import com.example.g11_group_application.UI_Layer.SearchActivity;
import com.example.g11_group_application.firebase_connection_DAO.FirestoreSchema;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AppMainPage extends AppCompatActivity {

    DatabaseReference unreadMessagesRef;
    private ActivityAppMainPageBinding binding;
    User currentUser;
    private String currentUserId;

    DatabaseReference userStatusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView textView = findViewById(R.id.textView_HomeName);
        Intent get_intent = getIntent();
        currentUserId = get_intent.getStringExtra("userID");
        if(currentUserId!=null){
            if (MainActivity.userAVLTreeMain.find(currentUserId)) {
                String userName = (String) MainActivity.userAVLTreeMain.search(currentUserId).getData().getAttributeValue(FirestoreSchema.LoginUserMaster.ALUM_FIRST_NAME.getAttributeName());
                textView.setText("Welcome " + userName);
                unreadMessagesRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId).child("unreadMessages");
                checkForUnreadMessages();
            }
        }
        setupBottomNavigation();
    }

    /**
     * Sets up the bottom navigation buttons and their click listeners.
     */
    private void setupBottomNavigation() {
        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton dashboardButton = findViewById(R.id.button_dashboard);
        ImageButton searchButton = findViewById(R.id.button_search);

        searchButton.setOnClickListener(v -> {
            Intent get_intent = getIntent();
            String username = get_intent.getStringExtra("userID");
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("username", username); // replace "key" and "value" with your actual key and value
            startActivity(intent);
        });
        // Get the full string from resources
        String fullText = getResources().getString(R.string.src_ancestra_desc);

        // Create a SpannableString from the full text
        SpannableString spannableString = new SpannableString(fullText);

        // Find the start and end indices of "Welcome to AncestraWell" in the full text
        int start = fullText.indexOf("Welcome to AncestraWell!");
        int end = start + "Welcome to AncestraWell".length();

        // Create a new AlignmentSpan
        AlignmentSpan alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);

        // Apply the AlignmentSpan to the part of the text
        spannableString.setSpan(alignmentSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // Set the SpannableString to the TextView
        TextView textView = findViewById(R.id.txt_description);
        textView.setText(spannableString);
        homeButton.setOnClickListener(v -> {
            Intent get_intent = getIntent();
            String username = get_intent.getStringExtra("userID");
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra("username", username); // replace "key" and "value" with your actual key and value
            startActivity(intent);
        });
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User wants to log out
                        Intent intent = new Intent(AppMainPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus("online");
    }

    /**
     * Updates the user's status to online or offline.
     * @param status The status to set (e.g., "online" or "offline").
     */
    private void updateUserStatus(String status) {
        System.out.println(currentUserId);
        userStatusRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId).child("status");

        Map<String, Object> statusUpdates = new HashMap<>();
        statusUpdates.put("status", status);
        statusUpdates.put("lastOnline", System.currentTimeMillis());

        userStatusRef.onDisconnect().setValue("offline"); // Automatically set to offline when disconnected
        userStatusRef.updateChildren(statusUpdates);
    }

    /**
     * Checks for unread messages in the Firebase database for current user.
     */
    private void checkForUnreadMessages() {

        unreadMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int unreadCount = (int) dataSnapshot.getChildrenCount();
                    System.out.println("unreadCount: " + unreadCount);
                    if (unreadCount > 0) {
                        showNewMessageNotification(unreadCount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read unread messages", databaseError.toException());
            }
        });
    }

    /**
     * Displays a toast and notification text if there are new unread messages.
     * @param unreadCount The number of unread messages.
     */
    private void showNewMessageNotification(int unreadCount) {
        Toast.makeText(this, "You have " + unreadCount + " new messages.", Toast.LENGTH_LONG).show();
        TextView notificationText = findViewById(R.id.viewNotifications);
        notificationText.setVisibility(View.VISIBLE);
        notificationText.setText("You have new messages! Tap here to read.");
        notificationText.setOnClickListener(v ->
                {
                    Intent intent = new Intent(AppMainPage.this, NotificationActivity.class);
                    intent.putExtra("username",currentUserId);
                    startActivity(intent);
                }
        );
    }
    @Override
    protected void onPause() {
        super.onPause();
        updateUserStatus("offline");
    }
}
