package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListOfTasks extends AppCompatActivity {
    private ArrayList<Tasks> taskList;
    private ArrayAdapter<Tasks> taskAdapter;
    private ListView taskListView;
    private ImageView bttn_back;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_tasks);
        taskListView = findViewById(R.id.taskListView);
        bttn_back=(ImageView)findViewById(R.id.bttn_back);
        taskList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<Tasks>(this, R.layout.list_item_task, R.id.taskNameTextView, taskList) {
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_task, parent, false);
                }

                TextView taskNameTextView = convertView.findViewById(R.id.taskNameTextView);
                ImageView taskStatusImageView = convertView.findViewById(R.id.taskStatusImageView);

                final Tasks task = getItem(position);

                if (task != null) {
                    taskNameTextView.setText(task.getTaskTitle());
                    // Change the check icon based on the "done" field of the task
                    if (task.isDone()) {
                        taskStatusImageView.setImageResource(R.drawable.baseline_check_box_24);
                    } else {
                        taskStatusImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                    }
                    // ... Votre code existant pour la personnalisation de l'adaptateur ...
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String taskId = task.getTaskId();

                        }
                    });
                }

                return convertView;
            }

        };
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               finish();

            }
        });
        taskListView.setAdapter(taskAdapter);

        // Récupérer et afficher les tâches de l'employé
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("EmployeeTasks");
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Effacer la liste existante des tâches
                taskList.clear();


                // Parcourir les instantanés de tâches de tous les utilisateurs
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot taskSnapshot : userSnapshot.getChildren()) {
                        // Récupérer l'ID de la tâche
                        String taskId = taskSnapshot.getKey();

                        // Récupérer les données de chaque tâche
                        String taskDescription = taskSnapshot.child("taskDescription").getValue(String.class);
                        boolean taskDone = taskSnapshot.child("done").getValue(Boolean.class);

                        // Créer un nouvel objet Tasks avec les données récupérées
                        Tasks task = new Tasks(taskId, taskDescription, taskDone);

                        // Ajouter la tâche à la liste
                        taskList.add(task);
                    }
                }
                // Trier la liste en fonction du statut "done"
                Collections.sort(taskList, new Comparator<Tasks>() {
                    @Override
                    public int compare(Tasks task1, Tasks task2) {
                        // Trier par ordre croissant : les tâches non effectuées (done=false) d'abord
                        // Pour trier par ordre décroissant, inversez les conditions (task2.isDone() - task1.isDone())
                        return Boolean.compare(task1.isDone(), task2.isDone());
                    }
                });
                // Avertir l'adaptateur que les données ont changé
                taskAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs de récupération des données de la base de données
                Log.e("ListOfTasks", "Erreur lors de la récupération des tâches", databaseError.toException());
                Toast.makeText(ListOfTasks.this, "Erreur lors de la récupération des tâches", Toast.LENGTH_SHORT).show();
            }
        });
    }
}