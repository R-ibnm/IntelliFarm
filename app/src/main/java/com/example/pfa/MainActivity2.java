package com.example.pfa;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity2 extends Activity {
    private static final String API_URL = "https://flask-server-5obr.onrender.com/predict";

    private Button sendButton;
    private TextView resultImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sendButton = findViewById(R.id.button);
        resultImageView = findViewById(R.id.textView);
// Initialise Firebase
        FirebaseApp.initializeApp(this);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer l'image depuis les ressources Drawable
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tomatetest);

                // Convertir l'image en tableau d'octets
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Envoie de l'image à l'API dans un thread séparé
                new SendImageTask().execute(imageBytes);
            }
        });
    }

    private class SendImageTask extends AsyncTask<byte[], Void, String> {
        @Override
        protected String doInBackground(byte[]... params) {
            try {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                // Créer la requête multipart
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "image.png",
                                RequestBody.create(MediaType.parse("image/png"), params[0]))
                        .build();

                // Créer la requête POST avec l'URL de l'API et le corps multipart
                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(requestBody)
                        .build();
                // Envoyer la requête à l'API
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    // Lire la réponse de l'API
                    return response.body().string();
                } else {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Afficher la réponse dans le layout
                Toast.makeText(MainActivity2.this, response, Toast.LENGTH_SHORT).show();

                // Créer un tableau de noms de plantes
                String[] plantNames = {
                        "Tomato___Late_blight", "Tomato___healthy", "Grape___healthy", "Orange___Haunglongbing(Citrus_greening)",
                        "Soybean___healthy", "Squash___Powdery_mildew", "Potato___healthy", "Corn(maize)___Northern_Leaf_Blight",
                        "Tomato___Early_blight", "Tomato___Septoria_leaf_spot", "Corn(maize)___Cercospora_leaf_spot Gray_leaf_spot",
                        "Strawberry___Leaf_scorch", "Peach___healthy", "Apple___Apple_scab", "Tomato___Tomato_Yellow_Leaf_Curl_Virus",
                        "Tomato___Bacterial_spot", "Apple___Black_rot", "Blueberry___healthy", "Cherry(including_sour)___Powdery_mildew",
                        "Peach___Bacterial_spot", "Apple___Cedar_apple_rust", "Tomato___Target_Spot", "Pepper,bell___healthy", "Grape___Leaf_blight(Isariopsis_Leaf_Spot)",
                        "Potato___Late_blight", "Tomato___Tomato_mosaic_virus", "Strawberry___healthy", "Apple___healthy", "Grape___Black_rot",
                        "Potato___Early_blight", "Cherry(including_sour)___healthy", "Corn(maize)___Common_rust", "Grape___Esca(Black_Measles)",
                        "Raspberry___healthy", "Tomato___Leaf_Mold", "Tomato___Spider_mites Two-spotted_spider_mite", "Pepper,bell___Bacterial_spot",
                        "Corn(maize)___healthy" };

                // Stocker le résultat dans Firebase Realtime Database
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                // Parcourir la liste de noms de plantes et vérifier l'état correspondant
                for (int i = 0; i < plantNames.length; i++) {
                    if (response.equals(plantNames[i])) {
                        String[] splitResult = plantNames[i].split("___");
                        String plantName = splitResult[0]; // Nom de la plante
                        String state = splitResult[1]; // État de la plante

                        // Stocker le nom de la plante et l'état dans Firebase Realtime Database
                        DatabaseReference plantRef = databaseRef.child("results").child(plantName);
                        if(state.equals("healthy")){
                            plantRef.child("etat").setValue("0");
                        }else{
                            plantRef.child("etat").setValue("1");
                        }

                    }
                }


            } else {
                Toast.makeText(MainActivity2.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
            }
        }
    }
}