package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Aditya Iyengar (u7670692)
 * Created: 15-May-2024
 * Comments: This is the Main Chat Activity Java class for the chatroom
 */
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.g11_group_application.R;
import com.example.g11_group_application.Service_layer.Message;
import com.example.g11_group_application.Service_layer.UnreadMessageDetails;
import com.example.g11_group_application.Service_layer.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ChatRoom extends AppCompatActivity {
    private FirebaseAuth mAuth;
    MainActivity mainActivity = new MainActivity();
    private DatabaseReference MessageRef;
    private String currentUserId, currentUserName;
    private User chatUser, currentUser;
    private String currentChatKey;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    int lastMessageNo = 0;
    private RecyclerView messagesRecyclerView;
    private ImageButton backBtn;
    private MessagesAdapter adapter;
    private List<com.example.g11_group_application.Service_layer.Message> messages = new ArrayList<>();

    private String TAG = "ChatRoom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Log.d(TAG, "Reached ChatRoom");

        // Set user ID and chat key for testing
        currentUserId = getIntent().getStringExtra("username");

        // Fetch current user from AVL Tree
        if(mainActivity.userAVLTreeMain.find(currentUserId)){
            currentUser = mainActivity.userAVLTreeMain.getUser(currentUserId);
        }
        else{
            Log.e(TAG, "Can't find userID " + currentUserId + " in AVL Tree");
            return;
        }

        currentUserName = currentUser.getUserName();
        Log.d(TAG, "Current User Id: " + currentUserId);

        // Fetch chat user from intent extras
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            chatUser = getIntent().getExtras().getSerializable("ChatUser",User.class);
        }

        Log.d(TAG, "Chatting with UserID: " + chatUser.getId());
        String key = null;

        //To generate unique chat key
        if(currentUserId.compareTo(chatUser.getId())>=0){
            key = currentUserId + "-" + chatUser.getId();
        }
        else{
            key = chatUser.getId() + "-" + currentUserId;
        }
        currentChatKey = key;  // This should be a unique key for each chat session

        // Setup database references

        MessageRef = FirebaseDatabase.getInstance().getReference().child("PrivateChats").child(currentChatKey).child("messages");
        Log.d(TAG, "Set up reference to Private Chat");

        setupViews();
        setupListeners();

        adapter = new MessagesAdapter(messages, currentUserName);
        messagesRecyclerView.setAdapter(adapter);
    }

    /**
     * Initializes the views for the ChatRoom activity.
     */
    private void setupViews() {
        SendMessageButton = findViewById(R.id.send_chat_message_button);
        userMessageInput = findViewById(R.id.input_chat_message);
        mScrollView = findViewById(R.id.my_scroll_view);

        TextView textTitle = findViewById(R.id.text_chat_title);
        textTitle.setText(chatUser.getUserName());

        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * Sets up listeners for buttons and Firebase database changes.
     */
    private void setupListeners() {
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();
                userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        backBtn = findViewById(R.id.back_arrow);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoom.this, SearchActivity.class);
                intent.putExtra("username", currentUserId);
                startActivity(intent);
            }
        });
        MessageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                com.example.g11_group_application.Service_layer.Message message = dataSnapshot.getValue(com.example.g11_group_application.Service_layer.Message.class);
                if (message != null) {
                    lastMessageNo++;
                    messages.add(message);
                    Collections.sort(messages);
                    adapter.notifyItemInserted(messages.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                com.example.g11_group_application.Service_layer.Message message = dataSnapshot.getValue(com.example.g11_group_application.Service_layer.Message.class);
                if (message != null) {
                    int indexToUpdate = -1;
                    for (int i = 0; i < messages.size(); i++) {
                        if (messages.get(i).getKey().equals(message.getKey())) {
                            indexToUpdate = i;
                            break;
                        }
                    }
                    if (indexToUpdate != -1) {
                        messages.set(indexToUpdate, message);
                        adapter.notifyItemChanged(indexToUpdate);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                com.example.g11_group_application.Service_layer.Message message = dataSnapshot.getValue(com.example.g11_group_application.Service_layer.Message.class);
                if (message != null) {
                    lastMessageNo--;
                    int indexToRemove = -1;
                    for (int i = 0; i < messages.size(); i++) {
                        if (messages.get(i).getKey().equals(message.getKey())) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove != -1) {
                        messages.remove(indexToRemove);
                        adapter.notifyItemRemoved(indexToRemove);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle child moves if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ChatRoom", "loadMessage:onCancelled", databaseError.toException());
                Toast.makeText(ChatRoom.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves the message input by the user to the Firebase database.
     */
    private void saveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please write message first", Toast.LENGTH_SHORT).show();
            return;
        }
        sendMessage(message);
    }

    /**
     * Sends the message to the Firebase database.
     *
     * @param messageText The text of the message to send.
     */
    private void sendMessage(String messageText) {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = currentTimeFormat.format(calForTime.getTime());

        String messageKey = MessageRef.push().getKey();
        Message messageInfo = new Message(messageKey, messageText, currentUserName, currentDate, currentTime, lastMessageNo + 1);

        // Send the message
        MessageRef.child(messageKey).setValue(messageInfo).addOnSuccessListener(aVoid -> {
            // Add message details to the unread messages for the receiver
            DatabaseReference unreadMessagesRef = FirebaseDatabase.getInstance().getReference("Users").child(chatUser.getId()).child("unreadMessages").child(messageKey);
            UnreadMessageDetails unreadDetails = new UnreadMessageDetails(currentUserId, currentUserName, messageText.length() > 30 ? messageText.substring(0, 30) + "..." : messageText, calForDate.getTimeInMillis(), messageKey);

            unreadMessagesRef.setValue(unreadDetails).addOnSuccessListener(aVoid2 -> {
                Log.d("ChatActivity", "Unread message detail added successfully.");
            }).addOnFailureListener(e -> {
                Log.d("ChatActivity", "Failed to add unread message detail.", e);
            });
        });
    }
}