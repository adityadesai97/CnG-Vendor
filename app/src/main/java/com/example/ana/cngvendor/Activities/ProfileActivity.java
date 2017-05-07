package com.example.ana.cngvendor.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ana.cngvendor.Objects.User;
import com.example.ana.cngvendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserPic;
    private TextInputLayout mUserPhone;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private SharedPreferences sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this,"Please dont save if u have already with this account",Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mUserName = (TextView) findViewById(R.id.userName);
        mUserEmail = (TextView) findViewById(R.id.userEmail);
        mUserPic = (ImageView) findViewById(R.id.userPic);
        mUserPhone = (TextInputLayout) findViewById(R.id.userPhone);

        sharedPrefs = getSharedPreferences("userInfo",MODE_APPEND);
        mUserName.setText(sharedPrefs.getString("name",null));
        mUserEmail.setText(sharedPrefs.getString("email",null));

        Glide.with(this).load(sharedPrefs.getString("pic",null)).into(mUserPic);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.child("uid").getValue().equals(sharedPrefs.getString("uid",null))){
                        Toast.makeText(getBaseContext(),"You have already registered",Toast.LENGTH_SHORT);
                        SharedPreferences.Editor ph = sharedPrefs.edit();
                        ph.putString("phone",snapshot.child("phone").getValue().toString());
                        ph.commit();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void saveUserDetails(View view){
        SharedPreferences.Editor e = sharedPrefs.edit();
        if(mUserPhone.getEditText().getText().toString().equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Enter Valid Number", Toast.LENGTH_SHORT).show();
        }
        else
        {
            e.putString("phone",mUserPhone.getEditText().getText().toString());
            mDatabaseReference.push().setValue(new User(sharedPrefs.getString("name","test"),
                    sharedPrefs.getString("uid","test"),
                    sharedPrefs.getString("email","test"),
                    mUserPhone.getEditText().getText().toString(),
                    sharedPrefs.getString("pic",null)));

            Toast.makeText(this,"Details Saved",Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
