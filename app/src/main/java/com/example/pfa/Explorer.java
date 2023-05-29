package com.example.pfa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Explorer extends AppCompatActivity implements View.OnClickListener{
    private TextView afaire;
    private ImageView Adminmenu;

    CardView rec1, rec2, rec3 ;

    RelativeLayout layout_rec1, layout_rec2, layout_rec3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        afaire= (TextView)findViewById(R.id.afaire);
        rec1= (CardView) findViewById(R.id.rec1);
        rec2=(CardView) findViewById(R.id.rec2);
        rec3=(CardView)findViewById(R.id.rec3);
        layout_rec1 =(RelativeLayout) findViewById(R.id.layout_rec1);
        layout_rec2 =(RelativeLayout) findViewById(R.id.layout_rec2);
        layout_rec3 =(RelativeLayout) findViewById(R.id.layout_rec3);






        rec1.setOnClickListener(this);
        rec2.setOnClickListener(this);
        rec3.setOnClickListener(this);




        afaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Explorer.this, HomeAdmin.class);
                startActivity(intent);
                finish();

            }
        });


    }
    public void onClick(View view) {
        layout_rec1.setVisibility(View.VISIBLE);
        layout_rec2.setVisibility(View.GONE);
        layout_rec3.setVisibility(View.GONE);
        if(view.getId() == R.id.rec1){
            layout_rec1.setVisibility(View.VISIBLE);
            layout_rec2.setVisibility(View.GONE);
            layout_rec3.setVisibility(View.GONE);
        }
        else  if(view.getId() == R.id.rec2){
            layout_rec2.setVisibility(View.VISIBLE);
            layout_rec1.setVisibility(View.GONE);
            layout_rec3.setVisibility(View.GONE);
        }
        else  if(view.getId() == R.id.rec3){
            layout_rec3.setVisibility(View.VISIBLE);
            layout_rec1.setVisibility(View.GONE);
            layout_rec2.setVisibility(View.GONE);
        }

    }
}