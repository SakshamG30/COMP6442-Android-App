package com.example.g11_group_application.firebase_connection_DAO;
/**
 * @Author: Onam Dumbare (u7704695)
 * @Editor: Divyesh Srivastava (u7726856)
 * Created: 19-April-2024
 * Comments: This is the FirebaseDAO class
 *
 */
import android.app.Activity;

import com.example.g11_group_application.UI_Layer.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CountDownLatch;

public class firebaseDAO extends Activity {
    private static firebaseDAO instance = null;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    private static final String TAG = "DAOLayer";
    private static FirebaseAuth auth;
    private FirebaseFirestore db;
    private static MainActivity mainActivity;
    private static firebase_backend_Components backendComponents;
    public static boolean database_connected = false;
    public static CountDownLatch latch;

    private firebaseDAO() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static synchronized firebaseDAO getInstance() {
        if (instance == null) {
            instance = new firebaseDAO();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }
}


