package com.angik.android.mrpharmacy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.angik.android.mrpharmacy.Classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("SpellCheckingInspection")
public class SignUp extends AppCompatActivity {
    private EditText name;
    private EditText phone;
    private EditText pass1;
    private EditText pass2;
    private EditText add;
    private Button signup;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference userTable = database.getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        add = findViewById(R.id.add);
        signup = findViewById(R.id.signUpButton);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || pass1.getText().toString().isEmpty() || add.getText().toString().isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill all the Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass1.getText().toString().equals(pass2.getText().toString())) {
                    Toast.makeText(SignUp.this, "Password did not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please Wait");
                mDialog.show();

                //new
                final User user = new User(name.getText().toString(), phone.getText().toString(), pass1.getText().toString(), add.getText().toString());

                userTable.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getPhone()).exists()) {
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Phone Number Already Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            userTable.child(phone.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}