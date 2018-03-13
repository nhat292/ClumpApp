package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.ImageFile;
import com.ck.clump.ui.activity.view_holder.FileViewHolder;

import java.io.File;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {

    private final FileAdapter.OnItemClickListener listener;
    private List<ImageFile> data;
    private Context context;

    public FileAdapter(Context context, List<ImageFile> data, FileAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        ImageFile item = data.get(position);
        holder.click(item, listener, position);
        Uri uri = Uri.fromFile(new File(item.getPath()));
        Glide.with(context).load(uri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.ic_image_default)
                        .placeholder(R.drawable.ic_image_default))
                .apply(overrideOf(200, 200))
                .into(holder.getFileThumb());
        if (item.isSent()) {
            holder.getImageChecked().setVisibility(View.VISIBLE);
        } else {
            holder.getImageChecked().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(ImageFile Item, int position);
    }

}
