package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class Player extends AppCompatActivity {

    private TextView tvSongName, tvArtistName, tvCurrentTime, tvDuration;
    private ImageView ivThumbnail;
    private Button btnPlayPause, btnBack;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String songPath;

    private Handler handler = new Handler();

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
        btnBack = findViewById(R.id.btnBack);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvDuration = findViewById(R.id.tvDuration);

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
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                isPlaying = true;
                mediaPlayer.start();
                btnPlayPause.setText("Pause");
                seekBar.setMax(mediaPlayer.getDuration()); // Set max seek bar value to song duration
                updateSeekBar();
                tvDuration.setText(formatTime(mediaPlayer.getDuration()));
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading song", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Play/Pause Button
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

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // SeekBar Listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition())); // Update current time
            handler.postDelayed(this::updateSeekBar, 1000); // Update every second
        }
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
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
