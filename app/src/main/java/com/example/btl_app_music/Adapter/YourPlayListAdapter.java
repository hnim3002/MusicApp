package com.example.btl_app_music.Adapter;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.R;

import java.util.List;

public class YourPlayListAdapter extends RecyclerView.Adapter<YourPlayListAdapter.YourPlayListViewHolder> {


    List<Item> mItemForYou;

    Context mContext;

    private IClickItemListener iClickItemListener;

    public interface IClickItemListener {
        void onClickItem(Item item);
    }


    public YourPlayListAdapter (List<Item> listData, Context context, IClickItemListener iClickItemListener) {
        this.mItemForYou = listData;
        mContext = context;
        this.iClickItemListener = iClickItemListener;

    }


    @NonNull
    @Override
    public YourPlayListAdapter.YourPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YourPlayListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you, null));
    }

    @Override
    public void onBindViewHolder(@NonNull YourPlayListViewHolder holder, int position) {
        if(mItemForYou.size() != 0) {
            Item item = mItemForYou.get(position);
            holder.itemTxt.setText(item.getItemName());
            Glide.with(mContext).load(item.getItemImg()).error(R.drawable.default_avatar).into(holder.itemImg);

            int left = dpToPx(15);
            int top = dpToPx(4);
            int right = dpToPx(15);
            int bottom = dpToPx(4);

            int spanCount = 2;

            boolean isFirst2Item = position < spanCount;
            boolean isLast2Item = position > getItemCount() - spanCount;

            if(isFirst2Item) {
                top = dpToPx(15);

            }

            if(isLast2Item) {
                bottom = dpToPx(50);
            }

            boolean isLeftSide = (position + 1) %  spanCount != 0;
            boolean isRightSide = !isLeftSide;
            if(isLeftSide) {
                right = dpToPx(3);
            }
            if(isRightSide) {
                left = dpToPx(3);
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
    }

    private int dpToPx(int dp) {
        float px = dp * mContext.getResources().getDisplayMetrics().density;
        return (int)px;
    }

    @Override
    public int getItemCount() {
        return Math.min(mItemForYou.size(), 6);
    }

    public class YourPlayListViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        private ImageView itemImg;
        private TextView itemTxt;

        public YourPlayListViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTxt = itemView.findViewById(R.id.item_title);
            itemImg = itemView.findViewById(R.id.forYou_img);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }


}
