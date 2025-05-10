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

public class Login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signupLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Refers to your login.xml layout file

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);

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
