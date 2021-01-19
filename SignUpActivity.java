package com.example.android.help_response;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText e5_name, e6_email, e7_password;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        e5_name = findViewById(R.id.editText5);
        e6_email = findViewById(R.id.editText6);
        e7_password = findViewById(R.id.editText7);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

    }

    public void signUpUser (View v) {

        dialog.setMessage(" Welcome, Please wait");
        dialog.show();

        String name = e5_name.getText().toString();
        String email = e6_email.getText().toString();
        String password = e7_password.getText().toString();

        if (name.equals("") && email.equals("") && password.equals("")){

            Toast.makeText(getApplicationContext(), "Field cannot be blank", Toast.LENGTH_SHORT).show();
        } else {

            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dialog.hide();
                                Toast.makeText(getApplicationContext(),"User registered successfully", Toast.LENGTH_SHORT).show();

                               databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                               Users user_object = new Users(e5_name.getText().toString(),e6_email.getText().toString(),e7_password.getText().toString());
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                databaseReference.child(firebaseUser.getUid()).setValue(user_object)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"User data saved",Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getApplicationContext(),"User data could not be saved",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                dialog.hide();
                                Toast.makeText(getApplicationContext(),"User could not be Registered", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
}
