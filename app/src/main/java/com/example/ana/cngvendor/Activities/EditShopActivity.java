package com.example.ana.cngvendor.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.ana.cngvendor.Objects.Shop;
import com.example.ana.cngvendor.R;
import com.example.ana.cngvendor.Activities.MainActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;
import static android.R.attr.theme;
import static com.example.ana.cngvendor.Activities.VendorItemsListActivity.industryName;
import static com.example.ana.cngvendor.Activities.VendorItemsListActivity.mMenuItems;
import static java.lang.System.load;

public class EditShopActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mIdFirebasDatabase;
    private DatabaseReference mIdDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;


    private Spinner industryNameSpinner;
    private TextInputLayout shopNameEditText;
    private TextInputLayout shopPhoneEditText;
    private TextInputLayout shopAddressEditText;
    private EditText offerInput;
    private ImageView mImageView;
    private CardView hideMe;
    private int imgFlag=0;

    private String shopIndustry;
    private String shopName;
    private String shopPhone;
    private String shopAddress;
    private String shopId;
    private String finalId;
    private ArrayList<String> offers;
    private String editShop;
    private String[] indList;
    private String shopUri = "";
    private String[] values;
    private int offerEditFlag = 0;
    private int editOfferPosition;
    private ArrayList<String> coordinates;

    private ImageView itemPhoto;

    private SharedPreferences sharedprefs;

    private static final int RC_PHOTO_PICKER_1 = 2;
    private static final int RC_PHOTO_PICKER_2 = 3;
    private static final int RC_PHOTO_PICKER_3 = 4;
    private String earlierPhotoUrl;
    private ListView offersListview;

    private static final int RC_PHOTO_PICKER = 2;
    private static final int RC_CAMERA = 6;
    private static final int PLACE_PICKER_REQUEST = 5;
    private int uploadCount = 0;
    private int offerCount = 0;
    private ProgressBar bar;
    private ProgressBar bar2;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop);

        bar = (ProgressBar) findViewById(R.id.offersListProgress);
        bar2 = (ProgressBar) findViewById(R.id.itemdetailprogress);

        itemPhoto = (ImageView)findViewById(R.id.shopImage);

        Intent i = getIntent();
        editShop = i.getStringExtra("editShop");
        shopIndustry = i.getStringExtra("industry");
        shopId = i.getStringExtra("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        offers = new ArrayList<String>();
        coordinates = new ArrayList<String>();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("shop_photos");

        industryNameSpinner=(Spinner)findViewById(R.id.edit_shop_industry);
        shopNameEditText=(TextInputLayout)findViewById(R.id.edit_shop_name);
        shopPhoneEditText=(TextInputLayout)findViewById(R.id.edit_shop_phone);
        shopAddressEditText=(TextInputLayout)findViewById(R.id.edit_shop_address);
        offerInput = (EditText)findViewById(R.id.offerInput);
        mImageView = (ImageView) findViewById(R.id.shopImage);

//        mImageView1.setImageResource(R.drawable.ic_insert_photo_black_24px);
//        mImageView2.setClickable(false);
//        mImageView3.setClickable(false);


        offersListview = (ListView) findViewById(R.id.offersListView);
//        values = new String[] { "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View"
//        };

        sharedprefs = getSharedPreferences("userInfo",MODE_APPEND);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.shop_edit_done_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopIndustry=industryNameSpinner.getSelectedItem().toString();
                shopName=shopNameEditText.getEditText().getText().toString();
                shopPhone=shopPhoneEditText.getEditText().getText().toString();
                shopAddress=shopAddressEditText.getEditText().getText().toString();

                mFirebaseDatabase=FirebaseDatabase.getInstance();
                mDatabaseReference=mFirebaseDatabase.getReference().child(shopIndustry);

                if(editShop.equals("yes")){
                    if(shopName.equals("") || shopPhone.equals("") || shopAddress.equals(""))
                    {
                        Toast.makeText(EditShopActivity.this, "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DatabaseReference editReference = FirebaseDatabase.getInstance().getReference().child(shopIndustry).child(key);
                        Map<String,Object> taskMap = new HashMap<String,Object>();
                        taskMap.put("shopName",shopNameEditText.getEditText().getText().toString());
                        taskMap.put("shopAddress", shopAddressEditText.getEditText().getText().toString());
                        taskMap.put("shopPhone", shopPhoneEditText.getEditText().getText().toString());
                        taskMap.put("offers",offers);
                        taskMap.put("coordinates",coordinates);
                        if (shopUri != null) {
                            taskMap.put("shopUrl", shopUri.toString());
                        }
                        editReference.updateChildren(taskMap);
                    }

                }

                if(editShop.equals("no")){
                    if(shopName.equals("") || shopPhone.equals("") || shopAddress.equals(""))
                    {
                        Toast.makeText(EditShopActivity.this, "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        getId();
                    }
                }
                else{
                    Intent i =new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);
                }
            }
        });

        if(editShop.equals("yes")){
            setTitle("Edit your shop");
            //hideMe = (CardView)findViewById(R.id.hideForEdit);
            //hideMe.setVisibility(View.GONE);

            shopNameEditText.setEnabled(false);
            shopPhoneEditText.setEnabled(false);
            shopAddressEditText.setEnabled(false);

            industryNameSpinner.setVisibility(View.GONE);

            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference().child(shopIndustry);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.hasChild("shop_id") && snapshot.child("shop_id").getValue().toString().equals(shopId)){
                            indList = getResources().getStringArray(R.array.Industries);
                            int post=0,i;
                            for(i = 0;i<indList.length;i++){
                                if(indList[i].equals(snapshot.child("industryName").getValue().toString())){
                                    post = i;
                                }
                            }

                            shopNameEditText.setEnabled(true);
                            shopPhoneEditText.setEnabled(true);
                            shopAddressEditText.setEnabled(true);

                            industryNameSpinner.setSelection(post);
                            shopNameEditText.getEditText().setText(snapshot.child("shopName").getValue().toString());
                            shopPhoneEditText.getEditText().setText(snapshot.child("shopPhone").getValue().toString());
                            shopAddressEditText.getEditText().setText(snapshot.child("shopAddress").getValue().toString());
                            offers = (ArrayList<String>)snapshot.child("offers").getValue();
                            shopUri = (String)snapshot.child("shopUrl").getValue().toString();
                            if(shopUri!=null){
                                Glide.with(mImageView.getContext()).load(shopUri).into(mImageView);
                            }
                            key = (String) snapshot.getKey();
                            break;
                        }
                    }
