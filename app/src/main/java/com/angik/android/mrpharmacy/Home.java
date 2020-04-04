package com.angik.android.mrpharmacy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.android.mrpharmacy.Classes.BottomSheet;
import com.angik.android.mrpharmacy.Classes.Common;
import com.angik.android.mrpharmacy.Classes.CustomAdapterForOrder;
import com.angik.android.mrpharmacy.Classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner spinner;//Spinner for medicine type in the dialog

    private final int interval = 120000;

    private CardView orderCreate;//CardView onClick response
    private CardView orderHistory;//CardView onClick response
    private CardView historyPres;//CardView onClick response

    private int medCount = 2;//Initial count of the medicine

    private String medType;//Medicine type from spinner

    private static final ArrayList<String> medicineName = new ArrayList<>();
    private static final ArrayList<String> medicineQuantity = new ArrayList<>();
    private static final ArrayList<String> medicineBrandName = new ArrayList<>();
    private static final ArrayList<String> medicineType = new ArrayList<>();

    private DatabaseReference db_ref_currentUser;//We want to get the current status of the order with the current user and current upload id
    private RelativeLayout layout;//If current order status is taken then we want to reveal a layout otherwise stays hidden

    private SharedPreferences sp_firstMarker;
    private SharedPreferences sp_secondMarker;
    private SharedPreferences sp_thirdMarker;

    private SharedPreferences sharedPreferencesForUserObject;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        //Getting the whole container in a variable
        final View v = inflater.inflate(R.layout.fragment_home, container, false);
        layout = v.findViewById(R.id.layout);

        RelativeLayout layout = v.findViewById(R.id.layout);//Finding the order status layout
        layout.setVisibility(View.GONE);//Setting is gone primarily

        statusCheck(v);//Then check for the status

        //Making an adapter at the beginning of the Home fragment
        CustomAdapterForOrder adapterForOrder = new CustomAdapterForOrder(getActivity(), medicineName, medicineQuantity, medicineBrandName, medicineType);
        ((SignIn) Objects.requireNonNull(getContext())).setAdapter(adapterForOrder);//Setting and storing this adapter for later use in Cart Fragment

        orderHistory = v.findViewById(R.id.history);
        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HistoryFragment();
                String backStateName = fragment.getClass().getName();

                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

                if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame_container, fragment, backStateName);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            }
        });

        historyPres = v.findViewById(R.id.historyPres);
        historyPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PresHistory();
                String backStateName = fragment.getClass().getName();

                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

                if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame_container, fragment, backStateName);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            }
        });


        orderCreate = v.findViewById(R.id.orderCreate);
        orderCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //Getting the whole dialog layout in a variable
                @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.medicine_select_dialog, null);

                final EditText medicineNameInput = mView.findViewById(R.id.medicineNameInput);//Medicine name input
                final EditText medicineBrandInput = mView.findViewById(R.id.medicineBrandInput);//Medicine brand input
                final EditText medicinePowerInput = mView.findViewById(R.id.medicinePowerInput);//Medicine power input

                ImageButton removeButton = mView.findViewById(R.id.removeButton);//Removes medicine count
                ImageButton addButton = mView.findViewById(R.id.addButton);//Adds medicine count
                Button addToCartButton = mView.findViewById(R.id.addToCartButton);//Add to Cart button

                final TextView medCountTextView = mView.findViewById(R.id.medCount);//Medicine count

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        if (medCount == 1) {
                            medCountTextView.setText("" + medCount);
                            Toast toast = Toast.makeText(getActivity(), "You can not have less than 1", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        medCount = medCount - 1;//Decreases the count of the medicine
                        medCountTextView.setText("" + medCount);
                    }
                });

                addButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        if (medCount == 100) {
                            Toast toast = Toast.makeText(getActivity(), "You can not have more than 100", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        medCount = medCount + 1;//Increases the count of the medicine
                        medCountTextView.setText("" + medCount);
                    }
                });

                //Medicine type spinner
                Spinner spinner = mView.findViewById(R.id.medicineTypeSpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                        R.array.medicineType, android.R.layout.simple_spinner_item);//Setting the layout and array to display
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);


                //On click listener for the medicine type spinner
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //Getting the selected item into medType
                        medType = adapterView.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //Add to cart button clicked
                addToCartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Getting the values from edit texts
                        String medName = medicineNameInput.getText().toString();
                        String medBrand = medicineBrandInput.getText().toString();
                        String medPower = medicinePowerInput.getText().toString();
                        String medQuantity = medCountTextView.getText().toString();

                        //Checking if the input is empty or not
                        if (medName.equals("") || medBrand.equals("") || medPower.equals("") || medType.equals("")) {
                            Toast.makeText(getActivity(), "Please fill all the FIELDS", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //Making a string combining name and power
                        String medNameWithPower = medName + " " + medPower + " mg/ml";

                        //Adding these values in the ArrayList which we have created
                        medicineName.add(medNameWithPower);
                        medicineQuantity.add(medQuantity);
                        medicineBrandName.add(medBrand);
                        medicineType.add(medType);//Med type is a global variable

                        Toast.makeText(getActivity(), "Added to CART", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setView(mView);
                AlertDialog dialog = builder.create();
                Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.show();
            }
        });

        //Location spinner in the Home fragment
        spinner = v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                R.array.areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((SignIn) Objects.requireNonNull(getContext())).setLocation(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return v;
    }

    @SuppressLint("SetTextI18n")
    private void statusCheck(final View view) {
        //Getting the previously made sp from the Cart fragment
        SharedPreferences sp_currentOrderStatus = Objects.requireNonNull(getActivity()).getSharedPreferences("currentOrderStatus", MODE_PRIVATE);
        SharedPreferences sp_currentOrderID = getActivity().getSharedPreferences("currentOrderID", MODE_PRIVATE);
        SharedPreferences sp_currentOrderType = getActivity().getSharedPreferences("currentOrderType", MODE_PRIVATE);

        //Getting the values from sp
        boolean b = sp_currentOrderStatus.getBoolean("status", false);
        String id = sp_currentOrderID.getString("id", "");
        String type = sp_currentOrderType.getString("type", "");

        final RelativeLayout currentOrderStatusLayout = view.findViewById(R.id.layout);//Finding the relative layout

        //If current order status is true and if it is Cart type
        assert type != null;
        if (b && type.equals("Cart")) {

            TextView textView = view.findViewById(R.id.orderStatusTextView);//Finding current status text view
            textView.setText("One Order In Progress");//Notifying that order is in progress
            currentOrderStatusLayout.setVisibility(View.VISIBLE);//Making it visible

            setViews(view, type, id);//Calling setView method to set the condition for the relative layout
        } else if (b && type.equals("With Prescription")) {
            TextView textView = view.findViewById(R.id.orderStatusTextView);
            textView.setText("One Order In Progress");
            currentOrderStatusLayout.setVisibility(View.VISIBLE);

            setViews(view, type, id);
        }
    }

    private void setViews(final View view, final String type, final String id) {
        /* Making sp for storing the current condition for the 3 markers, 1st, 2nd and 4 th
         * 3rd and 5th markers are controlled by the database
         * So no need to store them
         */
        sp_firstMarker = Objects.requireNonNull(getActivity()).getSharedPreferences("isChecked", MODE_PRIVATE);
        sp_secondMarker = getActivity().getSharedPreferences("isChecked", MODE_PRIVATE);
        sp_thirdMarker = getActivity().getSharedPreferences("isChecked", MODE_PRIVATE);

        //Markers and their text views
        final ImageView m1, m2, m3, m4, m5;
        final TextView t1, t2, t3, t4, t5;

        //Finding them by the view
        m1 = view.findViewById(R.id.fifthMarker);
        m2 = view.findViewById(R.id.fourthMarker);
        m3 = view.findViewById(R.id.thirdMarker);
        m4 = view.findViewById(R.id.secondMarker);
        m5 = view.findViewById(R.id.firstMarker);

        t1 = view.findViewById(R.id.fifthTextView);
        t2 = view.findViewById(R.id.fourthTextView);
        t3 = view.findViewById(R.id.thirdTextView);
        t4 = view.findViewById(R.id.secondTextView);
        t5 = view.findViewById(R.id.firstTextView);

        //Checking in the sp if the markers are already checked, if yes put them in the checked state
        if (sp_firstMarker.getBoolean("checked", false)) {
            markerAndSP(m1, t1, sp_firstMarker);
        }
        if (sp_secondMarker.getBoolean("checked", false)) {
            markerAndSP(m2, t2, sp_secondMarker);
        }
        if (sp_thirdMarker.getBoolean("checked", false)) {
            markerAndSP(m3, t3, sp_thirdMarker);
        }

        //If not checked from before then start checking them
        markerAndSP(m1, t1, sp_firstMarker);//Checks the first marker
        changeMarker(m2, t2, sp_secondMarker);//After some time checks the second marker (Using Thread)
        //For the 3rd marker we need to check the condition of the database
        if (type.equals("Cart")) {
            //If type is cart then go to the Order Upload database and check the status
            db_ref_currentUser = FirebaseDatabase.getInstance().getReference("Order Upload").child(id);
            db_ref_currentUser.child("mStatus").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String orderStatus = dataSnapshot.getValue(String.class);
                        assert orderStatus != null;
                        //If taken then mark the marker checked
                        if (orderStatus.equals("Order is taken")) {
                            m4.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                            t4.setTypeface(t4.getTypeface(), Typeface.BOLD);
                            changeMarker(m3, t3, sp_thirdMarker);//Then proceed for the next marker which is also be checked by Thread
                            changeFifthMarker(type, id, m5, t5);//At last proceed for the 5th marker
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (type.equals("With Prescription")) {
            db_ref_currentUser = FirebaseDatabase.getInstance().getReference("Prescription Uploads").child(id);
            db_ref_currentUser.child("mStatus").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String orderStatus = dataSnapshot.getValue(String.class);
                        assert orderStatus != null;
                        if (orderStatus.equals("Order is taken")) {
                            m4.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                            t4.setTypeface(t4.getTypeface(), Typeface.BOLD);
                            changeMarker(m3, t3, sp_thirdMarker);
                            changeFifthMarker(type, id, m5, t5);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void changeFifthMarker(String type, String id, final ImageView imageView, final TextView textView) {
        if (type.equals("Cart")) {
            db_ref_currentUser = FirebaseDatabase.getInstance().getReference("Order Upload").child(id);
            db_ref_currentUser.child("mReached").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String orderStatus = dataSnapshot.getValue(String.class);
                        assert orderStatus != null;
                        //If Reached then mark the marker and set the text to notify user
                        if (orderStatus.equals("YES")) {
                            imageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                            TextView textView1 = Objects.requireNonNull(getView()).findViewById(R.id.orderStatusTextView);
                            textView1.setText("YOUR ORDER IS AT YOUR LOCATION");
                            openBottomSheet();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (type.equals("With Prescription")) {
            db_ref_currentUser = FirebaseDatabase.getInstance().getReference("Prescription Uploads").child(id);
            db_ref_currentUser.child("mReached").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String orderStatus = dataSnapshot.getValue(String.class);
                        assert orderStatus != null;
                        if (orderStatus.equals("YES")) {
                            imageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                            TextView textView1 = Objects.requireNonNull(getView()).findViewById(R.id.orderStatusTextView);
                            textView1.setText("YOUR ORDER IS AT YOUR LOCATION");
                            openBottomSheet();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //Making a general method to change the image text and sp for the checked markers
    private void markerAndSP(ImageView imageView, TextView textView, SharedPreferences sp) {
        imageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        sp.edit().putBoolean("checked", true).apply();
    }

    //Making a thread to change a marker after a certain period of time
    private void changeMarker(final ImageView imageView, final TextView textView, final SharedPreferences sp) {// 5 Second
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                markerAndSP(imageView, textView, sp);
            }
        };
        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);
    }

    private void openBottomSheet() {
        sharedPreferencesForUserObject = Objects.requireNonNull(getActivity()).getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferencesForUserObject.getString("user", "");//Getting json string
        Common.currentUser = gson.fromJson(json, User.class);
        BottomSheet bottomSheet = new BottomSheet();
        bottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "example");
        Bundle bundle = new Bundle();
        bundle.putString("order", "Order Type : Regular");
        bundle.putString("address", Common.currentUser.getAdd());
        bottomSheet.setArguments(bundle);
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
