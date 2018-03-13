package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.MediaAdapter;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.widget.RoundRectCornerImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MediaViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.avatarImage)
    RoundRectCornerImageView avatarImage;

    public MediaViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void click(final Media item, final MediaAdapter.OnItemClickListener listener, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item, position);
            }
        });
    }

    public RoundRectCornerImageView getAvatarImage() {
        return avatarImage;
    }
    
}