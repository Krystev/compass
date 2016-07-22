package com.inveitix.android.compass.database;

import android.content.Context;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inveitix.android.compass.Installation;
import com.inveitix.android.compass.database.models.LocationModel;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private final DatabaseReference databaseReference;

    private FirebaseHelper() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("message");
    }

    public static FirebaseHelper getInstance() {
        if(instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public void addLocation(LocationModel locationModel, Context context) {
        String itemKey = databaseReference.child("users").child(Installation.id(context)).push().getKey();
        databaseReference.child("users").child(Installation.id(context)).child(itemKey).setValue(locationModel);
    }
}
