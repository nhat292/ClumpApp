package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.FriendAdapter;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.name)
    TextView tvName;

    public FriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final Friend item, final FriendAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item);
            }
        });
    }

    public CircleImageView getAvatarImage() {
        return avatarImage;
    }

    public TextView getTvName() {
        return tvName;
    }

}