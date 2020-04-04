package com.angik.android.mrpharmacy.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angik.android.mrpharmacy.R;

@SuppressWarnings("FieldCanBeLocal")
public class SlideAdapter extends PagerAdapter {

    private final Context context;
    private LayoutInflater layoutInflater;

    public SlideAdapter(Context context) {
        this.context = context;
    }

    private final int[] slideImages = {
            R.drawable.logo2,
            R.drawable.writing,
            R.drawable.prescription,
            R.drawable.right_arrow
    };

    private final String[] headings = {
            "", "Make Your Own Order", "Or Attach Your Prescription Write The Details", "And Send It To Us"
    };

    private final String[] slide_desc = {
            "Mr Pharmacy delivers your required medicine from your nearest store within a short possible time",
            "You can make your own order by inputting your required medicine's name, quantity, brand and type",
            "You can also send us the prescription with the details and we do the rest",
            "Send with ease and look for the current order status at home screen"
    };


    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = view.findViewById(R.id.image1);
        TextView textView = view.findViewById(R.id.text1);
        TextView textView1 = view.findViewById(R.id.text2);

        imageView.setImageResource(slideImages[position]);
        textView.setText(headings[position]);
        textView1.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
