package com.example.loginpage; // Or com.example.tasknest

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    // Constructor
    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This line tells the adapter to use your 'item_task.xml' layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Remove old listener to avoid unexpected triggers from recycling views
        holder.checkBox.setOnCheckedChangeListener(null);

        // Set basic view
        holder.textTaskName.setText(task.getTaskName());
        holder.textDueDate.setText(task.getDueDate());

        if ("High".equals(task.getPriority())) {
            holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_high));
        } else if ("Medium".equals(task.getPriority())) {
            holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_medium));
        } else {
            holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_low));
        }

        // Set checkbox state
        holder.checkBox.setChecked("Completed".equals(task.getStatus()));

        // --- Listen for checkbox change ---
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Only handle user-initiated changes
            if (!buttonView.isPressed()) return;
            String newStatus = isChecked ? "Completed" : "Pending";
            if (!newStatus.equals(task.getStatus())) {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("tasks")
                        .document(task.getId())
                        .update("status", newStatus)
                        .addOnSuccessListener(aVoid -> {
                            task.setStatus(newStatus);
                            notifyItemChanged(position);

                            // Force parent fragment to reload counts and list!
                            android.app.Activity activity = (android.app.Activity) context;
                            activity.runOnUiThread(() -> {
                                // This assumes your fragment container uses R.id.main_fragment_container
                                if (activity instanceof DashboardActivity) {
                                    androidx.fragment.app.FragmentManager fm =
                                            ((DashboardActivity) activity).getSupportFragmentManager();
                                    androidx.fragment.app.Fragment fragment =
                                            fm.findFragmentById(R.id.main_fragment_container);
                                    if (fragment instanceof TasksFragment) {
                                        ((TasksFragment) fragment).loadTaskCounts();
                                        ((TasksFragment) fragment).loadTaskList();
                                    }
                                }
                            });
                        });
            }
        });

        // --- Edit on card click ---
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, EditTaskActivity.class);
            intent.putExtra("taskId", task.getId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // This inner class holds the views for ONE row (to save memory)
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView textTaskName, textDueDate;
        View priorityIndicator;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find the views from item_task.xml
            textTaskName = itemView.findViewById(R.id.text_task_name);
            textDueDate = itemView.findViewById(R.id.text_task_date);
            priorityIndicator = itemView.findViewById(R.id.view_priority_indicator);
            checkBox = itemView.findViewById(R.id.checkbox_complete);
        }
    }
}