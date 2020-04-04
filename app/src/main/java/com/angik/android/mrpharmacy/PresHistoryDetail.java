package com.angik.android.mrpharmacy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.angik.android.mrpharmacy.Classes.Common;
import com.angik.android.mrpharmacy.Classes.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PresHistoryDetail extends AppCompatActivity {

    private DatabaseReference databaseReference;//Accessing Prescription Uploads database
    private ImageView imageView;
    private TextView textView;
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pres_history_detail);

        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.imagename);
        textView1 = findViewById(R.id.detail);

        final String code = getIntent().getStringExtra("id");//Current selected order id
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription Uploads").child(code);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.currentUpload = dataSnapshot.getValue(Upload.class);//Keeping each record in the Upload class which is in Common class
                //Getting values from the saved object
                Picasso.get().load(Common.currentUpload.getImageUrl()).into(imageView);
                textView.setText(Common.currentUpload.getName());
                textView1.setText(Common.currentUpload.getDetails());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
