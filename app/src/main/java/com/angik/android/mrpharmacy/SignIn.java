package com.angik.android.mrpharmacy;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.angik.android.mrpharmacy.Classes.BottomSheet;
import com.angik.android.mrpharmacy.Classes.CustomAdapterForOrder;

public class SignIn extends AppCompatActivity implements Home.OnFragmentInteractionListener, Profile.OnFragmentInteractionListener, Cart.OnFragmentInteractionListener, Store.OnFragmentInteractionListener, About.OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener, PresHistory.OnFragmentInteractionListener, BottomSheet.BottomSheetListener {

    private SharedPreferences sp_firstMarker;
    private SharedPreferences sp_secondMarker;
    private SharedPreferences sp_fourthMarker;

    /* Making our custom class variable to make get and set method to send the adapter from
     * Home fragment to Cart fragment
     * As we can not access the listView of Cart fragment from Home fragment
     * As a result we will not be able to set the adapter properly
     * That's why we need to send the adapter
     */
    private CustomAdapterForOrder adapter;

    private int location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment fragment = new Home();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    //toolbar.setTitle("Home");
                    fragment = new Home();
                    replaceFragment(fragment);
                    return true;
                case R.id.profile:
                    //toolbar.setTitle("Profile");
                    fragment = new Profile();
                    replaceFragment(fragment);
                    return true;
                case R.id.cart:
                    //toolbar.setTitle("Cart");
                    fragment = new Cart();
                    replaceFragment(fragment);
                    return true;
                case R.id.store:
                    //toolbar.setTitle("Cart");
                    fragment = new Store();
                    replaceFragment(fragment);
                    return true;
                case R.id.about:
                    //toolbar.setTitle("About");
                    fragment = new About();
                    replaceFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    /*private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container, fragment, backStateName);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    //setAdapter method where we set the adapter for later use
    public void setAdapter(CustomAdapterForOrder adapter) {
        this.adapter = adapter;
    }

    //getAdapter method where we can access the preset adapter
    public CustomAdapterForOrder getAdapter() {
        return this.adapter;
    }


    //Get location from the home spinner
    public void setLocation(int location) {
        this.location = location;
    }

    public int getLocation() {
        return this.location;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onButtonClicked(boolean isClicked) {
        sp_firstMarker = getSharedPreferences("isChecked", MODE_PRIVATE);
        sp_firstMarker.edit().clear().apply();
        sp_secondMarker = getSharedPreferences("isChecked", MODE_PRIVATE);
        sp_secondMarker.edit().clear().apply();
        sp_fourthMarker = getSharedPreferences("isChecked", MODE_PRIVATE);
        sp_fourthMarker.edit().clear().apply();

        SharedPreferences sp_currentOrderStatus = getSharedPreferences("currentOrderStatus", MODE_PRIVATE);
        sp_currentOrderStatus.edit().clear().apply();
        SharedPreferences sp_currentOrderID = getSharedPreferences("currentOrderID", MODE_PRIVATE);
        sp_currentOrderID.edit().clear().apply();
        SharedPreferences sp_currentOrderType = getSharedPreferences("currentOrderType", MODE_PRIVATE);
        sp_currentOrderType.edit().clear().apply();
    }
}