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

@SuppressWarnings("NullableProblems")
public class OrderDetailAdapter extends ArrayAdapter<String> {
    private final Context c;

    private final ArrayList<String> medicineName;
    private final ArrayList<String> medicineQuantity;
    private final ArrayList<String> medicineBrandName;
    private final ArrayList<String> medicineType;


    public OrderDetailAdapter(Context c, ArrayList<String> medicineName, ArrayList<String> medicineQuantity, ArrayList<String> medicineBrandName, ArrayList<String> medicineType) {
        super(c, R.layout.history_detail);

        this.c = c;
        this.medicineName = medicineName;
        this.medicineQuantity = medicineQuantity;
        this.medicineBrandName = medicineBrandName;
        this.medicineType = medicineType;
    }

    static class ViewHolder {
        TextView medicineName, medicineQuantity, medicineBrandName, medicineType;
    }

    @Override
    public int getCount() {
        return medicineName.size();
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, @SuppressWarnings("NullableProblems") final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.history_detail, parent, false);

        final ViewHolder holder = new ViewHolder();

        holder.medicineName = view.findViewById(R.id.medName);
        holder.medicineQuantity = view.findViewById(R.id.medQuantity);
        holder.medicineBrandName = view.findViewById(R.id.medBrand);
        holder.medicineType = view.findViewById(R.id.medType);

        //Setting the values we got from the array list, in their view position
        holder.medicineName.setText(medicineName.get(position));
        holder.medicineQuantity.setText(medicineQuantity.get(position));
        holder.medicineType.setText(medicineType.get(position));
        holder.medicineBrandName.setText(medicineBrandName.get(position));

        return view;
    }
}
