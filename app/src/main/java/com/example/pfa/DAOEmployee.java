package com.example.pfa;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.util.HashMap;

public class DAOEmployee {
    private DatabaseReference databaseReference;
    public DAOEmployee()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference (Employee.class.getSimpleName());
    }
    public Task<Void> add (Employee emp)
    {
        return databaseReference.push().setValue(emp);
    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child (key).updateChildren (hashMap);
    }

    public Task<Void> removeEmployee(Employee employee) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Employee");
        DatabaseReference employeeRef = databaseRef.child(employee.getName());


        return employeeRef.removeValue();
    }
}
