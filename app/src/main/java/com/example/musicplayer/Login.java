package com.example.musicplayer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signupLink;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Refers to your login.xml layout file

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        // Link UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);

        // Load saved credentials if available
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        emailInput.setText(savedEmail);
        passwordInput.setText(savedPassword);

        // Handle Login Button Click
        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required");
                return;
            }

            // Log in with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Get user (optional: retrieve more info)
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", user.getEmail());
                                // If you have username stored separately, fetch and save it too
                                editor.apply();
                            }

                            // Redirect to Home fragment
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("fragment", "Home");
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle Signup Link Click
        signupLink.setOnClickListener(view -> {
            startActivity(new Intent(Login.this, Signup.class));
            finish();
        });
    }
}
