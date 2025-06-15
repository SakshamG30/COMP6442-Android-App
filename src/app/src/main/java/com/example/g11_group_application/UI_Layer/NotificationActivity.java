package com.example.g11_group_application.UI_Layer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.UnreadMessageDetails;
import com.example.g11_group_application.Service_layer.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 15-May-2024
 * Represents a notification activity that displays unread messages for a user and allows
 * navigation to the chat activity upon clicking a notification.
 */
public class NotificationActivity extends AppCompatActivity {

    DatabaseReference unreadMessagesRef;  // Reference to the Firebase database location for unread messages
    private String currentUserId;
    private ImageButton backBtn;
    String TAG = "NotificationActivity";
    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        currentUserId = getIntent().getStringExtra("username");
        unreadMessagesRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId).child("unreadMessages");

        checkForUnreadMessages();

        backBtn = findViewById(R.id.back_arrow_notif);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, AppMainPage.class);
                intent.putExtra("userID", currentUserId);
                startActivity(intent);
            }
        });
    }

    /**
     * Checks for unread messages for the current user and displays notifications if any are found.
     */
    private void checkForUnreadMessages() {

        unreadMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int unreadCount = (int) dataSnapshot.getChildrenCount();
                    if (unreadCount > 0) {
                        showNotification(unreadCount, dataSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read unread messages", databaseError.toException());
            }
        });
    }

    /**
     * Navigates to the chat activity with the specified user.
     *
     * @param currentUserId The ID of the current user.
     * @param chatUser      The user to chat with.
     */
    private void movetoChatActivity(String currentUserId, User chatUser) {
        Intent chatIntent = new Intent(NotificationActivity.this, ChatRoom.class);
        chatIntent.putExtra("username",currentUserId);
        chatIntent.putExtra("ChatUser",chatUser);
        startActivity(chatIntent);
    }

    /**
     * Displays a notification for unread messages, grouping them by sender.
     *
     * @param unreadCount   The number of unread messages.
     * @param dataSnapshot  The data snapshot containing unread message details.
     */
    private void showNotification(int unreadCount, DataSnapshot dataSnapshot) {
        //Toast.makeText(this, "You have " + unreadCount + " new messages.", Toast.LENGTH_SHORT).show();
        Map<String, List<UnreadMessageDetails>> messagesBySender = new HashMap<>();

        // Group messages by sender
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            UnreadMessageDetails details = childSnapshot.getValue(UnreadMessageDetails.class);
            if (details != null) {
                List<UnreadMessageDetails> messages = messagesBySender.getOrDefault(details.getSenderName(), new ArrayList<>());
                messages.add(details);
                messagesBySender.put(details.getSenderName(), messages);
            }
        }


        // Prepare a list of formatted strings for the ListView
        List<String> formattedSenderNames = new ArrayList<>();
        for (String senderName : messagesBySender.keySet()) {
            formattedSenderNames.add("Messages from " + senderName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, formattedSenderNames);
        ListView listView = findViewById(R.id.lstNotificationView);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);

        // Set onClickListener for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String senderName = formattedSenderNames.get(position).substring(14); //Removing 'Messages from'

                // Get the unread message details for the sender
                List<UnreadMessageDetails> details = messagesBySender.get(senderName);

                // Clear unread messages from that specific sender
                for (UnreadMessageDetails detail : details) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                            .child(currentUserId).child("unreadMessages").child(detail.getMessageId());
                    ref.removeValue();
                }
                User chatUser = mainActivity.userAVLTreeMain.getUser(details.get(0).getSenderId());
                movetoChatActivity(currentUserId, chatUser);
            }
        });
    }
}