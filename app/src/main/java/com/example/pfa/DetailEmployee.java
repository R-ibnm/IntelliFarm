package com.example.pfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailEmployee extends AppCompatActivity implements View.OnClickListener {
    private TextView nameTextView;
    private TextView prenomTextView;
    private TextView emailTextView;
    private TextView numberTextView;
    private ImageView deleteButton, call, msg;
    private ListView taskListView;
    private ArrayList<Tasks> tasksList;
    private ArrayAdapter<Tasks> taskAdapter;

    private DatabaseReference databaseReference;
    private String employeeId;
    private ImageButton bttn_back;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_employee);

        nameTextView = findViewById(R.id.nameTextView);
        prenomTextView = findViewById(R.id.prenomTextView);

        deleteButton = findViewById(R.id.deleteButton);
        taskListView = findViewById(R.id.taskListView);
        bttn_back=findViewById(R.id.bttn_back);
        call=(ImageView) findViewById(R.id.call);
        msg=(ImageView) findViewById(R.id.msg);

        call.setOnClickListener(this);
        msg.setOnClickListener(this);

        // Initialize the task list and adapter
        tasksList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, R.layout.list_item_task, R.id.taskNameTextView, tasksList);
        taskListView.setAdapter(taskAdapter);

        // Retrieve employee details from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String prenom = intent.getStringExtra("prenom");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        number = intent.getStringExtra("number");

        String picture = intent.getStringExtra("picture");

        // Set employee details in TextViews
        nameTextView.setText(name);
        prenomTextView.setText(prenom);


        // Retrieve employee ID based on the displayed details
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Employee");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    String currentName = employeeSnapshot.child("name").getValue(String.class);
                    String currentPrenom = employeeSnapshot.child("prenom").getValue(String.class);

                    if (name.equals(currentName) && prenom.equals(currentPrenom)) {
                        employeeId = employeeSnapshot.getKey();
                        break;
                    }
                }

                // Retrieve and display tasks for the employee
                if (employeeId != null) {
                    DatabaseReference employeeTasksRef = FirebaseDatabase.getInstance().getReference().child("EmployeeTasks").child(employeeId);
                    employeeTasksRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tasksList.clear();
                            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                                Tasks tasks = taskSnapshot.getValue(Tasks.class);
                                tasksList.add(tasks);
                            }
                            taskAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                            Toast.makeText(DetailEmployee.this, "Error reading data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                Toast.makeText(DetailEmployee.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });


        // Set click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (employeeId != null) {
                    // Delete the employee record from the database
                    DatabaseReference employeeRef = databaseReference.child(employeeId);
                    employeeRef.removeValue();
                    Toast.makeText(DetailEmployee.this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity after deleting the employee
                }
            }
        });
        // Set click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailToDelete = "test"; // Specify the email of the user to delete

            }
        });

        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        ImageView assignTaskButton = (ImageView) findViewById(R.id.assignTaskButton);
        assignTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailEmployee.this, AssignTaskActivity.class);
                intent.putExtra("employeeId", employeeId);
                startActivity(intent);
            }
        });
    }
    public void onClick(View view) {
        if(view.getId()==R.id.call)
        {
            System.out.println(number);
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));//change the number
            startActivity(callIntent);
        }else if(view.getId()==R.id.msg){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + number));
            startActivity(intent);
        }

    }

    private class TaskAdapter extends ArrayAdapter<Tasks> {
        private LayoutInflater inflater;
        private int resource;

        public TaskAdapter(Context context, int resource, List<Tasks> tasks) {
            super(context, resource, tasks);
            inflater = LayoutInflater.from(context);
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(resource, parent, false);
            }

            Tasks tasks = getItem(position);
            TextView taskNameTextView = convertView.findViewById(R.id.taskNameTextView);
            ImageView taskStatusImageView = convertView.findViewById(R.id.taskStatusImageView);

            taskNameTextView.setText(tasks.getTaskTitle());

            if (tasks.isDone()) {
                taskStatusImageView.setImageResource(R.drawable.baseline_check_box_24);
            } else {
                taskStatusImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
            }

            // Set click listener for task status image
            taskStatusImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference()
                            .child("EmployeeTasks").child(employeeId).child(tasks.getTaskId());

                    // Invert the task status
                    boolean newStatus = !tasks.isDone();

                    // Update the task status in the database
                    taskRef.child("done").setValue(newStatus)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update the task object in the adapter
                                    tasks.setDone(newStatus);

                                    // Refresh the adapter to reflect the changes
                                    notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the failure to update the task status
                                    Toast.makeText(getContext(), "Failed to update task status", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            return convertView;
        }
    }
}