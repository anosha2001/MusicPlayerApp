package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Set;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnSongActionListener listener;

    public interface OnSongActionListener {
        void onSongClick(Song song);
        void onLikeToggle(Song song, boolean isLiked);
    }

    public SongAdapter(Context context, List<Song> songList, OnSongActionListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_card, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);

        holder.songName.setText(song.getSongName());
        holder.artistName.setText(song.getArtistName());
        Glide.with(context).load(song.getSongThumbnail()).into(holder.thumbnail);

        // Set the like icon based on the `isLiked` value
        boolean isLiked = song.isLiked();
        holder.likeIcon.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // Set item click listener
        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));

        // Toggle the like status when the like icon is clicked
        holder.likeIcon.setOnClickListener(v -> {
            boolean newLikeStatus = !song.isLiked();  // Toggle the like status
            song.setLiked(newLikeStatus);  // Update the local song object

            // Update the song's like status in Firebase
            DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("songs").child(song.getId());
            songRef.child("isLiked").setValue(newLikeStatus);  // Update Firebase

            // Notify that the item has changed (to update the like icon)
            notifyItemChanged(holder.getAdapterPosition());

            // Trigger the callback for like toggle
            listener.onLikeToggle(song, newLikeStatus);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songName, artistName;
        ImageView thumbnail, likeIcon;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.tvSongName);
            artistName = itemView.findViewById(R.id.tvArtistName);
            thumbnail = itemView.findViewById(R.id.ivThumbnail);
            likeIcon = itemView.findViewById(R.id.ivLike);
        }
    }
}