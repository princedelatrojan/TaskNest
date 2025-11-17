package com.example.loginpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportsFragment extends Fragment {

    private TextView textTotalCount, textMotivation;
    private Button btnSettings;

    private FirebaseFirestore db;
    private String currentUserId;

    public ReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textTotalCount = view.findViewById(R.id.text_total_count);
        textMotivation = view.findViewById(R.id.text_motivation);
        btnSettings = view.findViewById(R.id.btn_settings);

        loadReportData();

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Settings Clicked", Toast.LENGTH_SHORT).show();
            // You can add intent to open SettingsActivity here later
        });
    }

    private void loadReportData() {
        // Calculate Total Tasks
        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int total = querySnapshot.size();
                    textTotalCount.setText(String.valueOf(total));

                    // Calculate Completed for Motivation Text
                    // (This is a simple version, ideally you filter for this week)
                    long completed = querySnapshot.getDocuments().stream()
                            .filter(doc -> "Completed".equals(doc.getString("status")))
                            .count();

                    int percentage = (total == 0) ? 0 : (int) ((completed * 100) / total);

                    textMotivation.setText("Great job! " + percentage + "% tasks completed.");
                });
    }
}