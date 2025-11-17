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
        // Get the task for the current row
        Task task = taskList.get(position);

        // Set the text
        holder.textTaskName.setText(task.getTaskName());
        holder.textDueDate.setText(task.getDueDate());

        // Set the Priority Color Indicator
        // (Assumes you have these colors in colors.xml)
        if ("High".equals(task.getPriority())) {
            holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_high));
        } else if ("Medium".equals(task.getPriority())) {
            holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_medium));
        } else {
            holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_low));
        }

        // Handle Checkbox Logic (Just visual for now)
        holder.checkBox.setChecked("Completed".equals(task.getStatus()));
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