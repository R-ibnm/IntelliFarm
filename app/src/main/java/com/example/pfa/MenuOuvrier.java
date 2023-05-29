package com.example.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MenuOuvrier extends AppCompatActivity {
    private TextView logout;
    private TextView edit_worker,List_of_Tasks;

    private ImageButton back_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_ouvrier);
        logout=(TextView)findViewById(R.id.logout);
        edit_worker=(TextView)findViewById(R.id.edit_worker);
        List_of_Tasks=(TextView)findViewById(R.id.List_of_Tasks);
        back_btn=findViewById(R.id.back_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MenuOuvrier.this, login.class);
                startActivity(intent);
                finish();

            }
        });
        edit_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuOuvrier.this, profileOuvrier.class);
                startActivity(intent);

            }
        });
        List_of_Tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuOuvrier.this, HomeOuvrier.class);
                startActivity(intent);

            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

    }
}