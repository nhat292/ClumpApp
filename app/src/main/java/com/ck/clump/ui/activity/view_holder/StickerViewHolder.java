package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.StickerAdapter;
import com.ck.clump.ui.activity.model.Sticker;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StickerViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.sticker)
    ImageView sticker;

    public StickerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void click(final Sticker item, final StickerAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item);
            }
        });
    }

    public ImageView getSticker() {
        return sticker;
    }
}