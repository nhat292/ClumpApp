package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.ui.activity.model.Sticker;
import com.ck.clump.ui.activity.view_holder.StickerViewHolder;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerViewHolder> {

    private final StickerAdapter.OnItemClickListener listener;
    private List<Sticker> data;
    private Context context;

    public StickerAdapter(Context context, List<Sticker> data, StickerAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickerViewHolder holder, int position) {
        Sticker item = data.get(position);
        holder.click(item, listener);
        String mDrawableName = item.getDrawableName();
        int resID = context.getResources().getIdentifier(mDrawableName, "drawable", context.getPackageName());
        holder.getSticker().setImageResource(resID);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(Sticker Item);
    }

}
