package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.MemberAdapter;
import com.ck.clump.ui.activity.model.Member;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.name)
    TextView tvName;
    @Bind(R.id.join)
    ImageView join;

    public MemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final Member item, final MemberAdapter.OnItemClickListener listener) {
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

    public ImageView getJoin() {
        return join;
    }
}