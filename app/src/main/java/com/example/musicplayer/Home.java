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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Home extends Fragment {

    RecyclerView recyclerLiked, recyclerNew;
    SongAdapter likedAdapter, newAdapter;
    List<Song> likedList = new ArrayList<>();
    List<Song> newList = new ArrayList<>();

    DatabaseReference dbRef;

    public Home() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerViews
        recyclerLiked = view.findViewById(R.id.recyclerLikedSongs);
        recyclerNew = view.findViewById(R.id.recyclerNewSongs);

        recyclerLiked.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerNew.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("songs");

        // Initialize Adapters for liked and new songs
        likedAdapter = new SongAdapter(getContext(), likedList, songActionListener);
        newAdapter = new SongAdapter(getContext(), newList, songActionListener);

        recyclerLiked.setAdapter(likedAdapter);
        recyclerNew.setAdapter(newAdapter);

        // Fetch liked songs and new songs
        fetchLikedSongs();
        fetchNewSongs();

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
            likedAdapter.notifyDataSetChanged();
            newAdapter.notifyDataSetChanged();
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
                    }
                });
    }

    private void fetchNewSongs() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list if you want to re-fetch or add data to it if you want to accumulate
                newList.clear();  // Optionally clear the list

                // Fetch and process the data, even if it's the same as before
                for (DataSnapshot songSnap : snapshot.getChildren()) {
                    Song song = songSnap.getValue(Song.class);
                    if (song != null) {
                        newList.add(song);  // Add to the list
                    }
                }

                // Send data to UI or perform any action (you can modify it based on your requirement)
                newAdapter.notifyDataSetChanged();  // Update UI (or send data)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e("FetchNewSongs", "Failed to read value.", error.toException());
            }
        });
    }

}