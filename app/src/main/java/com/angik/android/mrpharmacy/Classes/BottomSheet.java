package com.angik.android.mrpharmacy.Classes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.angik.android.mrpharmacy.R;

import java.util.Objects;


public class BottomSheet extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        TextView textView = view.findViewById(R.id.text);
        TextView textView1 = view.findViewById(R.id.text1);
        String string = Objects.requireNonNull(getArguments()).getString("order");
        String address = getArguments().getString("address");
        textView.setText(string);
        textView1.setText(address);
        Button button = view.findViewById(R.id.buttonYes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked(true);
                dismiss();
            }
        });
        return view;
    }

    public interface BottomSheetListener {
        void onButtonClicked(boolean isClicked);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
