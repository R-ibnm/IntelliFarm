package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class profileOuvrier extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText fullName, Email, number;
    ImageView profile;
    ImageButton bttn_back, editProfile;
    Button  changePassword, update, changePhoto;

    FirebaseUser currentUser;
    String userID;

    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileouvrier);

        fullName = (EditText) findViewById(R.id.fullName);
        Email = (EditText) findViewById(R.id.email);
        number=(EditText) findViewById(R.id.number);
        profile = (ImageView) findViewById(R.id.profile);
        editProfile = findViewById(R.id.editProfile);
        changePassword = (Button) findViewById(R.id.changePassword);
        update= (Button) findViewById(R.id.update);
        changePhoto=(Button) findViewById(R.id.changePhoto);
        bttn_back=findViewById(R.id.bttn_back);

        fullName.setEnabled(false);
        Email.setEnabled(false);
        number.setEnabled(false);

        // Obtenez l'ID de l'utilisateur connecté à partir de l'API d'authentification de Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentUser.getUid();

        // Écouter le clic sur le bouton de modification du profil
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activer l'édition des champs
                Email.setEnabled(true);
                number.setEnabled(true);
                Email.setTextColor(Color.BLACK);
                number.setTextColor(Color.BLACK);

                // Afficher un bouton de validation des modifications
                update.setVisibility(View.VISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupérer les nouvelles valeurs des champs
                String newEmail = Email.getText().toString();
                String newNumber = number.getText().toString();

                // Afficher un dialogue pour demander le mot de passe de l'utilisateur
                AlertDialog.Builder builder = new AlertDialog.Builder(profileOuvrier.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Veuillez entrer votre mot de passe pour confirmer la mise à jour de l'e-mail");

                // Créer dynamiquement le LinearLayout contenant l'EditText
                LinearLayout layout = new LinearLayout(profileOuvrier.this);
                layout.setGravity(Gravity.CENTER);

                // Créer dynamiquement l'EditText pour saisir le mot de passe
                final EditText passwordEditText = new EditText(profileOuvrier.this);
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordEditText.setHint("Mot de passe");


                // Définir les paramètres de mise en page pour le LinearLayout
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.edittext_width), // Remplacez R.dimen.edittext_width par la dimension souhaitée
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layout.setLayoutParams(layoutParams);

// Ajouter l'EditText au LinearLayout
                layout.addView(passwordEditText);

// Définir le LinearLayout comme vue dans l'AlertDialog
                builder.setView(layout);

                builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Récupérer le mot de passe entré par l'utilisateur
                        String password = passwordEditText.getText().toString();

                        // Valider les modifications
                        if (!TextUtils.isEmpty(password)) {
                            // Créer les crédentials avec le mot de passe entré
                            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);

                            // Réauthentifier l'utilisateur
                            currentUser.reauthenticate(credential)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // L'utilisateur a été réauthentifié avec succès
                                            // Mettre à jour l'e-mail et les autres détails de l'utilisateur
                                            updateEmailAndDetails(newEmail, newNumber);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Erreur lors de la réauthentification", Toast.LENGTH_SHORT).show();
                                            Log.e("Reauthentication", "Erreur lors de la réauthentification", e);
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Veuillez entrer votre mot de passe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher le dialogue de modification du mot de passe
                showChangePasswordDialog();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir la galerie de photos pour sélectionner une nouvelle image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });



        // Obtenez une référence à votre nœud "employee" dans Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(userID);

        // Ajoutez un écouteur pour récupérer les données du profil de l'utilisateur
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Récupérez les données du profil de l'utilisateur
                String nom = dataSnapshot.child("name").getValue(String.class);
                String prenom = dataSnapshot.child("prenom").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String tel = dataSnapshot.child("number").getValue(String.class);
                String photoUrl = dataSnapshot.child("picture").getValue(String.class);

                // Affichez les données du profil dans les TextViews correspondantes
                fullName.setText(prenom+ " "+nom);
                Email.setText(email);
                number.setText(tel);

                // 3. Afficher la photo de profil, si disponible
                if (photoUrl != null) {
                    // Récupérer une référence à l'image dans Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);

                    // Utiliser la référence pour télécharger l'URL de l'image et l'afficher dans l'ImageView
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Utiliser une bibliothèque d'image comme Glide ou Picasso pour charger et afficher l'image
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Gérer les erreurs de téléchargement de l'image
                        }
                    });

                }else{
                    photoUrl = "gs://apppfa-1dbb2.appspot.com/profile.png";
                    // Récupérer une référence à l'image dans Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);

                    // Utiliser la référence pour télécharger l'URL de l'image et l'afficher dans l'ImageView
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Utiliser une bibliothèque d'image comme Glide ou Picasso pour charger et afficher l'image
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Gérer les erreurs de téléchargement de l'image
                        }
                    });
                }
                Log.d("resultat", photoUrl);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérez les erreurs de récupération des données du profil de l'utilisateur
            }
        });

    }
    public void updateEmailAndDetails(String email, String num){

        // Valider les modifications
        if (!email.isEmpty() && !num.isEmpty()) {
            // Mettre à jour l'e-mail dans l'API d'authentification de Firebase
            currentUser.updateEmail(email)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Mettre à jour les détails de l'utilisateur dans la base de données
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(userID);
                            userRef.child("email").setValue(email);
                            userRef.child("number").setValue(num)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();

                                            // Désactiver l'édition des champs après la mise à jour
                                            Email.setEnabled(false);
                                            number.setEnabled(false);

                                            // Cacher le bouton de validation des modifications
                                            update.setVisibility(View.GONE);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour du profil", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour de l'e-mail", Toast.LENGTH_SHORT).show();
                            Log.e("test2", "Erreur lors de la mise à jour de l'e-mail", e);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Veuillez entrer des informations valides", Toast.LENGTH_SHORT).show();
        }
    }
    private void showChangePasswordDialog() {
        // Créer un dialogue d'alerte
        AlertDialog.Builder builder = new AlertDialog.Builder(profileOuvrier.this);
        builder.setTitle("Changer le mot de passe");


        // Créer un LinearLayout pour contenir les champs de texte
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);// Centrer le LinearLayout horizontalement
        layout.setGravity(Gravity.CENTER_HORIZONTAL);



        // Créer les champs de texte pour le mot de passe actuel et le nouveau mot de passe
        final EditText currentPasswordEditText = new EditText(this);
        currentPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        currentPasswordEditText.setHint("Mot de passe actuel");
        // Définir la largeur fixe de 12dp
        LinearLayout.LayoutParams currentPasswordParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.edittext_width), LinearLayout.LayoutParams.WRAP_CONTENT);
        // Définir la marge inférieure pour créer un espace
        currentPasswordParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.edittext_margin_bottom);
        currentPasswordEditText.setLayoutParams(currentPasswordParams);
        // Centrer l'EditText horizontalement
        currentPasswordEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(currentPasswordEditText);

        final EditText newPasswordEditText = new EditText(this);
        newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordEditText.setHint("Nouveau mot de passe");
        // Définir la largeur fixe de 12dp
        LinearLayout.LayoutParams newPasswordParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.edittext_width), LinearLayout.LayoutParams.WRAP_CONTENT);
        newPasswordEditText.setLayoutParams(newPasswordParams);
        // Centrer l'EditText horizontalement
        newPasswordEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(newPasswordEditText);


        // Ajouter le LinearLayout au dialogue
        builder.setView(layout);


        // Ajouter les boutons "Confirmer" et "Annuler" au dialogue
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Récupérer le mot de passe actuel et le nouveau mot de passe saisis par l'utilisateur
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                // Appeler une méthode pour modifier le mot de passe
                changePassword(currentPassword, newPassword);
            }
        });
        builder.setNegativeButton("Annuler", null);

        // Afficher le dialogue
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changePassword(String currentPassword, String newPassword) {
        // Récupérer l'utilisateur actuel
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Créer les crédentials avec le mot de passe actuel
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        // Réauthentifier l'utilisateur
        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // L'utilisateur a été réauthentifié avec succès
                        // Modifier le mot de passe de l'utilisateur
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Le mot de passe a été modifié avec succès
                                        Toast.makeText(profileOuvrier.this, "Le mot de passe a été modifié avec succès", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(profileOuvrier.this, login.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Une erreur s'est produite lors de la modification du mot de passe
                                        Toast.makeText(profileOuvrier.this, "Erreur lors de la modification du mot de passe", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Une erreur s'est produite lors de la réauthentification de l'utilisateur
                        Toast.makeText(profileOuvrier.this, "Erreur lors de la réauthentification de l'utilisateur", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Récupérer l'URI de l'image sélectionnée
            Uri imageUri = data.getData();

            // Enregistrer l'image sélectionnée dans Firebase Storage
            uploadProfilePhoto(imageUri);
        }
    }

    private void uploadProfilePhoto(Uri imageUri) {
        // Créer une référence à l'emplacement de stockage dans Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilePhotos").child(userID);

        // Télécharger l'image sélectionnée
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // L'image a été téléchargée avec succès
                        // Récupérer l'URL de téléchargement de l'image
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Mettre à jour l'URL de la photo de profil dans la base de données
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(userID);
                                userRef.child("picture").setValue(uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Photo de profil mise à jour avec succès", Toast.LENGTH_SHORT).show();

                                                // Afficher la nouvelle photo de profil
                                                Glide.with(getApplicationContext())
                                                        .load(uri)
                                                        .into(profile);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour de la photo de profil", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Une erreur s'est produite lors du téléchargement de l'image
                        Toast.makeText(getApplicationContext(), "Erreur lors du téléchargement de la photo de profil", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}