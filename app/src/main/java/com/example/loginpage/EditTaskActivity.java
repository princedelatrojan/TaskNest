package com.example.loginpage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditTaskActivity extends AppCompatActivity {

    private EditText etTaskName, etDate, etDescription;
    private Button btnSave, btnDelete;
    private FirebaseFirestore db;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);

        db = FirebaseFirestore.getInstance();

        // Get taskId passed from adapter
        taskId = getIntent().getStringExtra("taskId");
        etTaskName = findViewById(R.id.etTaskName);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSaveChanges);
        btnDelete = findViewById(R.id.btnDeleteTask);

        loadTask();

        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> deleteTask());

        Button btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> finish());

    }

    private void loadTask() {
        if (taskId == null) {
            Toast.makeText(this, "Missing task ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        db.collection("tasks").document(taskId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etTaskName.setText(document.getString("taskName"));
                        etDate.setText(document.getString("dueDate"));
                        etDescription.setText(document.getString("description"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading task", Toast.LENGTH_SHORT).show());
    }

    private void saveChanges() {
        String name = etTaskName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        DocumentReference docRef = db.collection("tasks").document(taskId);
        docRef.update(
                "taskName", name,
                "dueDate", date,
                "description", desc
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteTask() {
        db.collection("tasks").document(taskId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
