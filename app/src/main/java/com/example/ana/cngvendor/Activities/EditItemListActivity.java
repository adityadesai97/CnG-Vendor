package com.example.ana.cngvendor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ana.cngvendor.Objects.ItemDetail;
import com.example.ana.cngvendor.Objects.MenuItem;
import com.example.ana.cngvendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class EditItemListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private TextInputLayout itemNameEditText;
    private ProgressBar pb;

    private String itemName;
    private String oldItemName;
    private String editItem;
    private ArrayList<ItemDetail> temp;
    private int alreadyExistsFlag=0;

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
        oldItemName = itemName;

        temp = new ArrayList<ItemDetail>();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.item_edit_done_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName=itemNameEditText.getEditText().getText().toString();

                if(itemName.equals("")){
                    Toast.makeText(EditItemListActivity.this,"Pleae enter the name of the item.",Toast.LENGTH_SHORT).show();
                }
                else{
                    //mFirebaseDatabase=FirebaseDatabase.getInstance();
                    //mDatabaseReference=mFirebaseDatabase.getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id);
                    if(editItem.equals("yes")){
                        DatabaseReference editReference = FirebaseDatabase.getInstance().getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id).child(key);
                        Map<String,Object> taskMap = new HashMap<String,Object>();
                        taskMap.put("itemName", itemNameEditText.getEditText().getText().toString());
                        editReference.updateChildren(taskMap);


                        //Copying Data
                        DatabaseReference copyReference = FirebaseDatabase.getInstance().getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id).child(oldItemName);
                        copyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    String iName = (String) snapshot.child("itemName").getValue();
                                    ArrayList<String> iPrice = (ArrayList<String>) snapshot.child("itemPrice").getValue();
                                    String iDesc = (String) snapshot.child("itemDescription").getValue();
                                    ArrayList<String> iUrl = (ArrayList<String>) snapshot.child("itemUrl").getValue();
                                    temp.add(new ItemDetail(iName, iPrice, iDesc,iUrl));
                                }
                                restoreNode();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //Deleting this node
                        copyReference.removeValue();
                    }

                    if(editItem.equals("no")){
                        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int count=0;
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    count++;
                                    if(snapshot.hasChild("itemName") && snapshot.child("itemName").getValue().equals(itemName)){
                                        alreadyExistsFlag=1;
                                        Toast.makeText(EditItemListActivity.this,"Item already exists",Toast.LENGTH_SHORT).show();
                                        break;
                                    }
//                                    else{
//                                        mDatabaseReference.push().setValue(new MenuItem(itemName));
//                                    }
                                }
                                if(alreadyExistsFlag==0){
                                    mDatabaseReference.push().setValue(new MenuItem(itemName));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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


    public void restoreNode(){
        Log.v("tag2", Integer.toString(temp.size()));
        DatabaseReference copyReference2 = FirebaseDatabase.getInstance().getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id).child(itemName);
        for(int i = 0; i < temp.size(); i++){
            copyReference2.push().setValue(temp.get(i));
        }
    }

}
