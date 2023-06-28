package com.example.btl_app_music.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_app_music.Fragment.SubFragment.LocalList;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Position;
import com.example.btl_app_music.R;
import com.example.btl_app_music.SongChangeListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private List<MusicList> list;
    private final Context context;
    private int playingPosition = 0;
    private final SongChangeListener songChangeListener;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener)context);
    }
    
    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, int position) {

        MusicList list2 = list.get(position);



        if(LocalList.musicLists.size() == list.size()) {

            /*if(Position.currentIndex == position) {
                playingPosition = holder.getAdapterPosition();
                holder.title.setTextColor(Color.parseColor("#0e0d0f"));
                holder.musicDuration.setTextColor(Color.parseColor("#0e0d0f"));
                holder.imageView.setImageResource(R.drawable.ic_af_adobe_express);
            }
            else {
                holder.title.setTextColor(Color.parseColor("#707070"));
                holder.musicDuration.setTextColor(Color.parseColor("#707070"));
                holder.imageView.setImageResource(R.drawable.ic_play_adobe_express);
            }*/
            if(Objects.equals(Position.currentName, list2.getTitle())) {
                playingPosition = holder.getAdapterPosition();
                holder.title.setTextColor(Color.parseColor("#0e0d0f"));
                holder.musicDuration.setTextColor(Color.parseColor("#0e0d0f"));
                holder.imageView.setImageResource(R.drawable.ic_af_adobe_express);
            }
            else {
                holder.title.setTextColor(Color.parseColor("#707070"));
                holder.musicDuration.setTextColor(Color.parseColor("#707070"));
                holder.imageView.setImageResource(R.drawable.ic_play_adobe_express);
            }

        }
        else {
            if(list2.isPlaying()) {
                playingPosition = holder.getAdapterPosition();
                holder.title.setTextColor(Color.parseColor("#0e0d0f"));
                holder.musicDuration.setTextColor(Color.parseColor("#0e0d0f"));
                holder.imageView.setImageResource(R.drawable.ic_af_adobe_express);
            }
            else {
                holder.title.setTextColor(Color.parseColor("#707070"));
                holder.musicDuration.setTextColor(Color.parseColor("#707070"));
                holder.imageView.setImageResource(R.drawable.ic_play_adobe_express);
            }
        }

        if(!MainActivity.onOf) {
            holder.title.setTextColor(Color.parseColor("#707070"));
            holder.musicDuration.setTextColor(Color.parseColor("#707070"));
            holder.imageView.setImageResource(R.drawable.ic_play_adobe_express);

        }

        String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(list2.getDuration())) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration()))));



        holder.title.setText(list2.getTitle());
        holder.artist.setText(list2.getArtist());
        holder.musicDuration.setText(generateDuration);


        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.onOf = true;
                if(LocalList.musicLists.size() != list.size()) {
                    if(playingPosition <= list.size()) {
                        list.get(playingPosition).setPlaying(false);
                    }

                    list2.setPlaying(true);
                }

                int a = 0;
                for (int i = 0; i < LocalList.musicLists.size(); i++) {
                    if(LocalList.musicLists.get(i).getTitle().equals(list2.getTitle())) {
                        a = i;
                    }
                }

                //Position.getInstance().reset();
                Position.currentIndex = a;
                Position.currentName = list2.getTitle();
                songChangeListener.onChanged(Position.currentIndex);

                notifyDataSetChanged();
            }
        });
    }


    //upadate//
    public void updateList(List<MusicList> list) {
        this.list = list;
        notifyDataSetChanged();

    }
    //_________________________//


    //Xoa 1 list //
    public void Delete(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }
    //____________________________________//




    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private final RelativeLayout rootLayout;
        private final TextView title;
        private final TextView artist;
        private final TextView musicDuration;
        private final ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.playInAdapter);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
            //Hien thi menu xoa//
            rootLayout.setOnCreateContextMenuListener(this);
            //________________________________________________________/
        }

        //Hien thi menu xoa//
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 212, 0, "Xóa");
        }
        //___________________//


    }

}
