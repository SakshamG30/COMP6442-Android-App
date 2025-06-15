package com.example.g11_group_application.Service_layer;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 01-May-2024
 * Represents a user with first name, optional middle name, last name, and date of birth.
 * This class also provides functionality to display user details.
 *
 */
public class User implements Serializable {
    private final String id;
    private final String firstName;
    private final String middleName; // Optional middle name
    private final String lastName;
    private final String dob; // Date of Birth

    // Firebase database instance
    private static final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    public User(String id, String firstName, String middleName, String lastName, String dob) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getMiddleName() { return middleName; }

    public String getId() { return id; }

    public String getUserName() {
        return firstName + (middleName != null ? " " + middleName : "") + " " + lastName;
    }

    @Override
    public String toString() {
        return "Name: " + getUserName() + ", DOB: " + dob;
    }

    public static void displayUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        for (User user : users) {
            System.out.println(user.toString());
        }
    }

    // Method to save or update user data in Firebase
    public void saveOrUpdateUser() {
        DatabaseReference userRef = databaseRef.child("Users").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("firstName", firstName);
                userMap.put("middleName", middleName);
                userMap.put("lastName", lastName);
                userMap.put("dob", dob);
                userMap.put("status", "offline");  // Default status as offline when created
                userMap.put("lastOnline", System.currentTimeMillis());

                if (!dataSnapshot.exists()) {
                    // If user does not exist, initialize unread messages as an empty node
                    userMap.put("unreadMessages", new HashMap<String, UnreadMessageDetails>());
                } // If user exists, do not reset the unread messages

                userRef.updateChildren(userMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }
}