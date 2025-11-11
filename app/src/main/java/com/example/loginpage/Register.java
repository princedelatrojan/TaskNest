package com.example.loginpage; // Or com.example.tasknest if you've refactored

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// REMOVED: Imports for Spinner and ArrayAdapter, as they are no longer used.
// REMOVED: Import for java.lang.reflect.Array, as it was not used.

public class Register extends AppCompatActivity {

    // --- Member Variables ---
    // It's better practice to define your views and DB helper here
    private EditText inputFullName, inputUserEmail, inputPassword;
    private Button registerButton, buttonLoginLink;
    private ImageView togglePasswordVisibility;
    private MyDbHelper dbHelper;
    private SQLiteDatabase db;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // REMOVED: EdgeToEdge.enable(this); - This is fine, but often not needed
        setContentView(R.layout.activity_register);

        // --- Database Setup ---
        // Initialize the dbHelper and db as member variables
        dbHelper = new MyDbHelper(this, "login.db", null, 1);
        db = dbHelper.getWritableDatabase();

        // --- View Initialization ---
        // Find all the views from your new layout
        inputFullName = findViewById(R.id.input_fullName);
        inputUserEmail = findViewById(R.id.input_userEmail);
        inputPassword = findViewById(R.id.input_password);
        registerButton = findViewById(R.id.register_button);
        buttonLoginLink = findViewById(R.id.button_login_link); // ADDED: This is your new "Log in" text button
        togglePasswordVisibility = findViewById(R.id.toggle_password_visibility); // ADDED: The 'eye' icon

        // REMOVED: All code for backButton and genderSpinner, as they don't exist.

        // --- Click Listener for Register Button ---
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from the fields, .trim() removes any extra spaces
                String fullNameText = inputFullName.getText().toString().trim();
                String userEmailText = inputUserEmail.getText().toString().trim();
                String passwordText = inputPassword.getText().toString().trim();

                // REMOVED: All code for gender, mobile, address, and confirmPassword

                // --- Validation ---
                // A better check: Aare ANY of the fields empty?
                if (TextUtils.isEmpty(fullNameText) || TextUtils.isEmpty(userEmailText) || TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop the code here
                }

                // REMOVED: The "password does not match" check, as confirmPassword was removed.

                // --- Database Insert ---
                ContentValues values = new ContentValues();
                values.put("fullName", fullNameText);
                values.put("userEmail", userEmailText);
                values.put("password", passwordText);
                // REMOVED: puts for gender, mobile, and address

                long newRowId = db.insert("users", null, values);

                if (newRowId == -1) {
                    Toast.makeText(Register.this, "Error creating account", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Register.this, "Account created successfully! Please log in.", Toast.LENGTH_LONG).show();
                    // Send user back to the login screen
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the register screen so user can't go back
                }
            }
        });

        // --- ADDED: Click Listener for the "Log in" link ---
        buttonLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simply close this activity to go back to the login screen
                finish();
            }
        });

        // --- ADDED: Click Listener for the password 'eye' icon ---
        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    // --- ADDED: Helper method to toggle password visibility ---
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Show password
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on);
        }
        // Move cursor to the end
        inputPassword.setSelection(inputPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }


    // --- UPDATED: onDestroy ---
    // This is a more robust way to close your database helper
    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close(); // This closes the database connection
        }
        super.onDestroy();
    }
}