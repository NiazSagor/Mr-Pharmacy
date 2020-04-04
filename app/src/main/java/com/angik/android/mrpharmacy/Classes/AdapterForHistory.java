package com.angik.android.mrpharmacy.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.angik.android.mrpharmacy.R;

import java.util.ArrayList;

public class AdapterForHistory extends ArrayAdapter {

    private final Context mC;
    private final ArrayList<String> mSerialNo;
    private final ArrayList<String> mId;

    public AdapterForHistory(Context c, ArrayList<String> serialNo, ArrayList<String> id) {
        super(c, R.layout.carthistory);

        this.mC = c;
        this.mSerialNo = serialNo;
        this.mId = id;
    }

    static class ViewHolder {
        TextView serialNo, orderID;
    }


    @Override
    public int getCount() {
        return mSerialNo.size();
    }

    @SuppressWarnings("NullableProblems")
    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, @SuppressWarnings("NullableProblems") final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mC.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.carthistory, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.serialNo = view.findViewById(R.id.serialNo);
        viewHolder.orderID = view.findViewById(R.id.orderId);

        viewHolder.serialNo.setText(mSerialNo.get(position));
        viewHolder.orderID.setText(mId.get(position));

        return view;
    }
}
