package com.example.loginpage; // Or com.example.tasknest

public class Task {
    private String id;     // This holds the Firestore document ID
    private String taskId;     // We need this to update/delete later
    private String taskName;
    private String dueDate;
    private String priority;   // "High", "Medium", "Low"
    private String status;     // "Pending", "Completed"
    private String userId;

    // 1. Empty Constructor (REQUIRED for Firebase to read data)
    public Task() {}

    private String description;
    private long timestamp;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }



    // --- Add these getter and setter for ID ---


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    // 2. Full Constructor (To create new tasks easily)
    public Task(String taskId, String taskName, String dueDate, String priority, String status, String userId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.userId = userId;
    }

    // 3. Getters (Right-click -> Generate -> Getters and Setters)
    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getUserId() { return userId; }

    // Setters (Optional, but good to have)
    public void setStatus(String status) { this.status = status; }
}