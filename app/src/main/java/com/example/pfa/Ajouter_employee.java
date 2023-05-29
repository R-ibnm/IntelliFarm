package com.example.pfa;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pfa.DAOEmployee;
import com.example.pfa.ListeEmployees;
import com.example.pfa.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ajouter_employee extends AppCompatActivity {

    private EditText edit_name;
    private EditText edit_prenom;
    private EditText edit_email;
    private EditText edit_password;
    private EditText edit_number;

    private Button btn_submit;
    private ImageButton logout;
    private ImageButton bttn_back;


    private DAOEmployee dao;

    private FirebaseAuth mAuth;
    private DatabaseReference employeeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_employee);

        edit_name = findViewById(R.id.edit_name);
        edit_prenom = findViewById(R.id.edit_prenom);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_number = findViewById(R.id.edit_number);
        bttn_back= findViewById(R.id.bttn_back);

        btn_submit = findViewById(R.id.btn_submit);

        mAuth = FirebaseAuth.getInstance();

        dao = new DAOEmployee();




        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edit_email.getText().toString();
                String password = edit_password.getText().toString();


                createAccount(email, password);

            }
        });



        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Récupérer l'utilisateur actuellement connecté
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                String userId = user.getUid();

                                String name = edit_name.getText().toString();
                                String prenom = edit_prenom.getText().toString();
                                String number = edit_number.getText().toString();
                                String picture = "gs://apppfa-1dbb2.appspot.com/profile.png";
                                String email = edit_email.getText().toString();

                                // Créer le champ "id" dans la base de données en temps réel
                                DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference("Employee");
                                DatabaseReference idRef = employeeRef.child(userId);
                                idRef.child("name").setValue(name);
                                idRef.child("prenom").setValue(prenom);
                                idRef.child("email").setValue(email);
                                idRef.child("number").setValue(number);
                                idRef.child("picture").setValue(picture);


                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(Ajouter_employee.this, "createUserWithEmail:success",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Ajouter_employee.this, "createUserWithEmail:failure",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }
}