package com.example.ana.cngvendor.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ana.cngvendor.Activities.EditShopActivity;
import com.example.ana.cngvendor.Activities.MainActivity;
import com.example.ana.cngvendor.Activities.VendorItemsListActivity;
import com.example.ana.cngvendor.Objects.Shop;
import com.example.ana.cngvendor.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

//import com.example.adityadesai.cng.Activities.VendorItemListActivity;


/**
 * Created by adityadesai on 13/02/17.
 */


public class VendorShopListAdapter extends RecyclerView.Adapter<VendorShopListAdapter.ShopHolder> {

    static Context context;

    private static ArrayList<Shop> mShops;
    int lastPosition = -1;

    public static class ShopHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mItemName;
        private TextView mItemAddress;
        private ImageView mImageView;
        private RatingBar ratingBar;
        private Shop mShop;
        private LinearLayout container;

        private String industryName;
        private String name;
        private String address;
        private String phone;
        private String id;
        private String Url;

        public ShopHolder(View v) {
            super(v);

            mItemName = (TextView) v.findViewById(R.id.shop_name);
            mItemAddress = (TextView) v.findViewById(R.id.shop_address);
            mImageView = (ImageView) v.findViewById(R.id.shop_image);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
            container = (LinearLayout) v.findViewById(R.id.shopRootLayout);
            v.setOnClickListener(this);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    new AlertDialog.Builder(context)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = getAdapterPosition();
                                    mShop = mShops.get(position);
                                    if(mShop!=null){
                                        industryName = mShop.getIndustryName();
                                        id = mShop.getShop_id();
                                    }

                                    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    final DatabaseReference databaseReference = firebaseDatabase.getReference().child(industryName);
                                    final FirebaseStorage mfirebaseStorage = FirebaseStorage.getInstance();
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                if(snapshot.hasChild("shop_id") && snapshot.child("shop_id").getValue().toString().equals(id)){
                                                    final String uri = (String) snapshot.child("shopUrl").getValue();

                                                    StorageReference ref;
                                                    if(!uri.equals("")){
                                                        ref = mfirebaseStorage.getReferenceFromUrl(uri);
                                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                                    }

                                                    if(!uri.equals("")){
                                                        ref = mfirebaseStorage.getReferenceFromUrl(uri);
                                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                                    }

                                                    if(!uri.equals("")){
                                                        ref = mfirebaseStorage.getReferenceFromUrl(uri);
                                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                                    }

                                                    snapshot.getRef().removeValue();
                                                    databaseReference.child(snapshot.child("shop_id").getValue().toString()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    v.getContext().startActivity(new Intent(v.getContext(),MainActivity.class));
                                }
                            })
                            .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = getAdapterPosition();
                                    mShop = mShops.get(position);
                                    if(mShop!=null){
                                        industryName = mShop.getIndustryName();
                                        id = mShop.getShop_id();
                                    }
                                    Intent i = new Intent(v.getContext(),EditShopActivity.class);
                                    i.putExtra("editShop","yes");
                                    i.putExtra("industry",industryName);
                                    i.putExtra("id",id);
                                    v.getContext().startActivity(i);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return false;
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position  =   getAdapterPosition();
            mShop = mShops.get(position);

            if(mShop!=null){
                name = mShop.getShopName();
                industryName=mShop.getIndustryName();
                address= mShop.getShopAddress();
                phone= mShop.getShopPhone();
                id = mShop.getShop_id();
                Url = mShop.getShopUrl();
            }

            Intent i=new Intent(v.getContext(),VendorItemsListActivity.class);
            i.putExtra("shopName",name);
            i.putExtra("shopAddress",address);
            i.putExtra("shopPhone",phone);
            i.putExtra("shop_id",id);
            i.putExtra("industry_name",industryName);
            //i.putAExtra("shop_url",Url);
            i.putExtra("shop_url",Url);
            v.getContext().startActivity(i);

        }

        public void bindIndustry(Shop shop) {
            mShop = shop;
            mItemName.setText(shop.getShopName());
            mItemAddress.setText(shop.getShopAddress());
            if(Integer.parseInt(shop.getNumRates())!=0){
                ratingBar.setRating(Float.parseFloat(shop.getTotalRatePoints())/Integer.parseInt(shop.getNumRates()));
            }
            else{
                ratingBar.setRating(0);
            }
            if(shop.getShopUrl() != null){
                Glide.with(mImageView.getContext()).load(shop.getShopUrl()).into(mImageView);
            }
        }
    }

    public VendorShopListAdapter(ArrayList<Shop> shops, Context context) {
        mShops = shops;
        this.context = context;
    }

    @Override
    public VendorShopListAdapter.ShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);
        return new VendorShopListAdapter.ShopHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(VendorShopListAdapter.ShopHolder holder, int position) {
        Shop itemShop = mShops.get(position);
        holder.bindIndustry(itemShop);
        setAnimation(holder.container, position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mShops.size();
    }

    public static Shop getShopbyName(String name){
        for(Shop shop : mShops){
            if(shop.getShopName() != null && shop.getShopName().contains(name)){
                return shop;
            }
        }
        return null;
    }
}
