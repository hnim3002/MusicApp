package com.example.btl_app_music.Adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_app_music.Fragment.BottomNavFragment.Home;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.Object.ListItem;
import com.example.btl_app_music.R;

import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> {

    public static final int TYPE_FORYOU = 1;
    public static final int TYPE_ARTIST = 2;

    List<ListItem> mListItem;

    private final Context context;

    private ArtistAdapter.IClickItemListener iClickItemListener;
    private YourPlayListAdapter.IClickItemListener iClickItemListener1;


    private IClickItemListener2 iClickItemListener2;

    public interface IClickItemListener2 {
        void onClickItem(int playlistType);
    }


    public int getItemViewType(int position) {
        return mListItem.get(position).getType();
    }


    public String getItemCategory(int position) {
        return mListItem.get(position).getItemCategory();
    }

    public ListItemAdapter(List<ListItem> mListItem, Context context, YourPlayListAdapter.IClickItemListener iClickItemListener1, ArtistAdapter.IClickItemListener iClickItemListener, IClickItemListener2 iClickItemListener2) {
        this.mListItem = mListItem;
        this.context = context;
        this.iClickItemListener1 = iClickItemListener1;
        this.iClickItemListener = iClickItemListener;
        this.iClickItemListener2 = iClickItemListener2;
    }

    @NonNull
    @Override
    public ListItemAdapter.ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        ListItem listItem = mListItem.get(position);
        if(listItem == null) {
            return;
        }
        if(holder.getItemViewType() == TYPE_FORYOU) {
            LinearLayoutManager linearLayoutManager = new GridLayoutManager(context, 2);
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            YourPlayListAdapter yourPlayListAdapter = new YourPlayListAdapter(listItem.getItemForYou(), context, new YourPlayListAdapter.IClickItemListener() {
                @Override
                public void onClickItem(Item item) {
                    iClickItemListener1.onClickItem(item);
                }
            });
            holder.recyclerView.setAdapter(yourPlayListAdapter);
            holder.itemName.setVisibility(View.GONE);

        } else if(holder.getItemViewType() == TYPE_ARTIST) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(linearLayoutManager);

            ArtistAdapter artistAdapter = new ArtistAdapter(listItem.getItemArtist(), context, listItem.getListType(), new ArtistAdapter.IClickItemListener() {
                @Override
                public void onClickItem(Item item, int a) {
                    iClickItemListener.onClickItem(item, a);
                }
            });


            holder.itemName.setText(listItem.getItemCategory());
            holder.recyclerView.setAdapter(artistAdapter);


            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iClickItemListener2.onClickItem(listItem.getListType());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mListItem != null) {
            return mListItem.size();
        }
        return 0;
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout rootLayout;
        private TextView itemName;
        private RecyclerView recyclerView;
        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.item_holder);
            itemName = itemView.findViewById(R.id.item_title);
            recyclerView = itemView.findViewById(R.id.recyclerViewItem);
        }
    }
}
