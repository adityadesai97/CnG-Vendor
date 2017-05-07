package com.example.ana.cngvendor.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ana.cngvendor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityadesai on 07/05/17.
 */

public class PriceTableAdapter extends ArrayAdapter<String> {


    public PriceTableAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PriceTableAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        String p = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(android.R.layout.simple_list_item_1, null);
        }

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(android.R.id.text1);

            if (tt1 != null) {
                tt1.setText(p);
            }
        }

        return v;
    }


    @Override
    public int getCount() {
        return super.getCount();
    }
}
