package com.example.ana.cngvendor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ana.cngvendor.Objects.MenuItem;
import com.example.ana.cngvendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditItemListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private TextInputLayout itemNameEditText;
    private ProgressBar pb;

    private String itemName;
    private String editItem;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemNameEditText=(TextInputLayout) findViewById(R.id.edit_item_name);
        pb = (ProgressBar) findViewById(R.id.itemProgress);

        pb.setVisibility(View.GONE);


        Intent i = getIntent();
        itemName = i.getStringExtra("itemName");
        editItem = i.getStringExtra("editItem");



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.item_edit_done_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName=itemNameEditText.getEditText().getText().toString();

                if(itemName.equals("")){
                    Toast.makeText(EditItemListActivity.this,"Pleae enter the name of the item.",Toast.LENGTH_SHORT).show();
                }
                else{
                    mFirebaseDatabase=FirebaseDatabase.getInstance();
                    //mDatabaseReference=mFirebaseDatabase.getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id);

                    if(editItem.equals("yes")){
                        DatabaseReference editReference = FirebaseDatabase.getInstance().getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id).child(key);
                        Map<String,Object> taskMap = new HashMap<String,Object>();
                        taskMap.put("itemName", itemNameEditText.getEditText().getText().toString());
                        editReference.updateChildren(taskMap);
                    }

                    if(editItem.equals("no")){
                        mDatabaseReference = mFirebaseDatabase.getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id);
                        mDatabaseReference.push().setValue(new MenuItem(itemName));
                    }
                    Intent i =new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);
                }
            }
        });

        if(editItem.equals("yes")){
            setTitle("Edit your item");
            //Initial
            itemNameEditText.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);

            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.hasChild("itemName") && snapshot.child("itemName").getValue().toString().equals(itemName)){

                            //Initial
                            itemNameEditText.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);

                            itemNameEditText.getEditText().setText(snapshot.child("itemName").getValue().toString());
                            key = (String) snapshot.getKey();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            setTitle("Add an item");
        }
    }

}