package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.AuditAdapter;
import com.ck.clump.ui.activity.model.Audit;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AuditViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.profileImage)
    CircleImageView profileImage;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvDate)
    TextView tvDate;
    @Bind(R.id.line)
    View line;

    public AuditViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDate.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final Audit item, final AuditAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item);
            }
        });
    }

    public CircleImageView getProfileImage() {
        return profileImage;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvDate() {
        return tvDate;
    }

    public View getLine() {
        return line;
    }
}