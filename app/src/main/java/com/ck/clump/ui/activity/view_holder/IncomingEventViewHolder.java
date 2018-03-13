package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.model.EventModel;
import com.ck.clump.ui.activity.adapter.IncomingEventAdapter;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IncomingEventViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.imvBackground)
    ImageView imvBackground;
    @Bind(R.id.tvDateTime)
    TextView tvDateTime;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.rvMembers)
    RecyclerView rvMembers;
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.imvTag)
    ImageView imvTag;
    @Bind(R.id.tvAttending)
    TextView tvAttending;

    public IncomingEventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvDateTime.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvStatus.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvAttending.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final EventModel Item, final IncomingEventAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(Item);
            }
        });
    }

    public ImageView getImvBackground() {
        return imvBackground;
    }

    public TextView getTvDateTime() {
        return tvDateTime;
    }

    public TextView getTvName() {
        return tvName;
    }

    public RecyclerView getRvMembers() {
        return rvMembers;
    }

    public TextView getTvStatus() {
        return tvStatus;
    }

    public ImageView getImvTag() {
        return imvTag;
    }
}