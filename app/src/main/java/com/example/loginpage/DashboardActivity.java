package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TaskNest Dashboard — displays live date/time, completed/pending counts,
 * and today’s task list from the local SQLite database.
 */
public class DashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private TextView txtCompletedTasks, txtPendingTasks, textDateTime;
    private ListView listTodayTasks;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        txtCompletedTasks = findViewById(R.id.txtCompletedTasks);
        txtPendingTasks = findViewById(R.id.txtPendingTasks);
        listTodayTasks = findViewById(R.id.listTodayTasks);
        textDateTime = findViewById(R.id.textDateTime);

        loadDashboardData();
        startClock();
    }

    /** Loads the dashboard data: task counts and today’s task list */
    private void loadDashboardData() {
        // Count completed tasks
        Cursor cursorCompleted = db.rawQuery(
                "SELECT COUNT(*) FROM tasks WHERE status='Completed'", null);
        if (cursorCompleted.moveToFirst()) {
            txtCompletedTasks.setText(cursorCompleted.getString(0));
        }
        cursorCompleted.close();

        // Count pending tasks
        Cursor cursorPending = db.rawQuery(
                "SELECT COUNT(*) FROM tasks WHERE status='Pending'", null);
        if (cursorPending.moveToFirst()) {
            txtPendingTasks.setText(cursorPending.getString(0));
        }
        cursorPending.close();

        // Load today's tasks
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        Cursor cursorToday = db.rawQuery(
                "SELECT _id, task_name, status FROM tasks WHERE date=?", new String[]{todayDate});

        String[] from = {"task_name", "status"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursorToday,
                from,
                to,
                0
        );
        listTodayTasks.setAdapter(adapter);
    }

    /** Updates the current date & time every second */
    private void startClock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "EEEE, MMM dd, yyyy - hh:mm:ss a", Locale.getDefault());
                String currentDateTime = sdf.format(new Date());
                textDateTime.setText(currentDateTime);
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }
}
