package com.example.loginpage; // Or com.example.tasknest

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Added for RecyclerView
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task; // Note: This clashes with your model class 'Task'
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList; // Added
import java.util.Date;
import java.util.List; // Added
import java.util.Locale;

public class TasksFragment extends Fragment {

    // --- Firebase Variables ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;

    // --- View Variables ---
    private TextView txtCompletedTasks, txtPendingTasks, textDateTime;
    private RecyclerView recyclerViewTasks;

    // --- Adapter Variables (NEW) ---
    // We need a list to hold data and an adapter to show it
    // Note: We use full package name for your model 'Task' to avoid conflict with Google's 'Task'
    private List<com.example.loginpage.Task> taskList;
    private TaskAdapter adapter;

    // --- Clock Handler ---
    private Handler handler = new Handler();

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Initialize Firebase ---
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        currentUserId = currentUser.getUid();

        // --- Find Views ---
        txtCompletedTasks = view.findViewById(R.id.txtCompletedTasks);
        txtPendingTasks = view.findViewById(R.id.txtPendingTasks);
        textDateTime = view.findViewById(R.id.textDateTime);
        recyclerViewTasks = view.findViewById(R.id.recycler_view_tasks);

        // --- Setup RecyclerView (NEW) ---
        // 1. Tell the RecyclerView to list items vertically
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Initialize the empty list
        taskList = new ArrayList<>();

        // 3. Initialize the adapter and connect it to the list
        adapter = new TaskAdapter(getContext(), taskList);

        // 4. Connect the adapter to the RecyclerView
        recyclerViewTasks.setAdapter(adapter);

        // --- Start Features ---
        startClock();
        loadTaskCounts();
        loadTaskList(); // Calling the new method to fetch the list
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTaskCounts();
        loadTaskList();
    }



    /** (NEW) Loads the actual list of tasks for the RecyclerView */
    public void loadTaskList() {
        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    taskList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        com.example.loginpage.Task task = doc.toObject(com.example.loginpage.Task.class);
                        if (task != null) {
                            task.setId(doc.getId()); // CRITICALLY IMPORTANT
                            taskList.add(task);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading task list", Toast.LENGTH_SHORT).show();
                });
    }


    /** Loads the task counts */
    public void loadTaskCounts() {
        // 1. Get Completed Count
        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("status", "Completed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        txtCompletedTasks.setText(String.valueOf(count));
                    }
                });

        // 2. Get Pending Count
        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("status", "Pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        txtPendingTasks.setText(String.valueOf(count));
                    }
                });
    }

    /** Updates the clock */
    private void startClock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "EEEE, MMM dd, yyyy - hh:mm:ss a", Locale.getDefault()); // Fixed typo "ynn" to "yyyy"
                String currentDateTime = sdf.format(new Date());

                if (textDateTime != null) {
                    textDateTime.setText(currentDateTime);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}