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
import com.example.dailyshoppinglist.activites.HomeActivity;
import com.example.dailyshoppinglist.activites.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText rePassword;
    private Button signUp;
    private TextView logIn;
    private TextView signUpError ;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        email = findViewById(R.id.email_sign_up);
        password = findViewById(R.id.password_sign_up);
        rePassword = findViewById(R.id.re_password_sign_up);
        signUp = findViewById(R.id.btn_sign_up);
        logIn = findViewById(R.id.tv_log_in_link);
        signUpError = findViewById(R.id.tv_sign_up_failed);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mRePassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mRePassword)) {
                    rePassword.setError("Required Field");
                    return;
                }
                if (!mRePassword.matches(mPassword)) {
                    rePassword.setError("password doesn't match");
                    return;
                }

                progressDialog.setMessage("Processing");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Successful Registration", Toast.LENGTH_LONG).show();
                            signUpError.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();
                        } else {
                            signUpError.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
