package com.example.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Profile extends Fragment {


    private TextView displayName, displayEmail;
    private EditText editName, editEmail;
    private ImageView editIconName, editIconEmail;
    private Button buttonUpdateProfile;

    SharedPreferences sharedPreferences;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        displayName = view.findViewById(R.id.displayName);
        displayEmail = view.findViewById(R.id.displayEmail);

        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);

        editIconName = view.findViewById(R.id.editIconName);
        editIconEmail = view.findViewById(R.id.editIconEmail);

        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile);

        // SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "Guest");
        String email = sharedPreferences.getString("email", "No email");

        displayName.setText("Name: " + name);

        loadProfile();

        // Edit Icon Click Listeners
        editIconName.setOnClickListener(v -> toggleEditField(editName));
        editIconEmail.setOnClickListener(v -> toggleEditField(editEmail));

        // Update Button
        buttonUpdateProfile.setOnClickListener(v -> saveProfile());

        // Optional: Upload Picture Button
        view.findViewById(R.id.fabUploadProfilePic).setOnClickListener(v -> {
            // TODO: Add image upload logic (camera/gallery intent)
            Toast.makeText(getContext(), "Upload picture clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void toggleEditField(EditText editText) {
        if (editText.getVisibility() == View.GONE) {
            editText.setVisibility(View.VISIBLE);
        } else {
            editText.setVisibility(View.GONE);
        }
    }

    private void loadProfile() {
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");

        displayName.setText("Name: " + name);
        displayEmail.setText("Email: " + email);

        editName.setText(name);
        editEmail.setText(email);

        // Hide editable fields by default
        editName.setVisibility(View.GONE);
        editEmail.setVisibility(View.GONE);
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        // Optionally validate inputs
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Name and Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.apply();

        // Update UI
        displayName.setText("Name: " + name);
        displayEmail.setText("Email: " + email);

        // Hide editable fields
        editName.setVisibility(View.GONE);
        editEmail.setVisibility(View.GONE);

        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
    }
}