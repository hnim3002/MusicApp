package com.example.btl_app_music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Position;
import com.example.btl_app_music.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddSongAdapter extends RecyclerView.Adapter<AddSongAdapter.AddSongViewHolder> {
    private List<MusicList> list;
    private final Context context;

    private IClickItemListener iClickItemListener;

    public interface IClickItemListener {
        void onClickItem(MusicList list1);
    }

    public AddSongAdapter(List<MusicList> list, Context context,IClickItemListener iClickItemListener) {
        this.list = list;
        this.context = context;
        this.iClickItemListener = iClickItemListener;
    }


    @NonNull
    @Override
    public AddSongAdapter.AddSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddSongViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_music_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AddSongViewHolder holder, int position) {
        MusicList list2 = list.get(position);


        holder.title.setText(list2.getTitle());
        holder.artist.setText(list2.getArtist());

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(holder.getAdapterPosition());
                iClickItemListener.onClickItem(list2);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AddSongViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rootLayout;
        private final TextView title;
        private final TextView artist;



        public AddSongViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.rootLayout);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);

        }
    }

}
