// We've added imports for Firebase Auth, Firestore, and the listeners
package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar; // Good to add a "loading" bar later
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

// REMOVED: All SQLite and ContentValues imports

public class Register extends AppCompatActivity {

    // --- Member Variables ---
    private EditText inputFullName, inputUserEmail, inputPassword;
    private Button registerButton, buttonLoginLink;
    private ImageView togglePasswordVisibility;
    private boolean isPasswordVisible = false;

    // --- Firebase Member Variables ---
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // --- REMOVED: MyDbHelper and SQLiteDatabase variables ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Firebase Initialization ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- REMOVED: Database Setup ---

        // --- View Initialization ---
        inputFullName = findViewById(R.id.input_fullName);
        inputUserEmail = findViewById(R.id.input_userEmail);
        inputPassword = findViewById(R.id.input_password);
        registerButton = findViewById(R.id.register_button);
        buttonLoginLink = findViewById(R.id.button_login_link);
        togglePasswordVisibility = findViewById(R.id.toggle_password_visibility);

        // --- Click Listener for Register Button ---
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from the fields
                String fullNameText = inputFullName.getText().toString().trim();
                String userEmailText = inputUserEmail.getText().toString().trim();
                String passwordText = inputPassword.getText().toString().trim();

                // --- Validation ---
                if (TextUtils.isEmpty(fullNameText) || TextUtils.isEmpty(userEmailText) || TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop the code here
                }

                // You should add a password strength check here (e.g., >= 6 characters)
                if (passwordText.length() < 6) {
                    inputPassword.setError("Password must be at least 6 characters");
                    inputPassword.requestFocus();
                    return;
                }

                // --- CHANGED: Firebase Account Creation ---
                // This is an asynchronous task
                mAuth.createUserWithEmailAndPassword(userEmailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // --- User created in Auth! Now save Full Name to Firestore ---
                                    Toast.makeText(Register.this, "Account created, saving details...", Toast.LENGTH_SHORT).show();
                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Create a new user "document" in Firestore
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("fullName", fullNameText);
                                    user.put("email", userEmailText);

                                    db.collection("users").document(userId).set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // --- EVERYTHING IS SUCCESSFUL ---
                                                    Toast.makeText(Register.this, "Account created successfully! Please log in.", Toast.LENGTH_LONG).show();
                                                    // Send user back to the login screen
                                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish(); // Close the register screen
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Firestore failed, but Auth worked. This is rare.
                                                    Toast.makeText(Register.this, "Error saving user details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                } else {
                                    // --- Auth failed (e.g., email already in use, bad password) ---
                                    Toast.makeText(Register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // --- Click Listener for the "Log in" link ---
        buttonLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity
            }
        });

        // --- Click Listener for the password 'eye' icon ---
        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    // --- Helper method to toggle password visibility (unchanged) ---
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
        } else {
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on);
        }
        inputPassword.setSelection(inputPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    // --- REMOVED: onDestroy() no longer needed for db.close() ---
}