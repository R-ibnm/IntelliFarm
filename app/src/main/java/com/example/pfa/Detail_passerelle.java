package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Detail_passerelle extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView humidityTextView, humidityTextView1;
    private TextView temperatureTextView, temperatureTextView1;
    private ImageView bttn_back;
    private TextView etatPlanteTextView, etatPlanteTextView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_passerelle);

        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView1 = findViewById(R.id.humidityTextView1);
        temperatureTextView1 = findViewById(R.id.temperatureTextView1);
        bttn_back=(ImageView)findViewById(R.id.bttn_back);
        etatPlanteTextView = findViewById(R.id.etat_plante);
        etatPlanteTextView1 = findViewById(R.id.etat_plante1);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("DHT11");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                humidityTextView.setText("");
                temperatureTextView.setText("");
                DataSnapshot lastDataSnapshot = null;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    lastDataSnapshot = childSnapshot;
                }

                if (lastDataSnapshot != null) {
                    Double humidity = lastDataSnapshot.child("humidity").getValue(Double.class);
                    Double temperature = lastDataSnapshot.child("temperature").getValue(Double.class);

                    Log.d("Firebase", "Humidity: " + humidity);
                    Log.d("Firebase", "Temperature: " + temperature);

                    String humidityText = ": " + humidity + "%";
                    String temperatureText = ": " + temperature + "°C";
                    humidityTextView.setText(humidityText);
                    temperatureTextView.setText(temperatureText);
                    humidityTextView1.setText(humidityText);
                    temperatureTextView1.setText(temperatureText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                Toast.makeText(Detail_passerelle.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });


        DatabaseReference plantesReference = FirebaseDatabase.getInstance().getReference().child("plantes");

        plantesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot planteSnapshot : dataSnapshot.getChildren()) {
                    String nomPlante = planteSnapshot.getKey();
                    if (nomPlante.equals("Pomme")) {
                        Integer etatPomme = planteSnapshot.child("etat").getValue(Integer.class);
                        if (etatPomme != null) {
                            if (etatPomme == 0) {
                                etatPlanteTextView.setText("Saine");
                            } else if (etatPomme == 1) {
                                etatPlanteTextView.setText("Malade");
                            } else {
                                etatPlanteTextView.setText("État inconnu");
                            }
                        }
                    } else if (nomPlante.equals("Cerise")) {
                        // Ajoutez ici le code pour la plante de pomme, en utilisant le TextView etatPommeTextView
                        Integer etatCerise = planteSnapshot.child("etat").getValue(Integer.class);
                        if (etatCerise != null) {
                            if (etatCerise == 0) {
                                etatPlanteTextView1.setText("Saine");
                            } else if (etatCerise == 1) {
                                etatPlanteTextView1.setText("Malade");
                            } else {
                                etatPlanteTextView1.setText("État inconnu");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading plantes data: " + databaseError.getMessage());
            }
        });
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Detail_passerelle.this, Sante.class);
                startActivity(intent);

            }
        });
    }
}