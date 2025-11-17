package com.example.loginpage; // Or com.example.tasknest

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// *** FIX 1: Import BottomAppBar ***
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    // *** FIX 2: Add a variable for the BottomAppBar ***
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // --- 1. Setup Toolbar ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // This makes the Toolbar act as the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("TaskNest");
        }

        // --- 2. Initialize Views ---
        // *** FIX 3: Initialize the BottomAppBar from the layout ***
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab_add_task);
        fragmentManager = getSupportFragmentManager();

        // --- 3. Load Default Fragment (Tasks) ---
        if (savedInstanceState == null) {
            loadFragment(new TasksFragment());
        }

        // --- 4. Bottom Navigation Listener ---
        // *** THE MAIN FIX: Set the listener on the BottomAppBar, NOT the BottomNavigationView ***
        bottomAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_tasks) {
                loadFragment(new TasksFragment());
                return true;
            } else if (itemId == R.id.nav_reports) {
                loadFragment(new ReportsFragment()); // Ensure ReportsFragment exists!
                return true;
            }
            return false;
        });

        // --- 5. FAB Listener (This remains the same and is correct) ---
        fab.setOnClickListener(v -> {
            // Ensure CreateTaskActivity exists and is registered in Manifest
            Intent intent = new Intent(DashboardActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });
    }

    // --- 6. Top Toolbar Menu Logic (This remains the same and is correct) ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, SettingsActivity.class);
            // startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- Helper Method to Switch Fragments (This is correct) ---
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}
