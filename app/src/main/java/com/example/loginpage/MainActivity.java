// Added Firebase imports
package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// REMOVED: All SQLite imports

public class MainActivity extends AppCompatActivity {

    // --- REMOVED: SQLiteDatabase and MyDbHelper variables ---

    // --- Firebase Member Variables ---
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // REMOVED: EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- Firebase Initialization ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- ADDED: Check if user is already logged in ---
        // If so, skip login and go straight to Home
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }

        // --- Find views (using your old IDs) ---
        Button register = findViewById(R.id.button_register); // Assumes ID is from new layout
        register.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        Button forgot = findViewById(R.id.button_forgot_password);
        forgot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // --- REMOVED: dbHelper and db initialization ---

        Button login = findViewById(R.id.button_login);
        login.setOnClickListener(v -> {
            EditText email = findViewById(R.id.input_userEmail);
            String emailText = email.getText().toString().trim();

            EditText password = findViewById(R.id.input_password);
            String passwordText = password.getText().toString().trim();

            if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
                Toast.makeText(this, "Please Enter Your Email And Password", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- CHANGED: Firebase Sign In ---
            // This is an asynchronous task
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // --- Login Succeeded ---

                                // --- Replicating your Admin check ---
                                // NOTE: This is not a secure way to handle "admin".
                                // A better way is Firebase Custom Claims, but this works for now.
                                if (emailText.equals("admin@123.com")) {
                                    Toast.makeText(MainActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // --- It's a normal user, let's get their name ---
                                    String userId = mAuth.getCurrentUser().getUid();
                                    db.collection("users").document(userId).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    String fullName = "User"; // Default
                                                    if (documentSnapshot.exists()) {
                                                        fullName = documentSnapshot.getString("fullName");
                                                    }
                                                    Toast.makeText(MainActivity.this, "Welcome " + fullName, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                            } else {
                                // --- Login Failed ---
                                Toast.makeText(MainActivity.this, "Invalid Email or Password: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                password.setText("");
                            }
                        }
                    });
        });
    }

    // --- REMOVED: onDestroy() no longer needed for db.close() ---
}