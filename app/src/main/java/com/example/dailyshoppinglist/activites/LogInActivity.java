package com.example.dailyshoppinglist.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyshoppinglist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button logIn;
    private TextView signUp;
    private TextView logInError;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.email_log_in);
        password = findViewById(R.id.password_log_in);
        logIn = findViewById(R.id.btn_log_in);
        signUp = findViewById(R.id.tv_sign_up_link);
        logInError= findViewById(R.id.tv_log_in_failed);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseAuth mAuth = firebaseAuth;
        checkLogIn(mAuth);


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Required Field");
                    return;
                }
                progressDialog.setMessage("Processing");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "LogIn Successful", Toast.LENGTH_LONG).show();
                            logInError.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();

                        } else {
                            logInError.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }
                    }
                });


            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));

            }
        });
    }

    private void checkLogIn(FirebaseAuth mAuth) {
        if(mAuth.getCurrentUser()!=null){
             startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
