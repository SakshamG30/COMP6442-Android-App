package com.example.g11_group_application.firebase_connection_DAO;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import com.example.g11_group_application.UI_Layer.SuicidePreventionData;

/**
 * @Author: Onam Dumbare (u7704695)
 * Created: 08-May-2024
 * This is the FirebaseDAO class for handling real-time operations.
 * Handles real-time operations with Firebase Database specifically for suicide prevention data.
 * This class provides methods to upload and retrieve lists of {@link SuicidePreventionData}.
 */
public class RealTimeFirebaseOperations {
    private DatabaseReference databaseReference;
    private Gson gson;

    public RealTimeFirebaseOperations() {
        // Initialize reference to the correct database node
        databaseReference = FirebaseDatabase.getInstance().getReference("suicidePreventionData");
        gson = new Gson();
    }
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    /**
     * Uploads a list of SuicidePreventionData to Firebase.
     *
     * @param userDataList The list of SuicidePreventionData to be uploaded.
     */
    public void uploadDataList(List<SuicidePreventionData> userDataList) {
        String json = gson.toJson(userDataList);
        databaseReference.setValue(json); // Corrected to write directly to "suicidePreventionData"
    }

    /**
     * Retrieves a list of SuicidePreventionData from Firebase.
     * Calls the provided callback with the data list once retrieval is complete or in case of error.
     *
     * @param callback The callback to be invoked after data is retrieved or in case of an error.
     */
    public void getDataList(final UserDataListCallback callback) {

        // Add listener in change in real time database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String json = dataSnapshot.getValue(String.class);
                    Type listType = new TypeToken<List<SuicidePreventionData>>(){}.getType();
                    List<SuicidePreventionData> userDataList = gson.fromJson(json, listType);
                    callback.onUserDataListReceived(userDataList);
                } else {
                    callback.onUserDataListReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    /**
     * Interface definition for a callback to be invoked when user data list is retrieved.
     */
    public interface UserDataListCallback {
        void onUserDataListReceived(List<SuicidePreventionData> userDataList);
        void onError(Exception e);
    }
}