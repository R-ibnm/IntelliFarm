package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FruitDetails extends AppCompatActivity {

    private TextView humidityTextView, temperatureTextView, etatTextView;
    private ImageView bttn_back, myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);

        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        etatTextView = findViewById(R.id.etat_plante);
        bttn_back=(ImageView)findViewById(R.id.bttn_back);
        myImage=(ImageView)findViewById(R.id.myImage);

        Intent intent = getIntent();
        if (intent != null) {
            Double humidity = intent.getDoubleExtra("humidity", 0.0);
            Double temperature = intent.getDoubleExtra("temp", 0.0);
            String etat = intent.getStringExtra("etat");

            String humidityText = ": " + humidity + "%";
            String temperatureText = ": " + temperature + "°C";

            humidityTextView.setText(humidityText);
            temperatureTextView.setText(temperatureText);
            etatTextView.setText(etat);
        }

        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos/tefa7.jpg");

        // Utiliser la référence pour télécharger l'URL de l'image et l'afficher dans l'ImageView
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Utiliser une bibliothèque d'image comme Glide ou Picasso pour charger et afficher l'image
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(myImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Gérer les erreurs de téléchargement de l'image
            }
        });
    }
}