package com.example.ana.cngvendor.NavDrawerFragments;

/**
 * Created by Neil on 02-03-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ana.cngvendor.Activities.EditShopActivity;
import com.example.ana.cngvendor.Adapters.VendorShopListAdapter;
import com.example.ana.cngvendor.Objects.Shop;
import com.example.ana.cngvendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyShopsFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private VendorShopListAdapter mAdapter;
    private int id=1000;
    private boolean isCustomer;
    NavigationView navView;
    private ArrayList<Shop> mShopList;
    private ProgressBar bar;
    private ImageView emptyImage;
    private  TextView emptyText;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    private SharedPreferences userspf;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView =inflater.inflate(R.layout.myshops_page,null);

        bar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        emptyImage = (ImageView)rootView.findViewById(R.id.emptyImage);
        emptyText = (TextView) rootView.findViewById(R.id.emptyText);

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences("signInMode", Context.MODE_APPEND);
        userspf = getActivity().getSharedPreferences("userInfo",Context.MODE_APPEND);

        //Configured to just show groceries till users feature is not added
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mswipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.shopRefreshPage);
        mswipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mswipeRefreshLayout.setRefreshing(true);
                MyShopsFragment.fetchShopList fSl = new MyShopsFragment.fetchShopList();
                fSl.execute();
            }
        });


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.myshops_list);
        mLinearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mShopList= new ArrayList<>();

        MyShopsFragment.fetchShopList fSl = new MyShopsFragment.fetchShopList();
        fSl.execute();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.add_shop_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), EditShopActivity.class);
                i.putExtra("editShop","no");
                i.putExtra("industry","");
                i.putExtra("id","");
                startActivity(i);
            }
        });

        return rootView;
    }

    public void updateUI(){
        mswipeRefreshLayout.setRefreshing(false);
        bar.setVisibility(View.GONE);
        mAdapter = new VendorShopListAdapter(mShopList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        if(mShopList.isEmpty()){
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    public class fetchShopList extends AsyncTask<Void,Void,ArrayList<Shop>> {

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Shop> doInBackground(Void... params) {
            mDatabaseReference = mFirebaseDatabase.getReference();
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mShopList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.getKey().toString().equals("Industry") || snapshot.getKey().toString().equals("Id") || snapshot.getKey().toString().equals("users")){
                            continue;
                        }
                        else{
                            snapshot.getRef().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot1: dataSnapshot.getChildren()){
                                        if(snapshot1.child("ownerId").getValue() == null){continue;}
                                        if(snapshot1.child("ownerId").getValue().equals(userspf.getString("uid",null))){
                                            String shop_name = (String) snapshot1.child("shopName").getValue();
                                            String shop_address = (String) snapshot1.child("shopAddress").getValue();
                                            String shop_phonenum = (String) snapshot1.child("shopPhone").getValue();
                                            String shop_id =  (String) snapshot1.child("shop_id").getValue();
                                            String industry_name =  (String) snapshot1.child("industryName").getValue();
                                            String shop_uri = (String) snapshot1.child("shopUrl").getValue();
                                            String uid = (String) snapshot1.child("ownerId").getValue();
                                            ArrayList<String>  offers = (ArrayList<String>) snapshot1.child("offers").getValue();
                                            ArrayList<String> coordinates = (ArrayList<String>)snapshot1.child("coordinates").getValue();
                                            String totalRatePoints;
                                            String numRates;
                                            if(snapshot1.hasChild("totalRatePoints") && snapshot1.hasChild("numRates")){
                                                totalRatePoints = (String) snapshot1.child("totalRatePoints").getValue();
                                                numRates = (String) snapshot1.child("numRates").getValue();
                                            }
                                            else{
                                                totalRatePoints = "0";
                                                numRates = "0";
                                            }

                                            if(shop_name != null && shop_address != null && shop_phonenum != null && shop_id != null) {
                                                mShopList.add(new Shop(shop_name, shop_address, shop_phonenum,shop_id,industry_name,shop_uri,uid,offers,totalRatePoints,numRates,coordinates));
                                            }
                                        }
                                    }
                                    updateUI();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
////            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
////                @Override
////                public void onDataChange(DataSnapshot dataSnapshot) {
////                    mShopList.clear();
////                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
////                        if(snapshot.child("ownerId").getValue() == null){continue;}
////                        if(snapshot.child("ownerId").getValue().equals(userspf.getString("uid",null))){
////                            String shop_name = (String) snapshot.child("shopName").getValue();
////                            String shop_address = (String) snapshot.child("shopAddress").getValue();
////                            String shop_phonenum = (String) snapshot.child("shopPhone").getValue();
////                            String shop_id =  (String) snapshot.child("shop_id").getValue();
////                            String industry_name =  (String) snapshot.child("industryName").getValue();
////                            String shop_uri = (String) snapshot.child("shopUrl").getValue();
////                            String uid = (String) snapshot.child("ownerId").getValue();
////                            ArrayList<String>  offers = (ArrayList<String>) snapshot.child("offers").getValue();
////                            mShopList.add(new Shop(shop_name, shop_address, shop_phonenum,shop_id,industry_name,shop_uri,uid,offers));
////                        }
////                    }
////                    updateUI();
////                }
////
////                @Override
////                public void onCancelled(DatabaseError databaseError) {
////
////                }
////            });
//
//
//            mDatabaseReference = mFirebaseDatabase.getReference().child("Gym");
//            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        if(snapshot.child("ownerId").getValue() == null){continue;}
//                        if(snapshot.child("ownerId").getValue().equals(userspf.getString("uid",null))){
//                            String shop_name = (String) snapshot.child("shopName").getValue();
//                            String shop_address = (String) snapshot.child("shopAddress").getValue();
//                            String shop_phonenum = (String) snapshot.child("shopPhone").getValue();
//                            String shop_id =  (String) snapshot.child("shop_id").getValue();
//                            String industry_name =  (String) snapshot.child("industryName").getValue();
//                            String shop_uri = (String) snapshot.child("shopUrl").getValue();
//                            String uid = (String) snapshot.child("ownerId").getValue();
//                            ArrayList<String>  offers = (ArrayList<String>) snapshot.child("offers").getValue();
//                            mShopList.add(new Shop(shop_name, shop_address, shop_phonenum,shop_id,industry_name,shop_uri,uid,offers));
//                        }
//                    }
//                    updateUI();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


            return null;
        }
    }

}
