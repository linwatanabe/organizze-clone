package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {

    private static DatabaseReference mDatabase;
    private static FirebaseAuth mAuth;

    public static DatabaseReference getFirebaseDatabase() {
        if (mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return mDatabase;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

}
