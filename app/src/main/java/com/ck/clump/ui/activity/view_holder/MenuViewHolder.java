package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.name)
    TextView tvName;
    @Bind(R.id.line)
    View line;

    public MenuViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final String item, final MenuAdapter.OnItemClickListener listener, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item, position);
            }
        });
    }

    public TextView getTvName() {
        return tvName;
    }

    public View getLine() {
        return line;
    }
}