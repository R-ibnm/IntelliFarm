package com.example.pfa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListeEmployees extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView employeeListView;
    private TextView empNameTextView;
    private ArrayAdapter<String> employeeAdapter;
    private List<String> employeeList;
    private ImageView Adminmenu, jardinmenu, santemenu, meteomenu;
    private ImageButton bttn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_employees);

        employeeList = new ArrayList<>();
        employeeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeeList);
        employeeListView = findViewById(R.id.employeeListView);
        Adminmenu = findViewById(R.id.Adminmenu);
        jardinmenu = findViewById(R.id.jardinmenu);
        santemenu = findViewById(R.id.santemenu);
        meteomenu = findViewById(R.id.meteomenu);
        bttn_back=findViewById(R.id.bttn_back);

        employeeListView.setAdapter(employeeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Employee");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employeeList.clear();

                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    String name = employeeSnapshot.child("name").getValue(String.class);
                    String prenom = employeeSnapshot.child("prenom").getValue(String.class);
                    String number = employeeSnapshot.child("number").getValue(String.class);

                    String employeeDetails = name + " - " + prenom ;
                    employeeList.add(employeeDetails);
                }

                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                Toast.makeText(ListeEmployees.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });

        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedEmployeeDetails = employeeList.get(position);
                String clickedEmployeeName = clickedEmployeeDetails.substring(0, clickedEmployeeDetails.indexOf(" - "));
                String clickedEmployeePrenom = clickedEmployeeDetails.substring(clickedEmployeeDetails.indexOf(" - ") + 3);
                String clickedEmployeeNumber = clickedEmployeeDetails.substring(clickedEmployeeDetails.lastIndexOf("-") + 1); // Récupérer le numéro

                // Adding 3 to skip " - "
                String clickedEmployeePosition = clickedEmployeeDetails.substring(clickedEmployeeDetails.indexOf(" - ") + 3);

                Log.d("ListeEmployees", "Clicked employee: " + clickedEmployeeName);

                // Start DetailEmployeeActivity with the employee details
                Intent intent = new Intent(ListeEmployees.this, DetailEmployee.class);
                intent.putExtra("name", clickedEmployeeName);
                intent.putExtra("prenom", clickedEmployeePrenom);
                intent.putExtra("number", "0658725354");

                startActivity(intent);
            }
        });
        Adminmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
        jardinmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                Intent intent = new Intent(ListeEmployees.this, HomeAdmin.class);
                startActivity(intent);

            }
        });
        santemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                Intent intent = new Intent(ListeEmployees.this, Sante.class);
                startActivity(intent);

            }
        });
        meteomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                Intent intent = new Intent(ListeEmployees.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}