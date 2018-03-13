package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.ui.activity.view_holder.EmojiHolder;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiHolder> {

    private final OnItemClickListener listener;
    private String[] data;
    private Context context;

    public EmojiAdapter(Context context, String[] data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public EmojiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new EmojiHolder(view);
    }

    @Override
    public void onBindViewHolder(EmojiHolder holder, int position) {
        String emoji = data[position];
        holder.getTvEmoji().setText(emoji);
        holder.click(emoji, listener);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public interface OnItemClickListener {
        void onClick(String Item);
    }

}
