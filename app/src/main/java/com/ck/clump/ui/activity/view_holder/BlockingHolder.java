package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.response.BlockingResponse;
import com.ck.clump.ui.activity.adapter.BlockingAdapter;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlockingHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.imgDelete)
    ImageView imgDelete;

    public BlockingHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        txtName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final BlockingResponse.BlockingUser item, final BlockingAdapter.OnItemClickListener listener) {
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(item);
            }
        });
    }

    public TextView getTxtName() {
        return txtName;
    }

    public ImageView getImgDelete() {
        return imgDelete;
    }
}