package com.angik.android.mrpharmacy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.android.mrpharmacy.Classes.Common;
import com.angik.android.mrpharmacy.Classes.CustomAdapterForOrder;
import com.angik.android.mrpharmacy.Classes.NonScrollListView;
import com.angik.android.mrpharmacy.Classes.Upload;
import com.angik.android.mrpharmacy.Classes.UploadCart;
import com.angik.android.mrpharmacy.Classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Cart#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings({"StatementWithEmptyBody", "FieldCanBeLocal", "unused"})
public class Cart extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    @SuppressWarnings("FieldCanBeLocal")
    private String mParam1;
    private String mParam2;

    private CustomAdapterForOrder adapter;//For catching the adapter thrown by the Home fragment

    private NonScrollListView listView;//ListView for the Cart where medicine shows up

    private TextView itemCount;

    private Button selectImageButton;//Selects prescription
    private Button uploadImageButton;//Uploads prescription

    private Button sendOrder;//Sends order from the array lists

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri mImageUri;//To hold loaded image's uri

    private ProgressBar progressBar;//Progress bar for uploading

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference_2;
    private DatabaseReference dr_history;//User history database
    private DatabaseReference dr_presHistory;//User history database with prescription

    private StorageTask uploadTask;//Keep tracking of the current task

    private EditText presName;
    private EditText presDetails;//For user details input about prescription

    private SharedPreferences sharedPreferences;//This sp gives us the object that was saved during user login in LogIn activity
    private SharedPreferences sp_orderCount;//Normal upload count
    private SharedPreferences sp_PresOrderCount;//With pres upload count
    private SharedPreferences sp_currentOrderStatus;//To store current order status
    private SharedPreferences sp_currentOrderID;//To store current order id
    private SharedPreferences sp_currentOrderType;//To store current order id

    private OnFragmentInteractionListener mListener;


    public Cart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Cart.
     */
    // TODO: Rename and change types and number of parameters
    public static Cart newInstance(String param1, String param2) {
        Cart fragment = new Cart();
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


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@SuppressWarnings("NullableProblems") LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ////Database and storage ref of Prescription node
        storageReference = FirebaseStorage.getInstance().getReference("Prescription Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription Uploads");

        //Fetching stored class/object saved in shared preferences
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", "");//Getting json string

        /* Retrieving the saved object
         * Now this Common.currentUser is getting information from sp which is saved in the device, not from the User class
         */
        Common.currentUser = gson.fromJson(json, User.class);

        //Database ref for Order Upload node
        databaseReference_2 = FirebaseDatabase.getInstance().getReference("Order Upload");


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        itemCount = view.findViewById(R.id.itemCount);

        adapter = ((SignIn) Objects.requireNonNull(getContext())).getAdapter();//Getting the adapter which we have saved previously
        itemCount.setText("Items in Cart (" + adapter.getCount() + ")");//Getting the items count from the adapter

        listView = view.findViewById(R.id.orderListView);//Finding the lost view
        listView.setAdapter(adapter);//Setting the adapter

        sendOrder = view.findViewById(R.id.sendOrder);//Sends order in the cart
        if (adapter.getCount() > 0) {
            sendOrder.setVisibility(View.VISIBLE);
        }
        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking if there is 0 items in cart
                if (adapter.getCount() == 0) {
                    Toast.makeText(getActivity(), "Sorry! Cart is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Alert dialog box to confirm the order
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Confirm Your Order?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                orderToDatabase();//If yes then send the order
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();//If no close the dialog box
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        selectImageButton = view.findViewById(R.id.selectImageButton);//Selecting image from device
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();//Opens storage
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If image is loaded successfully
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            mImageUri = data.getData();//Holding the selected image
            Toast.makeText(getActivity(), "One more Step!", Toast.LENGTH_LONG).show();//Notify user

            //If image loaded successfully then show the alert dialog of prescription name details
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            @SuppressLint("InflateParams") View dialog = getLayoutInflater().inflate(R.layout.upload_dialog, null);//Grabbing our dialog layout

            //Prescription name and details input in dialog box
            presName = dialog.findViewById(R.id.prescriptionName);
            presDetails = dialog.findViewById(R.id.presDetailsInput);

            //Upload button in the dialog box
            uploadImageButton = dialog.findViewById(R.id.uploadImage);
            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("UnnecessaryReturnStatement")
                @Override
                public void onClick(View view) {
                    //Checking if there is any task ongoing otherwise upload the file
                    if (uploadTask != null && uploadTask.isInProgress()) {
                        Toast.makeText(getActivity(), "Upload is in Progress", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (presName.getText().toString().equals("") || presDetails.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please fill in the information", Toast.LENGTH_SHORT).show();
                        } else {
                            //Alert dialog box to confirm the order
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("Confirm Your Order?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            uploadFile();//If yes then send the order
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();//If no close the dialog box
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    }
                }
            });
            //Progress bar for file upload
            progressBar = dialog.findViewById(R.id.progressbar);

            builder.setView(dialog);
            AlertDialog dialog1 = builder.create();
            dialog1.show();
        }
    }

    //File choosing prompt
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //To get the file extension of the image file which is chosen
    private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(getContext()).getContentResolver();//Using getContext() as we are using this in Fragment
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //Uploads to the database
    private void uploadFile() {
        sp_PresOrderCount = Objects.requireNonNull(getActivity()).getSharedPreferences("presOrderCount", Context.MODE_PRIVATE);
        if (mImageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));//File name which will be saved in the database

            //Tracking current upload task
            uploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);


                            dr_presHistory = FirebaseDatabase.getInstance().getReference("Prescription History").child(Common.currentUser.getPhone());
                            dr_presHistory.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int i = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                                    sp_PresOrderCount.edit().putInt("count", i).apply();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            int i = sp_PresOrderCount.getInt("count", 0);


                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            //Making Upload type object to push data into the database
                            assert downloadUrl != null;
                            Upload upload = new Upload(presName.getText().toString().trim(), presDetails.getText().toString().trim()
                                    , downloadUrl.toString(), Common.currentUser.getName(), Common.currentUser.getPhone(), Common.currentUser.getAdd(), "Pending", "NO");

                            String uploadId = databaseReference.push().getKey();//Unique id which is given
                            assert uploadId != null;
                            databaseReference.child(uploadId).setValue(upload);//Setting values

                            dr_presHistory.child(String.valueOf(i)).setValue(uploadId);
                            setSharedPreferences(uploadId, "With Prescription");
                            Toast.makeText(getActivity(), "Photo Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    //Sends cart to database
    private void orderToDatabase() {
        //Making another array list to store the user details from sp
        ArrayList<String> userDetails = new ArrayList<>();
        userDetails.add(Common.currentUser.getName());
        userDetails.add(Common.currentUser.getPhone());
        userDetails.add(Common.currentUser.getAdd());

        //Uploads cart to Order Upload database
        UploadCart upload = new UploadCart(adapter.getMedicineName(), adapter.getMedicineQuantity(), adapter.getMedicineBrand(),
                adapter.getMedicineType(), userDetails, "Pending", "NO");//Getting medicine name array by get method

        //For storing uploaded cart orders count
        sp_orderCount = Objects.requireNonNull(getActivity()).getSharedPreferences("orderCount", Context.MODE_PRIVATE);

        dr_history = FirebaseDatabase.getInstance().getReference("History").child(Common.currentUser.getPhone());
        dr_history.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));//Get current count
                sp_orderCount.edit().putInt("count", i).apply();//Putting it in the sp
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        int i = sp_orderCount.getInt("count", 0);

        String uploadId = databaseReference_2.push().getKey();//Generates a Current order upload id
        assert uploadId != null;
        databaseReference_2.child(uploadId).setValue(upload);//Order Upload database reference
        dr_history.child(String.valueOf(i)).setValue(uploadId);//Increasing order count in History by 1

        setSharedPreferences(uploadId, "Cart");

        Toast.makeText(getActivity(), "Order Sent", Toast.LENGTH_LONG).show();
    }

    private void setSharedPreferences(String uploadId, String orderType) {
        //Getting sp
        sp_currentOrderStatus = Objects.requireNonNull(getActivity()).getSharedPreferences("currentOrderStatus", MODE_PRIVATE);
        sp_currentOrderID = getActivity().getSharedPreferences("currentOrderID", MODE_PRIVATE);
        sp_currentOrderType = getActivity().getSharedPreferences("currentOrderType", MODE_PRIVATE);

        sp_currentOrderStatus.edit().putBoolean("status", true).apply();//Setting current status true
        sp_currentOrderID.edit().putString("id", uploadId).apply();//Storing current upload id
        sp_currentOrderType.edit().putString("type", orderType).apply();//Storing current upload type Cart or prescription
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
