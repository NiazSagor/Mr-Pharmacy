package com.angik.android.mrpharmacy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.angik.android.mrpharmacy.Classes.Common;
import com.angik.android.mrpharmacy.Classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class LogIn extends AppCompatActivity {

    private TextView textView; //Sign up TextView
    private EditText phoneInput;
    private EditText passInput; //User Input
    private Button signInButton; //Login button

    /* We want to be able to have all the User information for later use purpose if the user can successfully log in
     * But though we use 'Common.currentUser = user' line 115, to store current logged in user in a User variable
     * this 'Common.currentUser' looses information if the app is removed from background
     * So we are making a shared preference which can store Object type variable
     * so that we can use getName() or the other methods in the User class to get the user data, in future
     */
    private SharedPreferences sharedPreferencesForUserObject;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesForDate;//SharedPreferences to make user logged in the app after first time successful login


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);//Making our own SharedPreferences named login
        sharedPreferencesForDate = getSharedPreferences("date", MODE_PRIVATE);//Making another one to store log in time named date

        sharedPreferencesForUserObject = getSharedPreferences("user", MODE_PRIVATE);//Making sp to store class's object


        /* Making a KEY in the SharedPreferences named isLogged which stores a boolean value
         * After launching LogIn activity we check if the isLogged is true or false by getBoolean method
         * True means LoggedIn successfully and false means opposite
         */
        if (sharedPreferences.getBoolean("isLogged", false)) {
            Intent homeIntent = new Intent(LogIn.this, SignIn.class);//If true homeIntent is invoked
            startActivity(homeIntent);
            finish();//LogIn activity does not get included in the stack
        }

        textView = findViewById(R.id.signUp);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        phoneInput = findViewById(R.id.phoneInput);//User phone number input
        passInput = findViewById(R.id.passInput);//User password input
        signInButton = findViewById(R.id.signInButton);//SignIn button

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userTable = database.getReference("User");//Getting reference of our user table in FireBase Database

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Getting the inputs from the EditText and storing them in variables
                final String phone = phoneInput.getText().toString().trim();
                final String password = passInput.getText().toString().trim();

                //Checking if the inputs are blank
                if (phone.equals("") || password.equals("")) {
                    Toast.makeText(LogIn.this, "Enter Phone Number and Password", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog mDialog = new ProgressDialog(LogIn.this);
                    mDialog.setMessage("Please Wait");
                    mDialog.show();

                    userTable.addValueEventListener(new ValueEventListener() {
                        @SuppressWarnings("SpellCheckingInspection")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mDialog.dismiss();

                            //If the input data exist in the database
                            if (dataSnapshot.child(phoneInput.getText().toString()).exists()) {
                                //Getting the information about user stored in database in User type object
                                User user = dataSnapshot.child(phoneInput.getText().toString()).getValue(User.class);
                                if (Objects.requireNonNull(user).getPass().equals(passInput.getText().toString())) {

                                    //Then making the isLogged true as the user is in our database
                                    sharedPreferences.edit().putBoolean("isLogged", true).apply();

                                    //Successful log in message
                                    Toast.makeText(LogIn.this, "Sign In Successful", Toast.LENGTH_SHORT).show();

                                    //Intent to go home activity
                                    Intent homeIntent = new Intent(LogIn.this, SignIn.class);

                                    Common.currentUser = user;

                                    //Gson helps to store object into a shared preference
                                    Gson gson = new Gson();
                                    String json = gson.toJson(Common.currentUser);//Convert object into a string value
                                    sharedPreferencesForUserObject.edit().putString("user", json).apply();//Now storing this string into sp

                                    startActivity(homeIntent);
                                    //Getting current time and date
                                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                    String date = df.format(Calendar.getInstance().getTime());

                                    //Then storing the value in our database
                                    sharedPreferencesForDate.edit().putString("date", date).apply();
                                    finish();//Removing this activity from back stack
                                } else {
                                    Toast.makeText(LogIn.this, "Password Is Incorrect", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LogIn.this, "User Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
