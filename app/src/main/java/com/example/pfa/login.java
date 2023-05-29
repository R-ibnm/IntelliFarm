package com.example.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity implements View.OnClickListener {

    EditText username_auth;
    EditText password_auth;
    TextView signup_auth;
    Button signin;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username_auth=(EditText)findViewById(R.id.username_auth);
        password_auth=(EditText)findViewById(R.id.password_auth);
        signin=(Button) findViewById(R.id.bt_signin);



        signin.setOnClickListener(this);



        //initialisation de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if (currentUser!=null){
            String email = currentUser.getEmail();
            if (email != null) {
                if (email.equals("mouna@gmail.com")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, HomeOuvrier.class);
                    startActivity(intent);
                }
            }

        }
        else{

        }



    }
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }
    public void onClick(View view){
        if(view.getId()==R.id.bt_signin){
            String email = username_auth.getText().toString();
            String password = password_auth.getText().toString();
            signIn( email,  password);
        }
    }


}