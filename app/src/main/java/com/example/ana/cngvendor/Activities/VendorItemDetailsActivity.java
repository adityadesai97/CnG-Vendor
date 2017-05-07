package com.example.ana.cngvendor.Activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ana.cngvendor.Objects.ItemDetail;
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

import static com.example.ana.cngvendor.Activities.VendorItemsListActivity.id;

public class VendorItemDetailsActivity extends AppCompatActivity {

    private ExpandableListView mExpandableListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private VendorItemDetailsAdapter mAdapter;
    private ArrayList<ItemDetail> mDetailList;
    private ArrayList<ArrayList<String>> mDescriptionList;
    public String item_name;
    private ProgressBar bar;
    private ImageView emptyImage;
    private TextView emptyText;
    private Toolbar toolbar;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mItemDetailDatabaseReference;
    private FirebaseStorage mFireBaseStorage;
    private ValueEventListener mValueEventListener;


    private String subItemName;
    private String industryName;
    private String id;
    private int i = 0;

    private ItemDetail mItemDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_item_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bar = (ProgressBar)findViewById(R.id.progressBar);
        emptyImage = (ImageView)findViewById(R.id.emptyImage);
        emptyText = (TextView)findViewById(R.id.emptyText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.itemdetailsRefreshPage);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                VendorItemDetailsActivity.fetchItemDetail fID = new VendorItemDetailsActivity.fetchItemDetail();
                fID.execute();
            }
        });

        Intent i = getIntent();
        item_name = i.getStringExtra("Item");
        industryName = i.getStringExtra("industryName");
        id = i.getStringExtra("id");


        setTitle(item_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_item_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getBaseContext(), EditItemDetailActivity.class);
                i.putExtra("editDetail","no");
                i.putExtra("industryName",industryName);
                i.putExtra("shopId",id);
                i.putExtra("itemName",item_name);
                startActivity(i);
            }
        });

        mExpandableListView = (ExpandableListView) this.findViewById(R.id.vendor_item_details);
        mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {

                new AlertDialog.Builder(VendorItemDetailsActivity.this)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TextView x = (TextView) view.findViewById(R.id.item_name);
                                subItemName = x.getText().toString();
//                                if(x != null){
//                                    subItemName = x.getItemName();
//                                }

                                final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                final DatabaseReference databaseReference = firebaseDatabase.getReference().child(VendorItemsListActivity.industryName).
                                        child(VendorItemsListActivity.id).child(item_name);
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                            if(snapshot.hasChild("itemName") && snapshot.child("itemName").getValue().toString().equals(subItemName)){
                                                ArrayList<String> urls = (ArrayList<String>) snapshot.child("itemUrl").getValue();
                                                mFireBaseStorage = FirebaseStorage.getInstance();
                                                final StorageReference ref1;
                                                final StorageReference ref2;
                                                final StorageReference ref3;
                                                if(!urls.get(0).equals("0")){
                                                    ref1 = mFireBaseStorage.getReferenceFromUrl(urls.get(0));
                                                    ref1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                                if(!urls.get(1).equals("0")){
                                                    ref2 = mFireBaseStorage.getReferenceFromUrl(urls.get(1));
                                                    ref2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                                if(!urls.get(2).equals("0")){
                                                    ref3 = mFireBaseStorage.getReferenceFromUrl(urls.get(2));
                                                    ref3.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        })
                        .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TextView x = (TextView) view.findViewById(R.id.item_name);
                                subItemName = x.getText().toString();
//                                if(x != null){
//                                    subItemName = x.getItemName();
//                                    Log.v("tag","sub item is "+subItemName);
//                                }
                                Intent i = new Intent(VendorItemDetailsActivity.this, com.example.ana.cngvendor.Activities.EditItemDetailActivity.class);
                                i.putExtra("editDetail","yes");
                                i.putExtra("subItemName",subItemName);
                                i.putExtra("itemName",item_name);
                                startActivity(i);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });
        mDetailList = new ArrayList<ItemDetail>();
        mDescriptionList = new ArrayList<ArrayList<String>>();

        /*mDetailList.add(new ItemDetail("Name A", "Rs 100", "Description A"));
        mDetailList.add(new ItemDetail("Name B", "Rs 100", "Description B"));
        mDetailList.add(new ItemDetail("Name C", "Rs 100", "Description C"));
        mDetailList.add(new ItemDetail("Name D", "Rs 100", "Description D"));
        mDetailList.add(new ItemDetail("Name E", "Rs 100", "Description E"));
        mDetailList.add(new ItemDetail("Name F", "Rs 100", "Description F"));
        mDetailList.add(new ItemDetail("Name G", "Rs 100", "Description G"));
        mDetailList.add(new ItemDetail("Name H", "Rs 100", "Description H"));
        mDetailList.add(new ItemDetail("Name I", "Rs 100", "Description I"));
        mDetailList.add(new ItemDetail("Name J", "Rs 100", "Description J"));*/

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mItemDetailDatabaseReference = mFirebaseDatabase.getReference().child(industryName).child(id).child(item_name.replaceAll("[^A-Za-z0-9 ]", ""));

        VendorItemDetailsActivity.fetchItemDetail fID = new VendorItemDetailsActivity.fetchItemDetail();
        fID.execute();

        /*mAdapter = new VendorItemDetailsAdapter(this, mDetailList);
        mListView.setAdapter(new SlideExpandableListAdapter(
                mAdapter,
                R.id.item_detail_view,
                R.id.item_description
        ));*/

//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                view.setVisibility(View.INVISIBLE);
//                ClipData data = ClipData.newPlainText("", "");
//                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
//                view.startDrag(data, shadow, null, 0);
//                return false;
//            }
//        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void updateUI(){
        bar.setVisibility(View.GONE);
        mAdapter = new VendorItemDetailsAdapter(mDetailList,mDescriptionList);
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setChildIndicator(null);
        if(mDetailList.isEmpty()){
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    public class fetchItemDetail extends AsyncTask<Void,Void,ArrayList<ItemDetail>> {

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<ItemDetail> doInBackground(Void... params) {

            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mDetailList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String iName = (String) snapshot.child("itemName").getValue();
                        ArrayList<String> iPrice = (ArrayList<String>) snapshot.child("itemPrice").getValue();
                        String iDesc = (String) snapshot.child("itemDescription").getValue();
                        ArrayList<String> iUrl = (ArrayList<String>) snapshot.child("itemUrl").getValue();
                        /*Trapping the price and Description????How???*/
                        if(iName != null && iPrice != null && iDesc != null) {
                            ItemDetail itemDetail = new ItemDetail(iName, iPrice, iDesc,iUrl);
                            mDetailList.add(itemDetail);
                            mDescriptionList.add(new ArrayList<String>());
                            mDescriptionList.get(i).add(iDesc);
                            i++;
                        }
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mItemDetailDatabaseReference.addValueEventListener(mValueEventListener);
            return null;
        }
    }


    public class VendorItemDetailsAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private ArrayList<ItemDetail> groups;
        private ArrayList<ArrayList<String>> children;

        public VendorItemDetailsAdapter(ArrayList<ItemDetail> groups, ArrayList<ArrayList<String>> children) {
            this.groups = groups;
            this.children = children;
            inf = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        @Override
        public ItemDetail getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            VendorItemDetailsAdapter.ChildHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.shop_detail_item_child, parent, false);
                holder = new VendorItemDetailsAdapter.ChildHolder();

                holder.description = (TextView) convertView.findViewById(R.id.item_description);
                //holder.text.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                convertView.setTag(holder);
            } else {
                holder = (VendorItemDetailsAdapter.ChildHolder) convertView.getTag();
            }

            holder.description.setText(getChild(groupPosition, childPosition).toString());
//        holder.text.setAutoLinkMask(Linkify.PHONE_NUMBERS);

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            VendorItemDetailsAdapter.GroupHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.shop_detail_item, parent, false);

                holder = new VendorItemDetailsAdapter.GroupHolder();
                holder.name = (TextView) convertView.findViewById(R.id.item_name);
                holder.price = (TextView) convertView.findViewById(R.id.item_price);
                holder.image = (ImageView) convertView.findViewById(R.id.item_image);
                convertView.setTag(holder);
            } else {
                holder = (VendorItemDetailsAdapter.GroupHolder) convertView.getTag();
            }

            holder.name.setText(getGroup(groupPosition).getItemName().toString());
            //holder.price.setText(getGroup(groupPosition).getItemPrice().toString());
            String prices="";
            for(int i = 0;i < getGroup(groupPosition).getItemPrice().size(); i++){
                if(!getGroup(groupPosition).getItemPrice().get(i).equals("0") && !getGroup(groupPosition).getItemPrice().get(i).equals("N.A")){
                    prices=prices+getGroup(groupPosition).getItemPrice().get(i)+"\n";
                }
            }
            holder.price.setText(prices);
            Log.v("tag1",prices);
            Glide.with(holder.image.getContext()).load(getGroup(groupPosition).getItemUrl().get(0)).into(holder.image);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class GroupHolder {
            TextView name;
            TextView price;
            ImageView image;

        }
        private  class ChildHolder{
            TextView description;
        }
    }

}
