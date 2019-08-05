package com.example.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase firebaseDB;
    public static DatabaseReference firebaseDBRef;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference firebaseStoRef;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static FirebaseUtil firebaseUtil;
    public static final int RC_SIGN_IN=123;
    public static ArrayList<TravelDeal> deals;
    private static ListActivity caller;

    private FirebaseUtil(){}
    public static boolean isAdmin;

    public static void openFBRef(String ref,final ListActivity callerActivity){
        if(firebaseUtil == null){
            firebaseUtil =new FirebaseUtil();
            firebaseDB=FirebaseDatabase.getInstance();
            firebaseAuth=FirebaseAuth.getInstance();
            caller=callerActivity;
            authStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser()==null) {
                        FirebaseUtil.signIn();
                    }
                    else{
                        String userId=firebaseAuth.getUid();
                        checkIsAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(),"Welcome Back",Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();
        }
        deals=new ArrayList<TravelDeal>();
        firebaseDBRef=firebaseDB.getReference().child(ref);
    }

    private static void signIn(){
        List<AuthUI.IdpConfig> providers= Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private static void checkIsAdmin(String uid){
        FirebaseUtil.isAdmin=false;
        DatabaseReference ref=firebaseDB.getReference().child("admin")
                .child(uid);
        ChildEventListener listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    public static void attachListener(){
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachListener(){
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public static void connectStorage(){
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseStoRef=firebaseStorage.getReference().child("deals_pictures");
    }
}
