package com.example.ana.cngvendor.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ana.cngvendor.Activities.EditItemListActivity;
import com.example.ana.cngvendor.Activities.VendorItemDetailsActivity;
import com.example.ana.cngvendor.Activities.VendorItemsListActivity;
import com.example.ana.cngvendor.Objects.MenuItem;
import com.example.ana.cngvendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by adityadesai on 11/02/17.
 */


public class VendorItemListAdapter extends RecyclerView.Adapter<VendorItemListAdapter.MenuHolder>{

    static Context context;

    private static ArrayList<MenuItem> mMenuItems;
    private int lastPosition = -1;


    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItemName;
        private String mMenuItem;
        LinearLayout container;
        private MenuItem sMenuItem;

        private String itemName;

        public MenuHolder(View v) {
            super(v);

            mItemName = (TextView) v.findViewById(R.id.industry_name);
            container = (LinearLayout) v.findViewById(R.id.industryRootLayout);
            v.setOnClickListener(this);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    new AlertDialog.Builder(context)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = getAdapterPosition();
                                    sMenuItem = mMenuItems.get(position);
                                    if(sMenuItem != null){
                                        itemName = sMenuItem.getItemName();
                                    }

                                    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    final DatabaseReference databaseReference = firebaseDatabase.getReference().
                                            child(VendorItemsListActivity.industryName).
                                            child(VendorItemsListActivity.id);

                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                if(snapshot.hasChild("itemName") && snapshot.child("itemName").getValue().toString().equals(itemName)){
                                                    snapshot.getRef().removeValue();
                                                    databaseReference.child(snapshot.child("itemName").getValue().toString()).removeValue();
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
                                    int posn = getAdapterPosition();
                                    sMenuItem = mMenuItems.get(posn);
                                    if(sMenuItem != null){
                                        itemName = sMenuItem.getItemName();
                                    }
                                    Intent i = new Intent(v.getContext(), EditItemListActivity.class);
                                    i.putExtra("editItem","yes");
                                    i.putExtra("itemName",itemName);
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
            Intent i=new Intent(v.getContext(), VendorItemDetailsActivity.class);
            i.putExtra("Item",mMenuItem);
            i.putExtra("industryName",VendorItemsListActivity.industryName);
            i.putExtra("id",VendorItemsListActivity.id);
            v.getContext().startActivity(i);
        }

        public void bindIndustry(String menuItem) {
            mMenuItem = menuItem;
            mItemName.setText(mMenuItem);
        }
    }

    public VendorItemListAdapter(ArrayList<MenuItem> menuItem, Context context) {
        mMenuItems = menuItem;
        this.context = context;
    }

    @Override
    public VendorItemListAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.industry_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(VendorItemListAdapter.MenuHolder holder, int position) {
        String menuItem = mMenuItems.get(position).getItemName();
        holder.bindIndustry(menuItem);
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
        return mMenuItems.size();
    }
}
