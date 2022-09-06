package com.mo.planner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, usermail, userpassword;
    Button createAccountButton;

    FirebaseAuth mAuth;
    DatabaseReference path;

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        username = findViewById(R.id.userName);
        usermail = findViewById(R.id.userMail);
        userpassword = findViewById(R.id.userPassword);
        createAccountButton =findViewById(R.id.createAccountButton);

        pDialog = new ProgressDialog(RegisterActivity.this);
        pDialog.setMessage("Yükleniyor...");
        mAuth = FirebaseAuth.getInstance();
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                String name = username.getText().toString();
                String email = usermail.getText().toString();
                String password = userpassword.getText().toString();
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    pDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Alanlar boş geçilemez!", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Şifre en az 6 karakter olmalı.", Toast.LENGTH_SHORT).show();
                } else {
                    createAccount(name, email, password);
                }
            }
        });

    }
    public void createAccount(String name,String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();
                    path= FirebaseDatabase.getInstance().getReference().child("User").child(userId);
                    HashMap<String,Object> hashMap=new HashMap<>();

                    hashMap.put("id",userId);
                    hashMap.put("name",name);
                    hashMap.put("email",email);

                    path.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    finish();
                }else {
                    pDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"Kayıt işlemi başarısız",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    }

