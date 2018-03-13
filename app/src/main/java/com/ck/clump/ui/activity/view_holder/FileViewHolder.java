package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.adapter.FileAdapter;
import com.ck.clump.ui.activity.model.ImageFile;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.fileThumb)
    ImageView fileThumb;
    @Bind(R.id.imageChecked)
    ImageView imageChecked;

    public FileViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void click(final ImageFile item, final FileAdapter.OnItemClickListener listener, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isSent()) return;
                listener.onClick(item, position);
            }
        });
    }

    public ImageView getFileThumb() {
        return fileThumb;
    }

    public ImageView getImageChecked() {
        return imageChecked;
    }
}