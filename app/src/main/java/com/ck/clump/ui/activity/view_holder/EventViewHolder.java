package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.EventAdapter;
import com.ck.clump.ui.activity.model.Event;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EventViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.rootContentItem)
    RelativeLayout rootContentItem;
    @Bind(R.id.profileImage)
    CircleImageView profileImage;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvDesc)
    TextView tvDesc;
    @Bind(R.id.contentTag)
    RelativeLayout contentTag;
    @Bind(R.id.imvTag)
    ImageView imvTag;
    @Bind(R.id.tvTag)
    TextView tvTag;

    public EventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDesc.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvTag.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final Event item, final EventAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item);
            }
        });
    }

    public RelativeLayout getRootContentItem() {
        return rootContentItem;
    }

    public CircleImageView getProfileImage() {
        return profileImage;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvDesc() {
        return tvDesc;
    }

    public RelativeLayout getContentTag() {
        return contentTag;
    }

    public ImageView getImvTag() {
        return imvTag;
    }

    public TextView getTvTag() {
        return tvTag;
    }
}