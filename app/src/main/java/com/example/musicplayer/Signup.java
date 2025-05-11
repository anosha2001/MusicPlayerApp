package com.example.musicplayer;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    private EditText nameInput, emailInput, passwordInput;
    private Button signupButton;
    private TextView loginLink;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Refers to your signup.xml layout file

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Link UI components
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        // Handle Signup Button Click
        signupButton.setOnClickListener(view -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(name)) {
                nameInput.setError("Name is required");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() < 6) {
                passwordInput.setError("Password must be at least 6 characters");
                return;
            }

            // Create a user in Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Get user ID from Firebase
                            String userId = mAuth.getCurrentUser().getUid();

                            // Create a User object
                            User user = new User(name, email, password);

                            // Save user data in Realtime Database
                            databaseReference.child(userId).setValue(user)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                                    .edit()
                                                    .putString("name", name)
                                                    .putString("password",password)
                                                    .putString("email", email)
                                                    .apply();

                                            Toast.makeText(Signup.this, "Account Created", Toast.LENGTH_SHORT).show();

                                            // Redirect to login or home screen
                                            startActivity(new Intent(Signup.this, Login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Signup.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Signup.this, "Sign-Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle Login Link Click
        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(Signup.this, Login.class));
            finish();
        });
    }

    // User class for storing data in Firebase
    public static class User {
        public String name, email,password;

        public User(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }
}
