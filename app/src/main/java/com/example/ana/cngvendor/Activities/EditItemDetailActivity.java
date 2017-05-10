package com.example.ana.cngvendor.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ana.cngvendor.Adapters.PriceTableAdapter;
import com.example.ana.cngvendor.Objects.ItemDetail;
import com.example.ana.cngvendor.R;
import com.example.ana.cngvendor.Activities.MainActivity;
import com.example.ana.cngvendor.Activities.VendorItemsListActivity;
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
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION_CODES.N;
import static com.example.ana.cngvendor.R.id.offerInput;

public class EditItemDetailActivity extends AppCompatActivity{

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private TextInputLayout itemNameEditText;
    private TextInputLayout itemPriceEditText;
    private TextInputLayout itemDescriptionEditText;
    private ImageView itemPhoto;
    private Button button;

    private String itemName;
    private String itemPrice;
    private String itemDescription;
    private Uri itemUri = null;

    private static final int RC_PHOTO_PICKER_1 = 2;
    private static final int RC_PHOTO_PICKER_2 = 3;
    private static final int RC_PHOTO_PICKER_3 = 4;
    private int imgFlag=0;
    private boolean first1,first2,first3;


    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;

    private ImageView imageView1,imageView2,imageView3;
    private ProgressBar bar1, bar2, bar3;
    private ArrayList<String> itemUris;
    private int uploadCount = 0;
    private TextInputLayout quantEditText;
    private TextInputLayout unitEditText;

    private String editDetail;
    private String key;
    private String item;
    private String industry;
    private String id;
    private String quant;
    private String unit;
    private int maxWeightPlusOne = 6;
    private int editpricePosition;
    private int priceEditFlag=0;

    private ListView priceListview;

    private String earlierPhotoUrl;

    ArrayList<String> priceTable;

    private static final int RC_PHOTO_PICKER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        unit = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bar1 = (ProgressBar) findViewById(R.id.image1Progress);
        bar2 = (ProgressBar) findViewById(R.id.image2Progress);
        bar3 = (ProgressBar) findViewById(R.id.image3Progress);

        mImageView1 = (ImageView) findViewById(R.id.itemImage1);
        mImageView2 = (ImageView) findViewById(R.id.itemImage2);
        mImageView3 = (ImageView) findViewById(R.id.itemImage3);

