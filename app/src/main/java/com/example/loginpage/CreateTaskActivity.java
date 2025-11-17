package com.example.loginpage; // Or com.example.tasknest

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText inputTaskName, inputTaskDesc, inputTaskDate, inputTaskTime;
    private RadioGroup radioGroupPriority;
    private Button btnSaveTask;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // --- Initialize Firebase ---
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- Find Views ---
        inputTaskName = findViewById(R.id.input_task_name);
        inputTaskDesc = findViewById(R.id.input_task_desc);
        inputTaskDate = findViewById(R.id.input_task_date);
        inputTaskTime = findViewById(R.id.input_task_time);
        radioGroupPriority = findViewById(R.id.radioGroup_priority);
        btnSaveTask = findViewById(R.id.button_save_task);

        // --- Date Picker Logic ---
        inputTaskDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateTaskActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format: yyyy-MM-dd (Good for sorting)
                        // Or MMM dd, yyyy (Good for display)
                        // Let's use a simple display format for now:
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        inputTaskDate.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // --- Time Picker Logic ---
        inputTaskTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    CreateTaskActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        inputTaskTime.setText(selectedTime);
                    },
                    hour, minute, true);
            timePickerDialog.show();
        });

        // --- Save Button Logic ---
        btnSaveTask.setOnClickListener(v -> saveTaskToFirestore());
    }

    private void saveTaskToFirestore() {
        String name = inputTaskName.getText().toString().trim();
        String desc = inputTaskDesc.getText().toString().trim();
        String date = inputTaskDate.getText().toString().trim();
        String time = inputTaskTime.getText().toString().trim();

        // Get Priority
        String priority = "Low"; // Default
        int selectedId = radioGroupPriority.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_high) priority = "High";
        else if (selectedId == R.id.radio_medium) priority = "Medium";

        // Validation
        if (TextUtils.isEmpty(name)) {
            inputTaskName.setError("Task name is required");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Data Object
        // We use a Map here, but you could also use your Task class if you update the constructor
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("taskName", name);
        taskMap.put("description", desc);
        taskMap.put("dueDate", date + " " + time); // Combine date and time
        taskMap.put("priority", priority);
        taskMap.put("status", "Pending"); // Default status
        taskMap.put("userId", mAuth.getCurrentUser().getUid()); // LINK TO USER!
        taskMap.put("timestamp", System.currentTimeMillis()); // Good for sorting

        // Save to Firestore
        db.collection("tasks")
                .add(taskMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateTaskActivity.this, "Task Saved!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity and go back to Dashboard
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateTaskActivity.this, "Error saving task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}