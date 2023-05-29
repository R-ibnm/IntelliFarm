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

public class Sante extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView humidityTextView ,parcel_n;
    private TextView temperatureTextView;
    private ImageView expanded_menu,jardinmenu, meteomenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sante);
        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        expanded_menu=(ImageView)findViewById(R.id.expanded_menu);
        jardinmenu=(ImageView)findViewById(R.id.jardinmenu);
        meteomenu=(ImageView)findViewById(R.id.meteomenu);
        parcel_n= findViewById(R.id.parcel_n);

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                Toast.makeText(Sante.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });
        expanded_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Sante.this, MenuAdmin.class);
                startActivity(intent);

            }
        });
        jardinmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Sante.this, HomeAdmin.class);
                startActivity(intent);

            }
        });
        parcel_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Sante.this, Detail_passerelle.class);
                startActivity(intent);

            }
        });
        meteomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Sante.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}