package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LikedSongs extends Fragment {

    RecyclerView recyclerLikedSongs;
    SongAdapter likedAdapter;
    List<Song> likedList = new ArrayList<>();

    DatabaseReference dbRef;

    public LikedSongs() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_liked_songs, container, false);

        // Initialize RecyclerView
        recyclerLikedSongs = view.findViewById(R.id.recyclerLikedSongs);
        recyclerLikedSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("songs");

        // Initialize Adapter for liked songs
        likedAdapter = new SongAdapter(getContext(), likedList, songActionListener);
        recyclerLikedSongs.setAdapter(likedAdapter);

        // Fetch liked songs
        fetchLikedSongs();

        return view;
    }

    private final SongAdapter.OnSongActionListener songActionListener = new SongAdapter.OnSongActionListener() {
        @Override
        public void onSongClick(Song song) {
            Intent intent = new Intent(getContext(), Player.class);
            intent.putExtra("songName", song.getSongName());
            intent.putExtra("artistName", song.getArtistName());
            intent.putExtra("songPath", song.getSongPath());
            intent.putExtra("songThumbnail", song.getSongThumbnail());
            startActivity(intent);
        }

        @Override
        public void onLikeToggle(Song song, boolean isLiked) {
            // Update the song's liked state in the Firebase database
            String songKey = song.getId();  // Assuming song has a unique ID
            DatabaseReference songRef = dbRef.child(songKey);

            // Toggle the like status
            songRef.child("isLiked").setValue(isLiked);

            // Update the song object and refresh adapters
            song.setLiked(isLiked);
            likedList.remove(song);
            likedAdapter.notifyDataSetChanged();
        }
    };

    private void fetchLikedSongs() {
        // Query to fetch songs that are liked (isLiked = true)
        dbRef.orderByChild("isLiked").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        likedList.clear();  // Clear the existing liked list

                        // Add all liked songs to the list
                        for (DataSnapshot songSnap : snapshot.getChildren()) {
                            Song song = songSnap.getValue(Song.class);
                            if (song != null) {
                                likedList.add(song);
                            }
                        }

                        likedAdapter.notifyDataSetChanged();  // Update the UI with liked songs
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                        Log.e("LikedSongs", "Error fetching liked songs.", error.toException());
                    }
                });
    }
}
