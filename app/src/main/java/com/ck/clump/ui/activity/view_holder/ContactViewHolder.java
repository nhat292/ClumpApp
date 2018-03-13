package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.StickyContractHeadersAdapter;
import com.ck.clump.ui.activity.model.Contact;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.profileImage)
    CircleImageView profileImage;
    @Bind(R.id.typeImage)
    CircleImageView typeImage;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.imvInfo)
    ImageView imvInfo;

    public ContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final Contact item, final StickyContractHeadersAdapter.OnItemClickListener listener) {
        if (listener == null) return;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item, true);
            }
        });
    }

    public void itemClick(final Contact item, final StickyContractHeadersAdapter.OnItemClickListener listener) {
        if (listener == null) return;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item, true);
            }
        });
    }


    public CircleImageView getProfileImage() {
        return profileImage;
    }

    public CircleImageView getTypeImage() {
        return typeImage;
    }

    public TextView getTvName() {
        return tvName;
    }

    public ImageView getImvInfo() {
        return imvInfo;
    }
}