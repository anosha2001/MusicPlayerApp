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
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerSearchResults;
    private SongAdapter searchAdapter;
    private List<Song> searchResults = new ArrayList<>();
    private DatabaseReference dbRef;

    public Search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize Firebase Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("songs");

        // Initialize UI components
        searchView = view.findViewById(R.id.searchView);
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults);

        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SongAdapter(getContext(), searchResults, songActionListener);
        recyclerSearchResults.setAdapter(searchAdapter);

        // Add listener to the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSongs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSongs(newText);
                return true;
            }
        });

        return view;
    }

    private void searchSongs(String query) {
        Query songNameQuery = dbRef.orderByChild("songName").startAt(query).endAt(query + "\uf8ff");
        Query artistNameQuery = dbRef.orderByChild("artistName").startAt(query).endAt(query + "\uf8ff");

        searchResults.clear();

        songNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot songSnap : snapshot.getChildren()) {
                    Song song = songSnap.getValue(Song.class);
                    if (song != null && !searchResults.contains(song)) {
                        searchResults.add(song);
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchSongs", "Error querying songName.", error.toException());
            }
        });

        artistNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot songSnap : snapshot.getChildren()) {
                    Song song = songSnap.getValue(Song.class);
                    if (song != null && !searchResults.contains(song)) {
                        searchResults.add(song);
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchSongs", "Error querying artistName.", error.toException());
            }
        });
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
            String songKey = song.getId();
            DatabaseReference songRef = dbRef.child(songKey);

            songRef.child("isLiked").setValue(isLiked);
            song.setLiked(isLiked);
            searchAdapter.notifyDataSetChanged();
        }
    };
}
