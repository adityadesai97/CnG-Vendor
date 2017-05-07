package com.example.ana.cngvendor.NavDrawerFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ana.cngvendor.R;

/**
 * Created by adityadesai on 12/02/17.
 */

public class OffersPageFragment extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.offers_page,null);
    }
}
