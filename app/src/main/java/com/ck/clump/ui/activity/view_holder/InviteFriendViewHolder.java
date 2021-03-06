package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.InviteFriendAdapter;
import com.ck.clump.ui.activity.model.InviteFriend;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InviteFriendViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.name)
    TextView tvName;
    @Bind(R.id.checked)
    ImageView imChecked;
    @Bind(R.id.line)
    View line;

    public InviteFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final InviteFriend item, final InviteFriendAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item);
            }
        });
    }

    public TextView getTvName() {
        return tvName;
    }

    public ImageView getImChecked() {
        return imChecked;
    }

    public View getLine() {
        return line;
    }
}