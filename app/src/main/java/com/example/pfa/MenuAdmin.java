package com.example.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MenuAdmin extends AppCompatActivity {
    private TextView logout;
    private TextView add_worker,sante, List_of_Tasks;
    private ImageButton back_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        logout=(TextView)findViewById(R.id.logout);
        add_worker=(TextView)findViewById(R.id.add_worker);
        sante=(TextView)findViewById(R.id.sante);
        back_btn=findViewById(R.id.back_btn);
        List_of_Tasks= findViewById(R.id.List_of_Tasks);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MenuAdmin.this, login.class);
                startActivity(intent);
                finish();

            }
        });

        add_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuAdmin.this, Ajouter_employee.class);
                startActivity(intent);

            }
        });
        List_of_Tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuAdmin.this, ListOfTasks.class);
                startActivity(intent);

            }
        });
        sante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuAdmin.this, Sante.class);
                startActivity(intent);

            }
        });

        TextView listEmployees =(TextView) findViewById(R.id.listEmployees);
        listEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdmin.this, ListeEmployees.class);
                startActivity(intent);
            }

        });
    }
}