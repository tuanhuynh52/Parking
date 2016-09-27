package com.tuan.parking.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuan.parking.R;

/**
 * Created by Tuan on 9/26/2016.
 */

public class CustomListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] parking;
    private final Integer[] imgId;


    public CustomListAdapter(Activity context, String[] parking, Integer[] imgid) {
        super(context, R.layout.row_layout, parking);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.parking = parking;
        this.imgId = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_layout, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.ic_parking);
        TextView parking_price = (TextView) rowView.findViewById(R.id.parking_price);
        TextView name_address = (TextView) rowView.findViewById(R.id.parking_name_address);

        imageView.setImageResource(imgId[position]);
        parking_price.setText("$"+ parking[position]);
        name_address.setText(parking[position]);
        return rowView;
    }
}
