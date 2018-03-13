package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactHeaderViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tvHeader)
    TextView tvHeader;

    public ContactHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
    }

    public TextView getTvHeader() {
        return tvHeader;
    }

}