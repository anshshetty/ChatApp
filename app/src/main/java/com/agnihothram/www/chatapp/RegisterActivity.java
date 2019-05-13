package com.agnihothram.www.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    public final static String CHAT_PREF = "ChatPref";
    public static final String DISPLAY_NAME = "UserName";

    //Ref to fields
    private AutoCompleteTextView myUserNameView;
    private EditText myEmail;
    private EditText myPassword;
    private EditText myPasswordConfirm;

    //firebase ref
    private FirebaseAuth myAuth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog=new ProgressDialog(RegisterActivity.this);


        myEmail = findViewById(R.id.register_email);
        myUserNameView = findViewById(R.id.register_user_name);
        myPassword = findViewById(R.id.register_password);
        myPasswordConfirm = findViewById(R.id.register_confirm_password);

        //firebase instance
        myAuth = FirebaseAuth.getInstance();
    }

    //method called by tapping
    public void signUp(View v) {
        progressDialog.show();
        progressDialog.setMessage("Registering..");
        registerUser();
    }

    //actual reg happens here
    private void registerUser() {
        myEmail.setError(null);
        myPassword.setError(null);

        //grab values
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //pass validations
        if (!TextUtils.isEmpty(password) && !checkPassword(password)) {

            myPassword.setError(getString(R.string.invaild_password));
            focusView = myPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(email) && !checkEmail(email)) {
            myEmail.setError(getString(R.string.invaild_email));
            focusView = myEmail;
            cancel = true;
        }

        //requesting focus(kill switch)
        if (cancel) {
            focusView.requestFocus();
        } else {
            progressDialog.show();
            progressDialog.setMessage("Registering..");
            createUser();
        }
    }

    //validation or email
    private boolean checkEmail(String email) {
        return email.contains("@");
    }

    //validation for passwords
    private boolean checkPassword(String password) {
        String confPassword = myPasswordConfirm.getText().toString();

        return confPassword.equals(password) && password.length() > 8;
    }


    //signup a user firebase
    private void createUser() {
        //Grab the values
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();

        //calling method from firebase
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //delete
                Log.i("FINDCODE", "User Connection was" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Ooops! Registration Failed!", Toast.LENGTH_SHORT).show();
                    showErrorbox("Ooops! Registration Failed!");
                } else {
                    progressDialog.dismiss();
                    saveUserName();
                    Toast.makeText(getApplicationContext(), "Registration Succesful!", Toast.LENGTH_SHORT).show();
                    //login screen intent
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }


    //shared pref for usernames
    private void saveUserName() {
        String userName = myUserNameView.getText().toString();
        SharedPreferences pref = getSharedPreferences(CHAT_PREF, 0);
        pref.edit().putString(DISPLAY_NAME, userName).apply();
    }

    //create error


    private void showErrorbox(String message) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Hey")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
