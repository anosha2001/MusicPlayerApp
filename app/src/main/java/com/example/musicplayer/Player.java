package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class Player extends AppCompatActivity {

    private TextView tvSongName, tvArtistName;
    private ImageView ivThumbnail;
    private Button btnPlayPause;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String songPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvSongName = findViewById(R.id.tvPlayerSongName);
        tvArtistName = findViewById(R.id.tvPlayerArtistName);
        ivThumbnail = findViewById(R.id.ivPlayerThumbnail);
        btnPlayPause = findViewById(R.id.btnPlayPause);

        // Get data from intent
        String songName = getIntent().getStringExtra("songName");
        String artistName = getIntent().getStringExtra("artistName");
        songPath = getIntent().getStringExtra("songPath");
        String songThumbnail = getIntent().getStringExtra("songThumbnail");

        tvSongName.setText(songName);
        tvArtistName.setText(artistName);
        Glide.with(this).load(songThumbnail).into(ivThumbnail);

        // Initialize MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepareAsync(); // Prepare asynchronously
            mediaPlayer.setOnPreparedListener(mp -> {
                isPlaying = true;
                mediaPlayer.start();
                btnPlayPause.setText("Pause");
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading song", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    btnPlayPause.setText("Play");
                } else {
                    mediaPlayer.start();
                    btnPlayPause.setText("Pause");
                }
                isPlaying = !isPlaying;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}