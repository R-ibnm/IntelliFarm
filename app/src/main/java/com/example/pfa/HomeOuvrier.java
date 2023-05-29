package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeOuvrier extends AppCompatActivity {
    private ImageButton logout;
    private DatabaseReference databaseReference;
    private String employeeId;
    private ListView taskListView;
    private ImageView expanded_menu,tasks,meteomenu;
    private ArrayList<Tasks> taskList;
    private ArrayAdapter<Tasks> taskAdapter;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_ouvrier);
        taskListView = findViewById(R.id.taskListView);
        expanded_menu = findViewById(R.id.expanded_menu);
        tasks = findViewById(R.id.tasks);
        meteomenu = findViewById(R.id.meteomenu);

        taskList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<Tasks>(this, R.layout.list_item_task, R.id.taskNameTextView, taskList){
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_task, parent, false);
                }

                TextView taskNameTextView = convertView.findViewById(R.id.taskNameTextView);
                ImageView taskStatusImageView = convertView.findViewById(R.id.taskStatusImageView);

                final Tasks task = getItem(position);

                if (task != null) {
                    taskNameTextView.setText(task.getTaskDescription());

                    // Change the check icon based on the "done" field of the task
                    if (task.isDone()) {
                        taskStatusImageView.setImageResource(R.drawable.baseline_check_box_24);
                    } else {
                        taskStatusImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                    }

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String taskId = task.getTaskId();
                            boolean newStatus = !task.isDone();
                            updateTaskStatusInDatabase(taskId, newStatus);
                        }
                    });
                }

                return convertView;
            }
        };
        taskListView.setAdapter(taskAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Employee");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
// Obtenez l'ID de l'utilisateur connecté à partir de l'API d'authentification de Firebase
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    employeeId = currentUser.getUid();
                }

                // Retrieve and display tasks for the employee
                if (employeeId != null) {
                    DatabaseReference employeeTasksRef = FirebaseDatabase.getInstance().getReference().child("EmployeeTasks").child(employeeId);
                    employeeTasksRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            taskList.clear();
                            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                                Tasks tasks = taskSnapshot.getValue(Tasks.class);
                                taskList.add(tasks);

                                // Ajoutez un écouteur de clic pour chaque élément de la liste
                                tasks.setTaskId(taskSnapshot.getKey()); // Ajoutez cette ligne pour récupérer l'ID de la tâche
                            }
                            taskAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                            Toast.makeText(HomeOuvrier.this, "Error reading data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                Toast.makeText(HomeOuvrier.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });


        expanded_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeOuvrier.this, MenuOuvrier.class);
                startActivity(intent);

            }
        });

        meteomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeOuvrier.this, Meteo.class);
                startActivity(intent);

            }
        });



    }
    private void updateTaskStatusInDatabase(String taskId, boolean done) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("EmployeeTasks").child(employeeId).child(taskId);
        tasksRef.child("done").setValue(done)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Task_Update", "Task updated successfully in Realtime Database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Task_Update", "Error updating task in Realtime Database", e);
                    }
                });
    }


}