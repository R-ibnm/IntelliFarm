package com.example.pfa;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlanteDashboard extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView humidityTextView;
    private TextView temperatureTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plante_dashboard);
        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);

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

                    String humidityText = "Humidity: " + humidity + "%";
                    String temperatureText = "Temperature: " + temperature + "°C";
                    humidityTextView.setText(humidityText);
                    temperatureTextView.setText(temperatureText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                Toast.makeText(PlanteDashboard.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}