package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Meteo extends AppCompatActivity {



    private static final int REQUEST_LOCATION_PERMISSION = 1;
    EditText text_search;
    ImageView image_weather;
    ImageButton search_button;
    TextView btn_avenir, btntoday,text_city, text_date, text_temperature, text_vent, text_rassenti, text_humidity, text_precipitation;
    ImageView  tasks, expanded_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo);


        // Vérifier si l'autorisation de localisation est accordée
        if (ContextCompat.checkSelfPermission(Meteo.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si l'autorisation est accordée, obtenir la localisation actuelle
            Log.d("resultat", "afficherDonneesMeteoParDefaut: ah ");
            obtenirLocalisationActuelle();
        } else {
            Log.d("resultat", "afficherDonneesMeteoParDefaut: la ");
            // Si l'autorisation n'est pas accordée, demander à l'utilisateur de l'activer
            demanderAutorisationLocalisation();
        }

        text_search = findViewById(R.id.text_search);
        image_weather = findViewById(R.id.image_weather);

        btntoday = findViewById(R.id.btntoday);
        search_button = findViewById(R.id.search_button);
        text_city = findViewById(R.id.text_city);
        text_date = findViewById(R.id.text_date);
        text_temperature = findViewById(R.id.text_temperature);
        text_vent = findViewById(R.id.text_vent);
        text_rassenti = findViewById(R.id.text_rassenti);
        text_humidity = findViewById(R.id.text_humidity);
        text_precipitation = findViewById(R.id.text_precipitation);
        expanded_menu = findViewById(R.id.expanded_menu);
        tasks=findViewById(R.id.tasks);

        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Meteo.this, HomeOuvrier.class);
                startActivity(intent);
            }
        });
        expanded_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Meteo.this, MenuOuvrier.class);
                startActivity(intent);
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String city_find = text_search.getText().toString();

                if (city_find.length() == 0) {

                    // Si le texte de recherche est vide, affiche les données météo par défaut
                    afficherDonneesMeteoParDefaut();
                } else {

                    // Si le texte de recherche n'est pas vide, effectue la recherche de la ville
                    rechercherVille(city_find);
                }
            }
        });

        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    // Si le texte de recherche est vide, affiche les données météo par défaut
                    afficherDonneesMeteoParDefaut();
                }
            }
        });

    }

    private void afficherDonneesMeteoParDefaut() {
        // Vérifier si l'autorisation de localisation est accordée
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si l'autorisation est accordée, obtenir la localisation actuelle
            Log.d("resultat", "afficherDonneesMeteoParDefaut: ah ");
            obtenirLocalisationActuelle();
        } else {
            Log.d("resultat", "afficherDonneesMeteoParDefaut: la ");
            // Si l'autorisation n'est pas accordée, demander à l'utilisateur de l'activer
            afficherDonneesMeteoSansLocalisation();
        }

    }

    private void obtenirLocalisationActuelle() {
        Log.d("resultat", "obtenir: ah2 ");
        // Utiliser les fonctionnalités de localisation d'Android pour obtenir la localisation actuelle
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Vérifier si l'autorisation de localisation fine est accordée
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Obtenir la dernière localisation connue
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Utiliser la localisation pour rechercher la ville correspondante
                    rechercherVilleParLocalisation(latitude, longitude);
                    return;
                }
            }
        }
        // Si la localisation n'est pas disponible, afficher les données météo par défaut sans la localisation
        afficherDonneesMeteoSansLocalisation();
    }

    private void demanderAutorisationLocalisation() {
        // Demander à l'utilisateur d'activer la localisation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Autorisation de localisation");
        builder.setMessage("L'application a besoin de votre autorisation pour accéder à votre localisation. Voulez-vous l'activer ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("resultat", "afficherDonneesMeteoParDefaut: ah2 ");
                // Rediriger l'utilisateur vers les paramètres de localisation de l'appareil
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_LOCATION_PERMISSION);
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("resultat", "afficherDonneesMeteoParDefaut: la2 ");
                // Afficher les données météo par défaut sans la localisation
                afficherDonneesMeteoSansLocalisation();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("resultat", "onactivity: ah ");

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            rechercherVille("rabat");
        }
    }

    public void afficherDonneesMeteoSansLocalisation(){
        Log.d("resultat", "sansMeteo: ah ");
        final String city = "Temara";
        String url ="http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=5865aeb62ddcc8f80b977300a695f7b6&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //find city
                    text_city.setText(city);

                    //find date & time
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat std = new SimpleDateFormat("HH:mm a \nE, MMM dd yyyy");
                    String date = std.format(calendar.getTime());
                    text_date.setText(date);

                    //find icon
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    String icon = obj.getString("icon");
                    Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(image_weather);

                    //find temperature
                    JSONObject object = jsonObject.getJSONObject("main");
                    double temp = object.getDouble("temp");
                    text_temperature.setText("Temperature\n"+temp+"°C");

                    //find vent
                    JSONObject object2 = jsonObject.getJSONObject("wind");
                    double speed = object2.getDouble("speed");
                    double deg = object2.getDouble("deg");
                    text_vent.setText("Speed :"+speed+"mph");

                    //find precipitation
                    text_precipitation.setText("0\nIn last 24h");

                    //find humidity
                    double humidity = object.getDouble("humidity");
                    text_humidity.setText(humidity+"%");

                    //find rassenti
                    double feels_like = object.getDouble("feels_like");
                    text_rassenti.setText(feels_like+"ºC");


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Meteo.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Meteo.this);
        requestQueue.add(stringRequest);
    }

    private void rechercherVille(String ville) {
        String url ="http://api.openweathermap.org/data/2.5/weather?q="+ville+"&appid=5865aeb62ddcc8f80b977300a695f7b6&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //find city
                    text_city.setText(ville);

                    //find date & time
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat std = new SimpleDateFormat("HH:mm a \nE, MMM dd yyyy");
                    String date = std.format(calendar.getTime());
                    text_date.setText(date);

                    //find icon
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    String icon = obj.getString("icon");
                    Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(image_weather);

                    //find temperature
                    JSONObject object = jsonObject.getJSONObject("main");
                    double temp = object.getDouble("temp");
                    text_temperature.setText("Temperature\n"+temp+"°C");

                    //find vent
                    JSONObject object2 = jsonObject.getJSONObject("wind");
                    double speed = object2.getDouble("speed");
                    double deg = object2.getDouble("deg");
                    text_vent.setText(speed+"mph");

                    //find precipitation
                    text_precipitation.setText("0.1\nIn last 24h");

                    //find humidity
                    double humidity = object.getDouble("humidity");
                    text_humidity.setText(humidity+"%");

                    //find rassenti
                    double feels_like = object.getDouble("feels_like");
                    text_rassenti.setText(feels_like+"ºC");


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Meteo.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Meteo.this);
        requestQueue.add(stringRequest);
    }

    private void rechercherVilleParLocalisation(double latitude, double longitude) {
        System.out.println("mar7baaa");
        // Utiliser les coordonnées de latitude et de longitude pour rechercher la ville correspondante
        // Effectuer une requête à l'API météo en utilisant les coordonnées

        // Recevoir la réponse de l'API météo

        // Analyser les données de la réponse de l'API et extraire les informations nécessaires

        // Afficher les données météo de la ville correspondante dans votre interface utilisateur
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'autorisation de localisation est accordée, obtenir la localisation actuelle
                obtenirLocalisationActuelle();
            } else {
                // Si l'autorisation de localisation est refusée, afficher les données météo par défaut sans la localisation
                afficherDonneesMeteoSansLocalisation();
            }
        }
    }










}