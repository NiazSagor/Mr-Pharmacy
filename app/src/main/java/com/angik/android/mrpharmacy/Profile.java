package com.angik.android.mrpharmacy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.angik.android.mrpharmacy.Classes.Common;
import com.angik.android.mrpharmacy.Classes.User;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Profile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //User user = new User();

    private String date;//To store the date
    private TextView memberSince;
    private TextView userName;
    private TextView userPhone;
    private TextView userAddress;
    private Button logOutButton;//Log out button in profile

    private SharedPreferences spForWalkThrough;
    private SharedPreferences sharedPreferencesForUserObject;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesForDate;//Initializing shared preferences to clear all the previous save data in the device as the user logs out


    private OnFragmentInteractionListener mListener;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@SuppressWarnings("NullableProblems") LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferencesForDate = Objects.requireNonNull(getActivity()).getSharedPreferences("date", Context.MODE_PRIVATE);//Loading shared preferences named date
        date = sharedPreferencesForDate.getString("date", date);//Getting the stored string
        spForWalkThrough = getActivity().getSharedPreferences("started", Context.MODE_PRIVATE);

        //Member since text view
        memberSince = view.findViewById(R.id.memberSince);
        memberSince.setText(date);

        sharedPreferencesForUserObject = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferencesForUserObject.getString("user", "");//Getting json string
        Common.currentUser = gson.fromJson(json, User.class);

        //User name under the profile picture
        userName = view.findViewById(R.id.userName);
        userName.setText(Common.currentUser.getName());
        userPhone = view.findViewById(R.id.userPhone);
        userPhone.setText(Common.currentUser.getPhone());
        userAddress = view.findViewById(R.id.userAddress);
        userAddress.setText(Common.currentUser.getAdd());

        logOutButton = view.findViewById(R.id.logOutButton);//Finding the button via view

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);//Loading previous saved data

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("Please Wait");
                mDialog.show();
                Intent intent = new Intent(getActivity(), LogIn.class);//Intent to go back to the log in activity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();//Clearing all the saved data
                editor.apply();
                spForWalkThrough.edit().clear().apply();
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();//Restricting the SignIn activity being in the backStack as the user does not need it anymore
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
