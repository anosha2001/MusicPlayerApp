package com.example.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class Profile extends Fragment {


    private TextView displayName, displayEmail;
    private EditText editName, editEmail;
    private ImageView editIconName, editIconEmail;
    private Button buttonUpdateProfile;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;

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

        profileImageView = view.findViewById(R.id.profileImage); // Add this ImageView to your XML
        loadProfilePicture(profileImageView);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();

                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

                            // Show image
                            profileImageView.setImageBitmap(selectedImage);

                            // Convert to Base64
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                            // Save to SharedPreferences
                            sharedPreferences.edit().putString("profile_pic", base64Image).apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        view.findViewById(R.id.fabUploadProfilePic).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });


        return view;
    }

    private void loadProfilePicture(ImageView imageView) {
        String base64Image = sharedPreferences.getString("profile_pic", null);
        if (base64Image != null) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
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