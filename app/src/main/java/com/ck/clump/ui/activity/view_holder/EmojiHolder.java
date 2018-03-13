package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.EmojiAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmojiHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tvEmoji)
    TextView tvEmoji;

    public EmojiHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void click(final String item, final EmojiAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(item);
            }
        });
    }

    public TextView getTvEmoji() {
        return tvEmoji;
    }
}