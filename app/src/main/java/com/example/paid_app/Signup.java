package com.example.paid_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    EditText  email, password, fname, lname, country, landmark, pincode, city;
    Button Sign_up;
    TextView re_login;
    String Name, Email, Password, Fname, Lname, Country, Landmark, Pincode, City;
    FirebaseAuth mAuth;
    CollectionReference mRef;
    SharedPreferences sp;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.name6);
        email = findViewById(R.id.name2);
        country = findViewById(R.id.Email);
        landmark = findViewById(R.id.name3);
        city = findViewById(R.id.name4);
        pincode = findViewById(R.id.name5);
        password = findViewById(R.id.Password);
        Sign_up = findViewById(R.id.signup);
        re_login = findViewById(R.id.relogin);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance().collection("Users");

        re_login.setOnClickListener(v -> {
            startActivity(new Intent(Signup.this,Login.class));
        });

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndLogin();
            }
        });
    }
    private void checkDataAndLogin(){
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        Fname = fname.getText().toString().trim();
        Lname = lname.getText().toString().trim();
        Landmark = landmark.getText().toString().trim();
        City = city.getText().toString().trim();
        Pincode = pincode.getText().toString().trim();
        Country = country.getText().toString().trim();

        if(Fname.isEmpty() || Fname.equals("")){
            fname.setError("This is compulsory");
            fname.requestFocus();
            return;
        }
        if(Lname.isEmpty() || Lname.equals("")){
            lname.setError("This is compulsory");
            lname.requestFocus();
            return;
        }
        if(Email.isEmpty() || Email.equals("")){
            email.setError("This is compulsory");
            email.requestFocus();
            return;
        }
        if(Password.isEmpty() || Password.equals("")){
            password.setError("This is compulsory");
            password.requestFocus();
            return;
        }
        if(Password.length() < 6){
            password.setError("Too Short (8 to 12)");
            password.requestFocus();
            return;
        }

        Progress_bar();
        sign_up();
        sp = getSharedPreferences("Profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Username",Fname+" "+Lname);
        editor.putString("Email",Email);
        editor.apply();
    }
    private void sign_up(){
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Signup.this, task.getException().getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveData();
                }
            }
        });
    }
    private void saveData(){
        Map<String,String> map = new HashMap<>();
        map.put("First Name",Fname);
        map.put("Last Name",Lname);
        map.put("Email",Email);
        map.put("Landmark",Landmark);
        map.put("City",City);
        map.put("Pincode",Pincode);
        map.put("Country",Country);
        map.put("Password",Password);
        map.put("Id",mAuth.getUid());
        mRef.document(mAuth.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Signup.this, "Registration not done", Toast.LENGTH_SHORT).show();
                }
                else {
                    Dismiss();
                    Toast.makeText(Signup.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signup.this,Home.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Signup.this, "Registration not done", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Progress_bar(){
        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void Dismiss(){
        progressDialog.dismiss();
    }
}