        imageView1 = (ImageView) findViewById(R.id.itemIndicator1);
        imageView2 = (ImageView) findViewById(R.id.itemIndicator2);
        imageView3 = (ImageView) findViewById(R.id.itemIndicator3);

        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);

        mImageView1.setImageResource(R.drawable.ic_add_black_24px);
        mImageView1.setClickable(true);
        mImageView2.setClickable(false);
        mImageView3.setClickable(false);

        first1 = first2 = first3 = true;

        itemUris = new ArrayList<String>();
        for(int j=0;j<3;j++){
            itemUris.add(Integer.toString(0));
        }

        priceTable = new ArrayList<>(maxWeightPlusOne);

        quantEditText = (TextInputLayout) findViewById(R.id.quantity);

        unitEditText = (TextInputLayout) findViewById(R.id.unit);

        itemNameEditText=(TextInputLayout)findViewById(R.id.edit_item_detail_name);
        itemPriceEditText=(TextInputLayout)findViewById(R.id.edit_item_detail_price);
        itemDescriptionEditText=(TextInputLayout)findViewById(R.id.edit_item_detail_description);
        itemPhoto = (ImageView)findViewById(R.id.itemImage1);
        button = (Button) findViewById(R.id.detailAddButton);

        Intent i = getIntent();
        editDetail = i.getStringExtra("editDetail");
        itemName = i.getStringExtra("subItemName");
        item = i.getStringExtra("itemName");
        industry = i.getStringExtra("industryName");
        id = i.getStringExtra("shopId");

        priceListview = (ListView) findViewById(R.id.priceList);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("item_photos");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.item_detail_edit_done_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName=itemNameEditText.getEditText().getText().toString();
                itemPrice=itemPriceEditText.getEditText().getText().toString();
                itemDescription=itemDescriptionEditText.getEditText().getText().toString();

                mFirebaseDatabase=FirebaseDatabase.getInstance();

                if(editDetail.equals("yes")){
                    if(itemName.equals("") || itemDescription.equals(""))
                    {
                        Toast.makeText(EditItemDetailActivity.this, "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DatabaseReference editReference = FirebaseDatabase.getInstance().getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id).child(item.replaceAll("[^A-Za-z0-9 ]", "")).child(key);
                        Map<String,Object> taskMap = new HashMap<String,Object>();
                        taskMap.put("itemName", itemNameEditText.getEditText().getText().toString());
                        taskMap.put("itemDescription",itemDescriptionEditText.getEditText().getText().toString());
                        taskMap.put("itemPrice",priceTable);
                        if (itemUris.size()>=1) {
                            taskMap.put("itemUrl", itemUris);
                        }
                        editReference.updateChildren(taskMap);
                        Intent i = new Intent(view.getContext(), MainActivity.class);
                        startActivity(i);
                    }

                }

                if(editDetail.equals("no")){
                    if(itemUris.size()>=1){
                        if(!itemName.equals("") || !itemDescription.equals("")){
                            mDatabaseReference = mFirebaseDatabase.getReference().child(industry).child(id).child(item.replaceAll("[^A-Za-z0-9 ]", ""));
                            mDatabaseReference.push().setValue(new ItemDetail(itemName,priceTable,itemDescription,itemUris));
                            Intent i = new Intent(view.getContext(), MainActivity.class);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(EditItemDetailActivity.this,"Please dont leave any field blank",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(EditItemDetailActivity.this,"Please select atleast 1 image",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(editDetail.equals("yes")){
            setTitle("Edit your item");

            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            Log.v("tag",VendorItemsListActivity.industryName+" "+VendorItemsListActivity.id+" "+item+" "+itemName);
            final DatabaseReference databaseReference = firebaseDatabase.getReference().child(VendorItemsListActivity.industryName).child(VendorItemsListActivity.id).child(item.replaceAll("[^A-Za-z0-9 ]", ""));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.hasChild("itemName") && snapshot.child("itemName").getValue().toString().equals(itemName)){

                            //industryNameEditText.setText(snapshot.child("industryName").getValue().toString());
                            itemNameEditText.getEditText().setText(snapshot.child("itemName").getValue().toString());
                            itemDescriptionEditText.getEditText().setText(snapshot.child("itemDescription").getValue().toString());
                            priceTable = (ArrayList)snapshot.child("itemPrice").getValue();
                            key = (String) snapshot.getKey();
                            itemUris = (ArrayList<String>)snapshot.child("itemUrl").getValue();
                            if(!itemUris.get(0).equals("0")){
                                Glide.with(mImageView1.getContext()).load(itemUris.get(0)).into(mImageView1);
                            }
                            if(!itemUris.get(1).equals("0")){
                                Glide.with(mImageView2.getContext()).load(itemUris.get(1)).into(mImageView2);
                            }
                            if(!itemUris.get(2).equals("0")){
                                Glide.with(mImageView3.getContext()).load(itemUris.get(2)).into(mImageView3);
                            }
                            break;
                        }
                    }
                    updatePriceList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            setTitle("Add an item");
            priceTable.add(0,"Add a price above");
            updatePriceList();
        }
    }

    public void updatePriceList(){

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, priceTable);

        PriceTableAdapter adapter = new PriceTableAdapter(this,android.R.layout.simple_list_item_1,priceTable);

        priceListview.setAdapter(adapter);

        priceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue    = (String) priceListview.getItemAtPosition(position);
                if(!itemValue.equals("Add a price above")){
                    String[] parts = itemValue.split(" ");
                    itemPriceEditText.getEditText().setText(parts[1]);
                    quantEditText.getEditText().setText(parts[3]);
                    unitEditText.getEditText().setText(parts[4]);
                    priceEditFlag = 1;
                    editpricePosition = position;
                }
            }
        });
    }

    public void addPrice(View view)
    {
        String price=itemPriceEditText.getEditText().getText().toString();
        quant=quantEditText.getEditText().getText().toString();
        unit=unitEditText.getEditText().getText().toString();

        if(quant.equals("") || unit.equals("")){
            Toast.makeText(this, "Do not leave Quantity and Unit blank", Toast.LENGTH_SHORT).show();
        }
        else{
            if(priceEditFlag == 1){
                priceTable.remove(editpricePosition);
                if(!price.equals("")){
                    priceTable.add("Rs " + price + " for " + (quant) + " " + unit);
                    itemPriceEditText.getEditText().setText(null);
                    quantEditText.getEditText().setText(null);
                    unitEditText.getEditText().setText(null);
                }
                else{
                    if(priceTable.size()==0){
                        priceTable.add("Add a price above");
                    }
                }
                priceEditFlag=0;
            }
            else{
                if(price.equals("")){
                    Toast.makeText(this, "Do not leave Price blank", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(priceTable.contains("Add a price above")){
                        priceTable.remove("Add a price above");
                    }
                    if(!itemPriceEditText.getEditText().getText().toString().equals("")){
                        priceTable.add("Rs " + price + " for " + (quant) + " " + unit);
                        itemPriceEditText.getEditText().setText(null);
                        quantEditText.getEditText().setText(null);
                        unitEditText.getEditText().setText(null);
                    }
                }
            }
            updatePriceList();
        }
    }

    public void uploadPic1(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER_1);
        imgFlag=1;
    }
    public void uploadPic2(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER_2);
        imgFlag = 1;
    }

    public void uploadPic3(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER_3);
        imgFlag = 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER_1){
            if(resultCode == RESULT_OK){

                bar1.setVisibility(View.VISIBLE);
                mImageView1.setImageBitmap(null);

                Uri selectedImageUri = data.getData();
                StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());

                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(editDetail.equals("yes")){
                                    if(!itemUris.get(0).equals("0")){
                                        StorageReference earlierRef = mFirebaseStorage.getReferenceFromUrl(itemUris.get(0));
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
                                }

                                itemUris.remove(0);
                                itemUris.add(0,taskSnapshot.getDownloadUrl().toString());
                                Glide.with(mImageView1.getContext())
                                        .load(taskSnapshot.getDownloadUrl().toString())
                                        .into(mImageView1);

                                if(first1){
                                    mImageView2.setImageResource(R.drawable.ic_add_black_24px);
                                    mImageView2.setClickable(true);
                                    imageView1.setVisibility(View.VISIBLE);
                                    first1 = false;
                                }

                                bar1.setVisibility(View.GONE);
                                imgFlag=0;
                            }
                        });
            }
        }
        if(requestCode == RC_PHOTO_PICKER_2){
            if(resultCode == RESULT_OK){
                bar2.setVisibility(View.VISIBLE);
                mImageView2.setImageBitmap(null);

                Uri selectedImageUri = data.getData();
                StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());

                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(editDetail.equals("yes")){
                                    if (!itemUris.get(1).equals("0")){
                                        StorageReference earlierRef = mFirebaseStorage.getReferenceFromUrl(itemUris.get(1));
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
                                }

                                itemUris.remove(1);
                                itemUris.add(1,taskSnapshot.getDownloadUrl().toString());
                                Glide.with(mImageView2.getContext())
                                        .load(taskSnapshot.getDownloadUrl().toString())
                                        .into(mImageView2);

                                if(first2){
                                    mImageView3.setImageResource(R.drawable.ic_add_black_24px);
                                    mImageView3.setClickable(true);
                                    imageView3.setVisibility(View.VISIBLE);
                                    first2 = false;
                                }

                                bar2.setVisibility(View.GONE);
                                imgFlag=0;
                            }
                        });
            }
        }
        if(requestCode == RC_PHOTO_PICKER_3){
            if(resultCode == RESULT_OK){
                bar3.setVisibility(View.VISIBLE);
                mImageView3.setImageBitmap(null);

                Uri selectedImageUri = data.getData();
                StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());

                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(editDetail.equals("yes")){
                                    if(!itemUris.get(2).equals("0")){
                                        StorageReference earlierRef = mFirebaseStorage.getReferenceFromUrl(itemUris.get(2));
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
                                }

                                itemUris.remove(2);
                                itemUris.add(2,taskSnapshot.getDownloadUrl().toString());
                                Glide.with(mImageView3.getContext())
                                        .load(taskSnapshot.getDownloadUrl().toString())
                                        .into(mImageView3);

                                if(first3){

                                }
                                bar3.setVisibility(View.GONE);
                                mImageView3.setClickable(false);
                                imgFlag=0;
                            }
                        });
            }
        }

    }
}
