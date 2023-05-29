package com.example.pfa;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AssignTaskActivity extends AppCompatActivity {
    private EditText taskTitleEditText;
    private EditText taskDescriptionEditText;
    private Button assignTaskButton;
    private ImageButton bttn_back;
    private DatabaseReference employeeTasksReference;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);

        // Retrieve employee ID from intent
        Intent intent = getIntent();
        employeeId = intent.getStringExtra("employeeId");

        // Initialize views
        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        assignTaskButton = findViewById(R.id.assignTaskButton);
        bttn_back = findViewById(R.id.bttn_back);

        // Initialize Firebase database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        employeeTasksReference = databaseReference.child("EmployeeTasks");

        assignTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve task details from EditText fields
                String taskTitle = taskTitleEditText.getText().toString().trim();
                String taskDescription = taskDescriptionEditText.getText().toString().trim();

                // Validate task details
                if (TextUtils.isEmpty(taskTitle)) {
                    Toast.makeText(AssignTaskActivity.this, "Please enter task title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(taskDescription)) {
                    Toast.makeText(AssignTaskActivity.this, "Please enter task description", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Task object
                Tasks tasks = new Tasks();
                tasks.setTaskTitle(taskTitle);
                tasks.setTaskDescription(taskDescription);

                // Generate a unique task ID
                String taskId = employeeTasksReference.push().getKey();

                if (taskId != null) {
                    // Save the task to the employee's tasks in the database
                    employeeTasksReference.child(employeeId).child(taskId).setValue(tasks)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AssignTaskActivity.this, "Task assigned successfully", Toast.LENGTH_SHORT).show();
                                    finish(); // Close the activity after assigning the task
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AssignTaskActivity.this, "Failed to assign task", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(AssignTaskActivity.this, "Failed to generate task ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }
}