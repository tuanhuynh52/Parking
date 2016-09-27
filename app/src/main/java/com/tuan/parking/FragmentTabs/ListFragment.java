package com.tuan.parking.FragmentTabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tuan.parking.MainActivity;
import com.tuan.parking.Model.Parking;
import com.tuan.parking.R;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment{

    private static final String TAG = "ListFragment";

    public List<Parking> getParkingList() {
        return parkingList;
    }

    public void setParkingList(List<Parking> parkingList) {
        this.parkingList = parkingList;
    }

    public List<Parking> parkingList;
    ListView listView;


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);


//        for (int i=0; i < parkingList.size(); i++){
//
//        }
//        String[] parking = parkingList.get()
//
//        listView = (ListView)v.findViewById(R.id.listview);
//        CustomListAdapter adapter = new CustomListAdapter(getActivity(), )
        if (MainActivity.parkingList == null) {
            Toast.makeText(getActivity(), "Parking list in list fragment is null", Toast.LENGTH_LONG).show();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        parkingList = MainActivity.parkingList;
//        Log.i(TAG, parkingList.size()+"");
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }
}
