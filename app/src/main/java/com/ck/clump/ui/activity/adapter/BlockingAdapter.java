package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.model.response.BlockingResponse;
import com.ck.clump.ui.activity.view_holder.BlockingHolder;

import java.util.List;

public class BlockingAdapter extends RecyclerView.Adapter<BlockingHolder> {

    private final OnItemClickListener listener;
    private List<BlockingResponse.BlockingUser> data;
    private Context context;

    public BlockingAdapter(Context context, List<BlockingResponse.BlockingUser> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public BlockingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocking, null);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new BlockingHolder(view);
    }

    @Override
    public void onBindViewHolder(BlockingHolder holder, int position) {
        BlockingResponse.BlockingUser user = data.get(position);
        holder.getTxtName().setText(user.getDisplayName());
        holder.click(user, listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onDeleteClick(BlockingResponse.BlockingUser Item);
    }

}