//                    if(!offers.isEmpty()){
//                        updateOffersList();
//                    }
                    updateOffersList();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            setTitle("Add a shop");
            offers.add(0,"Add an offer above");
            updateOffersList();
        }
    }

    public void updateOffersList(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditShopActivity.this,android.R.layout.simple_list_item_1, android.R.id.text1, offers);
        offersListview.setAdapter(adapter);
        offersListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  itemValue    = (String) offersListview.getItemAtPosition(i);
                if(!itemValue.equals("Add an offer above")){
                    offerInput.setText(itemValue);
                    offerEditFlag = 1;
                    editOfferPosition = i;
                }
            }
        });
    }

    public void addOffer(View view){
        if(offerEditFlag == 1){
            offers.remove(editOfferPosition);
            if(!offerInput.getText().toString().equals("")){
                offers.add(editOfferPosition,offerInput.getText().toString());
                offerInput.setText(null);
            }
            else{
                if(offers.size()==0){
                    offers.add("Add an offer above");
                }
            }
            offerEditFlag = 0;
        }
        else{
            if(offers.contains("Add an offer above")){
                offers.remove("Add an offer above");
            }
            if(!offerInput.getText().toString().equals("")){
                offers.add(offerInput.getText().toString());
                offerInput.setText(null);
                offerInput.setHint("Add some more...");
            }
        }
        updateOffersList();
    }

    public void uploadItemPic(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    public void openCamera(View view){
        Toast.makeText(this,"This feature is not available right now",Toast.LENGTH_SHORT).show();
        // IDK if the code is right
       /* Intent intent = new Intent(Intent.ACTION_CAMERA_BUTTON);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_CAMERA);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER){
            if(resultCode == RESULT_OK){
                itemPhoto.setImageBitmap(null);
                bar2.setVisibility(View.VISIBLE);

                Uri selectedImageUri = data.getData();
                StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());

                if(editShop.equals("yes")){
                    StorageReference earlierRef = mFirebaseStorage.getReferenceFromUrl(shopUri);
                    earlierRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // When the image has successfully uploaded, we get its download URL
                                shopUri = taskSnapshot.getDownloadUrl().toString();
                                // Set the download URL to the message box, so that the user can send it to the database
                                Glide.with(itemPhoto.getContext()).load(shopUri).into(itemPhoto);
                                bar2.setVisibility(View.GONE);
                            }
                        });
            }
        }

        if(requestCode == RC_CAMERA){
            if(resultCode == RESULT_OK){
                //Code to do
            }
        }
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                coordinates.add(0,Double.toString(place.getLatLng().latitude));
                coordinates.add(1,Double.toString(place.getLatLng().longitude));
            }
        }
    }

    private void getId(){
        mIdFirebasDatabase=FirebaseDatabase.getInstance();
        mIdDatabaseReference=mIdFirebasDatabase.getReference().child("id");

        mIdDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(((String)snapshot.getKey()).equals(shopIndustry)){
                        shopId=(String)snapshot.getValue().toString();
                    }
                }
                Log.v("tag",shopId);
                pushShop(shopId);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pushShop(String shopId){
        finalId=Integer.toString((Integer.parseInt(shopId))+1);
        if (!shopUri.equals("")){
            mDatabaseReference.push().setValue(new Shop(shopName,shopAddress,shopPhone,finalId,shopIndustry,shopUri.toString(),sharedprefs.getString("uid",null),offers,"0","0",coordinates));
            updateId(finalId);

            Intent i =new Intent(getBaseContext(),MainActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(this,"Please select an image",Toast.LENGTH_LONG).show();
        }
    }

    private void updateId(final String id){
        mIdDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(((String)snapshot.getKey()).equals(shopIndustry)){
                        snapshot.getRef().setValue(id);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void locateButton(View view){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(EditShopActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
