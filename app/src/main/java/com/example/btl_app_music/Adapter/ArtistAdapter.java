package com.example.btl_app_music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private List<Item> mItemArtist;

    private Context mContext;

    private int listType;

    private IClickItemListener iClickItemListener;

    public interface IClickItemListener {
        void onClickItem(Item item, int a);
    }

    public ArtistAdapter(List<Item> mItemArtist, Context mContext, int listType, IClickItemListener iClickItemListener) {
        this.mItemArtist = mItemArtist;
        this.mContext = mContext;
        this.listType = listType;
        this.iClickItemListener = iClickItemListener;
    }

    public void setData(List<Item> listData, Context context) {
        this.mItemArtist = listData;
        mContext = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_big, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        if(mItemArtist.size() != 0) {
            Item item = mItemArtist.get(position);
            if(item == null) {
                return;
            }
            holder.itemTxt.setText(item.getItemName());
            Glide.with(mContext).load(item.getItemImg()).error(R.drawable.default_avatar).into(holder.itemImg);

            int left = dpToPx(15);
            int top = dpToPx(15);
            int right = dpToPx(15);
            int bottom = dpToPx(20);

       /* int spanCount = 2;

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
            right = dpToPx(3);
        }
        if(isRightSide) {
            left = dpToPx(3);
        }*/


            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(left, top, right, bottom);

            holder.cardView.setLayoutParams(layoutParams);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iClickItemListener.onClickItem(item, listType);
                }
            });
        }


    }

    private int dpToPx(int dp) {
        float px = dp * mContext.getResources().getDisplayMetrics().density;
        return (int)px;
    }

    @Override
    public int getItemCount() {
        if(mItemArtist != null) {
            return 6;
        }
        return 0;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout cardView;

        private ImageView itemImg;
        private TextView itemTxt;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTxt = itemView.findViewById(R.id.item_title);
            itemImg = itemView.findViewById(R.id.forYou_img);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
