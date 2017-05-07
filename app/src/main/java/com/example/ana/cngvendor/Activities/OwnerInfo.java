package com.example.ana.cngvendor.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ana.cngvendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerInfo extends Activity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private TextView mOwnerName;
    private TextView mOwnerEmail;
    private TextView mOwnerPhone;
    private ImageView mOwnerPic;

    private String check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        mOwnerName = (TextView) findViewById(R.id.ownerName);
        mOwnerEmail = (TextView) findViewById(R.id.ownerEmail);
        mOwnerPhone = (TextView) findViewById(R.id.ownerPhone);
        mOwnerPic = (ImageView) findViewById(R.id.ownerPic);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");
        Intent i = getIntent();
        check = i.getStringExtra("uid");

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.child("uid").getValue().equals(check)){
                        mOwnerName.setText(snapshot.child("name").getValue().toString());
                        mOwnerEmail.setText(snapshot.child("mail_id").getValue().toString());
                        mOwnerPhone.setText(snapshot.child("phone").getValue().toString());

                        Glide.with(OwnerInfo.this).load(snapshot.child("profile_url").getValue()).into(mOwnerPic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
