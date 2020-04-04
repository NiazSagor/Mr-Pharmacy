package com.angik.android.mrpharmacy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.angik.android.mrpharmacy.Classes.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public class HistoryDetail extends AppCompatActivity {

    private ListView listView;//Displaying medicine in list view

    //Array lists for the details of medicine
    private final ArrayList<String> brand = new ArrayList<>();
    private final ArrayList<String> name = new ArrayList<>();
    private final ArrayList<String> quantity = new ArrayList<>();
    private final ArrayList<String> type = new ArrayList<>();

    private DatabaseReference databaseReference;//Order Upload ref

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        listView = findViewById(R.id.historydetaillistview);
        final String code = getIntent().getStringExtra("currentOrder");//Getting id from other activity
        getValues(code);//Getting values with code
        OrderDetailAdapter adapter = new OrderDetailAdapter(this, name, quantity, brand, type);
        listView.setAdapter(adapter);
    }

    private void getValues(String code) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Order Upload").child(code);

        //Entering the child and grabbing the values
        databaseReference.child("mBrand").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                //Using a loop to store the values using index
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    brand.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("mName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    name.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("mQuantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    quantity.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("mType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    type.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
