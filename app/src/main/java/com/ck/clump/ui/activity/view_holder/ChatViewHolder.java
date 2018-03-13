package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.ChatListAdapter;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.widget.CircularTextView;
import com.ck.clump.ui.widget.swiperecyclerview.SwipeHorizontalMenuLayout;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.profileImage)
    CircleImageView profileImage;
    @Bind(R.id.typeImage)
    CircleImageView typeImage;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvDesc1)
    TextView tvDesc1;
    @Bind(R.id.tvDesc2)
    TextView tvDesc2;
    @Bind(R.id.tvDate)
    TextView tvDate;
    @Bind(R.id.tvAmount)
    CircularTextView tvAmount;
    @Bind(R.id.sml)
    SwipeHorizontalMenuLayout sml;
    @Bind(R.id.btnDelete)
    Button btnDelete;
    @Bind(R.id.smContentView)
    RelativeLayout smContentView;

    public ChatViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvDesc1.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDesc2.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDate.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvAmount.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnDelete.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
    }

    public void action(final Chat item, final int position, final ChatListAdapter.OnItemListener listener) {
        smContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item, position);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(item, position);
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

    public TextView getTvDesc1() {
        return tvDesc1;
    }

    public TextView getTvDesc2() {
        return tvDesc2;
    }

    public TextView getTvDate() {
        return tvDate;
    }

    public CircularTextView getTvAmount() {
        return tvAmount;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

    public SwipeHorizontalMenuLayout getSml() {
        return sml;
    }
}