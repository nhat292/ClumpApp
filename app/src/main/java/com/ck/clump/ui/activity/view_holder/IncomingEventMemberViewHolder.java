package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IncomingEventMemberViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.avatarImage)
    ImageView avatarImage;
    @Bind(R.id.txtAddition)
    TextView txtAddition;
    @Bind(R.id.viewAddition)
    View viewAddition;

    public IncomingEventMemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        txtAddition.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public ImageView getAvatarImage() {
        return avatarImage;
    }

    public TextView getTxtAddition() {
        return txtAddition;
    }

    public View getViewAddition() {
        return viewAddition;
    }
}