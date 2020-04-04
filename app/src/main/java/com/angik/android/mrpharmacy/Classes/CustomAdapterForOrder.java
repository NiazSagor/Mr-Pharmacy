package com.angik.android.mrpharmacy.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.android.mrpharmacy.R;
import com.angik.android.mrpharmacy.SignIn;

import java.util.ArrayList;

@SuppressWarnings("NullableProblems")
public class CustomAdapterForOrder extends ArrayAdapter<String> {
    private final Context c;

    private final ArrayList<String> medicineName;
    private final ArrayList<String> medicineQuantity;
    private final ArrayList<String> medicineBrandName;
    private final ArrayList<String> medicineType;

    private CustomAdapterForOrder adapter;


    public CustomAdapterForOrder(Context c, ArrayList<String> medicineName, ArrayList<String> medicineQuantity, ArrayList<String> medicineBrandName, ArrayList<String> medicineType) {
        super(c, R.layout.orderlistview);

        this.c = c;
        this.medicineName = medicineName;
        this.medicineQuantity = medicineQuantity;
        this.medicineBrandName = medicineBrandName;
        this.medicineType = medicineType;
    }

    static class ViewHolder {
        TextView medicineName, medicineQuantity, quantity, medicineBrandName, medicineType;
        Button removeButton;
        ImageView minusButton, plusButton;
    }

    @Override
    public int getCount() {
        return medicineName.size();
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, @SuppressWarnings("NullableProblems") final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.orderlistview, parent, false);

        final ViewHolder holder = new ViewHolder();

        holder.medicineName = view.findViewById(R.id.name);//Medicine name with power
        holder.medicineQuantity = view.findViewById(R.id.medQuantity);//Below name, quantity which is sent by the user via AddToCart button

        holder.medicineType = view.findViewById(R.id.medType);//Side of quantity, type
        holder.medicineBrandName = view.findViewById(R.id.medBrand);//Side of type, brand
        holder.medicineBrandName.setAllCaps(true);//Makes letters all capital

        holder.quantity = view.findViewById(R.id.quantity);//This view is in the middle of plus and minus button


        holder.removeButton = view.findViewById(R.id.removeButton);//Remove button in the list, deletes an entry
        holder.minusButton = view.findViewById(R.id.minusButton);//Minus button, decreases the quantity
        holder.plusButton = view.findViewById(R.id.plusButton);//Plus button, increases the quantity

        //Setting the values we got from the array list, in their view position
        holder.medicineName.setText(medicineName.get(position));
        holder.medicineQuantity.setText(medicineQuantity.get(position));
        holder.medicineType.setText(medicineType.get(position));
        holder.medicineBrandName.setText(medicineBrandName.get(position));

        holder.quantity.setText(medicineQuantity.get(position));//This quantity will be same as medicine quantity

        //Reduces quantity by one
        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String plusMinusQuantity = medicineQuantity.get(position);//Getting the current value by the array list using position
                int medQuantity = Integer.parseInt(plusMinusQuantity);//Making the value integer type so that we can change the value

                if (medQuantity == 1) {
                    Toast toast = Toast.makeText(getContext(), "Can not have less than 1", Toast.LENGTH_SHORT);//Using getContext() as we are using Toast in class not in activity or fragment
                    toast.show();
                    return;
                }

                medQuantity--;//Changing the value by one

                //Setting the updated value in the 2 quantity TextViews
                holder.quantity.setText("" + medQuantity);
                holder.medicineQuantity.setText("" + medQuantity);

                //Finally updating the main ArrayList with the new changed one
                medicineQuantity.set(position, String.valueOf(medQuantity));
            }
        });

        //Increases quantity by one
        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String plusMinusQuantity = medicineQuantity.get(position);
                int medQuantity = Integer.parseInt(plusMinusQuantity);

                if (medQuantity == 100) {
                    Toast toast = Toast.makeText(getContext(), "Can not have more than 100", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                medQuantity++;
                holder.quantity.setText("" + medQuantity);
                holder.medicineQuantity.setText("" + medQuantity);
                medicineQuantity.set(position, String.valueOf(medQuantity));
            }
        });

        //Removes an entry from the cart
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deleting the values in the ArrayLists
                medicineName.remove(position);
                medicineQuantity.remove(position);
                medicineBrandName.remove(position);
                medicineType.remove(position);

                adapter = ((SignIn) getContext()).getAdapter();//Grabbing the custom adapter
                adapter.notifyDataSetChanged();//Notifying adapter that data is changed and update the list
            }
        });
        return view;
    }

    public ArrayList<String> getMedicineName() {
        return medicineName;
    }

    public ArrayList<String> getMedicineBrand() {
        return medicineBrandName;
    }

    public ArrayList<String> getMedicineQuantity() {
        return medicineQuantity;
    }

    public ArrayList<String> getMedicineType() {
        return medicineType;
    }

}
