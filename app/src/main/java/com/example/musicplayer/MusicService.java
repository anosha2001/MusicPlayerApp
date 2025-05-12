package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {

    public static final String CHANNEL_ID = "MusicPlayerServiceChannel";
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MusicService", "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String songPath = intent.getStringExtra("songPath");
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");

        // Create notification channel
        createNotificationChannel();

        // Start Foreground service with a notification
        startForeground(1, buildNotification(songName, artistName));

        // Initialize and start MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    private Notification buildNotification(String songName, String artistName) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songName)
                .setContentText(artistName)
                .setSmallIcon(R.drawable.ic_music_placeholder) // Ensure you have an icon here
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Music Player Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
