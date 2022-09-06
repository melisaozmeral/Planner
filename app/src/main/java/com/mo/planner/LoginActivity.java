package com.mo.planner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    EditText usermail, userpassword;
    Button  signinButton,newaccountButton;
    ProgressDialog pDialog;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        usermail=findViewById(R.id.userMail);
        userpassword=findViewById(R.id.userPassword);
        signinButton=findViewById(R.id.siginButton);
        newaccountButton =findViewById(R.id.newaccountButton);
        mAuth = FirebaseAuth.getInstance();
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Yükleniyor...");
                pDialog.show();

                String email = usermail.getText().toString();
                String password = userpassword.getText().toString();
                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password))
                {
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Alanlar boş geçilemez!",Toast.LENGTH_LONG).show();
                }
                else{
                    signIn(email,password);
                }
            }
        });


        newaccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(signup);
                overridePendingTransition(0,0);
            }
        });

    }
       public void signIn(String email,String password){
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference pathSign= FirebaseDatabase.getInstance().getReference().child("User").
                                    child(mAuth.getCurrentUser().getUid());
                            pathSign.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pDialog.dismiss();
                                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0,0);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pDialog.dismiss();
                                }
                            });
                        }
                        else{
                            pDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Giriş işlemi başarısız",Toast.LENGTH_LONG).show();
                        }
                    }
            });

       }

    protected void onStart(){
        super.onStart();
        if(mAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            overridePendingTransition(0,0);
        }
    }



}