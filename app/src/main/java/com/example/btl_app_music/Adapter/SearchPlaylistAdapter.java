package com.example.btl_app_music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.R;

import java.util.List;

public class SearchPlaylistAdapter extends RecyclerView.Adapter<SearchPlaylistAdapter.SearchPlaylistViewHolder> {


    private List<Item> mItemArtist;

    private Context mContext;

    private IClickItemListener iClickItemListener;


    public interface IClickItemListener {
        void onClickItem(Item item);
    }

    public SearchPlaylistAdapter(List<Item> mItemArtist, Context mContext, IClickItemListener iClickItemListener) {
        this.mItemArtist = mItemArtist;
        this.mContext = mContext;
        this.iClickItemListener = iClickItemListener;
    }

    @NonNull
    @Override
    public SearchPlaylistAdapter.SearchPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchPlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_search_menu, null));
    }




    @Override
    public void onBindViewHolder(@NonNull SearchPlaylistViewHolder holder, int position) {
        Item item = mItemArtist.get(position);
        if(item == null) {
            return;
        }
        holder.itemTxt.setText(item.getItemName());
        if (!item.getItemImg().equals("")) {
            Glide.with(mContext).load(item.getItemImg()).error(R.drawable.default_avatar).into(holder.itemImg);

        }

        int left = dpToPx(16);
        int top = dpToPx(15);
        int right = dpToPx(16);
        int bottom = dpToPx(15);

        int spanCount = 2;

        boolean isFirst2Item = position < spanCount;
        boolean isLast2Item = position > getItemCount() - spanCount;

        if(isFirst2Item) {
            top = dpToPx(24);

        }

        if(isLast2Item) {
            bottom = dpToPx(24);
        }

        boolean isLeftSide = (position + 1) %  spanCount != 0;
        boolean isRightSide = !isLeftSide;
        if(isLeftSide) {
            right = dpToPx(10);
        }
        if(isRightSide) {
            left = dpToPx(10);
        }


        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.cardView.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);

        holder.cardView.setLayoutParams(layoutParams);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemListener.onClickItem(item);
            }
        });
    }

    public void updateList(List<Item> list) {
        mItemArtist = list;
        notifyDataSetChanged();

    }

    private int dpToPx(int dp) {
        float px = dp * mContext.getResources().getDisplayMetrics().density;
        return (int)px;
    }

    @Override
    public int getItemCount() {
        return mItemArtist.size();
    }

    public class SearchPlaylistViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout cardView;

        private ImageView itemImg;
        private TextView itemTxt;

        public SearchPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTxt = itemView.findViewById(R.id.item_title);
            itemImg = itemView.findViewById(R.id.forYou_img);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
