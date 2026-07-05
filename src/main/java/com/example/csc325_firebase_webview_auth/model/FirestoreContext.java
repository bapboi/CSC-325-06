package com.example.csc325_firebase_webview_auth.model;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

/**
 *
 * @author MoaathAlrajab
 */
public class FirestoreContext {

    public Firestore firebase() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(CredentialsProvider.getCredentials())
                .build();
        FirebaseApp.initializeApp(options);
        System.out.println("Firebase is initialized");
        return FirestoreClient.getFirestore();
    }

}
