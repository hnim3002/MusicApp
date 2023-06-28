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

import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Position;
import com.example.btl_app_music.R;
import com.example.btl_app_music.SongChangeListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MusicAdapterOnline extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MusicList> list;
    private final Context context;

    private int playlistType;
    private int playingPosition = 0;

    private final SongChangeListener songChangeListener;

    public MusicAdapterOnline(int playlistType,List<MusicList> list, Context context) {
        this.playlistType = playlistType;
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener)context);
    }
    


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
        } else if(viewType == 1) {
            return new MyViewHolder2(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if(playlistType == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusicList list2 = list.get(holder.getAdapterPosition());

        if(holder.getItemViewType() == 0) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            if(Objects.equals(Position.currentName, list2.getTitle())) {
                playingPosition = holder.getAdapterPosition();
                myViewHolder.title.setTextColor(Color.parseColor("#0e0d0f"));
                myViewHolder.musicDuration.setTextColor(Color.parseColor("#0e0d0f"));
                myViewHolder.imageView.setImageResource(R.drawable.ic_af_adobe_express);
            }
            else {
                myViewHolder.title.setTextColor(Color.parseColor("#707070"));
                myViewHolder.musicDuration.setTextColor(Color.parseColor("#707070"));
                myViewHolder.imageView.setImageResource(R.drawable.ic_play_adobe_express);
            }

            myViewHolder.title.setText(list2.getTitle());
            myViewHolder.artist.setText(list2.getArtist());
            myViewHolder.musicDuration.setText(list2.getDuration());


            myViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.onOf = false;
                    if(OnlineList.musicListsOnline.size() != list.size()) {
                        if(playingPosition <= list.size()) {
                            list.get(playingPosition).setPlaying(false);
                        }

                        list2.setPlaying(true);
                    }

                    int a = 0;
                    for (int i = 0; i < OnlineList.musicListsOnline.size(); i++) {
                        if(OnlineList.musicListsOnline.get(i).getTitle().equals(list2.getTitle())) {
                            a = i;
                        }
                    }

                    //Position.getInstance().reset();
                    Position.currentIndex = a;
                    Position.currentName = list2.getTitle();
                    songChangeListener.onChanged(Position.currentIndex);
                    MainActivity.musicPlayerList = new ArrayList<>(list);
                    notifyDataSetChanged();
                }
            });
        } else if(holder.getItemViewType() == 1 ) {
            MyViewHolder2 myViewHolder = (MyViewHolder2) holder;

            if(Objects.equals(Position.currentName, list2.getTitle())) {
                playingPosition = holder.getAdapterPosition();
                myViewHolder.title.setTextColor(Color.parseColor("#0e0d0f"));
                myViewHolder.musicDuration.setTextColor(Color.parseColor("#0e0d0f"));
                myViewHolder.imageView.setImageResource(R.drawable.ic_af_adobe_express);
            }
            else {
                myViewHolder.title.setTextColor(Color.parseColor("#707070"));
                myViewHolder.musicDuration.setTextColor(Color.parseColor("#707070"));
                myViewHolder.imageView.setImageResource(R.drawable.ic_play_adobe_express);
            }

            myViewHolder.title.setText(list2.getTitle());
            myViewHolder.artist.setText(list2.getArtist());
            myViewHolder.musicDuration.setText(list2.getDuration());


            myViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.onOf = false;
                    if(OnlineList.musicListsOnline.size() != list.size()) {
                        if(playingPosition <= list.size()) {
                            list.get(playingPosition).setPlaying(false);
                        }

                        list2.setPlaying(true);
                    }

                    int a = 0;
                    for (int i = 0; i < OnlineList.musicListsOnline.size(); i++) {
                        if(OnlineList.musicListsOnline.get(i).getTitle().equals(list2.getTitle())) {
                            a = i;
                        }
                    }

                    /*Position.getInstance().reset();*/
                    Position.currentIndex = a;
                    Position.currentName = list2.getTitle();
                    songChangeListener.onChanged(Position.currentIndex);
                    MainActivity.musicPlayerList.addAll(list);
                    notifyDataSetChanged();
                }
            });
        }

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
        if(list == null) {
            return 0;
        } else {
            return list.size();
        }

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
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

        }




    }

    static class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private final RelativeLayout rootLayout;
        private final TextView title;
        private final TextView artist;
        private final TextView musicDuration;
        private final ImageView imageView;

        public MyViewHolder2(@NonNull View itemView) {
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
            menu.add(this.getAdapterPosition(), 213, 0, "Xóa");
        }
        //___________________//


    }

}
