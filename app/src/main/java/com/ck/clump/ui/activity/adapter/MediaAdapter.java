package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.activity.view_holder.MediaViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class MediaAdapter extends RecyclerView.Adapter<MediaViewHolder> {

    private final MediaAdapter.OnItemClickListener listener;
    private List<Media> data;
    private Context context;

    public MediaAdapter(Context context, List<Media> data, MediaAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        Media item = data.get(position);
        holder.click(item, listener, position);
        Glide.with(context).load(BuildConfig.BASEURL + item.getPath())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.ic_image_default)
                        .placeholder(R.drawable.ic_image_default))
                .apply(overrideOf(200, 200))
                .into(holder.getAvatarImage());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(Media Item, int position);
    }

}
