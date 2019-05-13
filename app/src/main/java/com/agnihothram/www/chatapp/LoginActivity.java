package com.agnihothram.www.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //ref to firebase
    private FirebaseAuth myAuth;

    //UI Refs
    private EditText myEmail;
    private EditText myPassword;
    private FirebaseUser firebaseUser;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog=new ProgressDialog(LoginActivity.this);

        //grab data
        myEmail = findViewById(R.id.login_email);
        myPassword = findViewById(R.id.login_password);

        //firebase instance
        myAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = myAuth.getCurrentUser();
        updateUI(firebaseUser);

    }

    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
            finish();
            startActivity(intent);
        }
    }

    //signin button tapped
    public void signInUser(View v) {


        loginUserWithFirebase();

    }

    //login with firebase
    private void loginUserWithFirebase() {
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();

        //implement a check in like register activity
        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "Please enter you email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        progressDialog.setMessage("Signing you in..");
        myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("FINDCODEL", "was user logged in" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    showErrorbox("There was a problem logging in!");
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "There was a problem logging in!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    public void registerNewUser(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    private void showErrorbox(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Hey")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
