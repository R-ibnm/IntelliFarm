package com.example.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeAdmin extends AppCompatActivity {
    private ImageButton logout;
    private ImageView Adminmenu;
    private TextView mesplantes,explorer;
    private ImageView santemenu, meteomenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        Adminmenu = (ImageView)findViewById(R.id.Adminmenu);
        explorer=(TextView)findViewById(R.id.explorer);
        santemenu=(ImageView)findViewById(R.id.santemenu);
        meteomenu = (ImageView)findViewById(R.id.meteomenu);


        meteomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeAdmin.this, MainActivity.class);
                startActivity(intent);

            }
        });
        Adminmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeAdmin.this, MenuAdmin.class);
                startActivity(intent);

            }
        });
        explorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeAdmin.this, Explorer.class);
                startActivity(intent);

            }
        });
        santemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeAdmin.this, Sante.class);
                startActivity(intent);

            }
        });
    }
}