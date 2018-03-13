package com.ck.clump.ui.activity.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.enums.ChatType;
import com.ck.clump.ui.activity.adapter.ChatAdapter;
import com.ck.clump.ui.activity.model.ChatMessage;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatContentViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.llLeft)
    LinearLayout llLeft;
    @Bind(R.id.avatarLeft)
    CircleImageView avatarLeft;
    @Bind(R.id.tvNameLeft)
    TextView tvNameLeft;
    @Bind(R.id.tvContentLeft)
    TextView tvContentLeft;
    @Bind(R.id.imgStickerLeft)
    ImageView imgStickerLeft;
    @Bind(R.id.imgFileLeft)
    ImageView imgFileLeft;
    @Bind(R.id.tvStatusLeft)
    TextView tvStatusLeft;
    @Bind(R.id.tvTimeLeft)
    TextView tvTimeLeft;
    @Bind(R.id.llTextContentLeft)
    LinearLayout llTextContentLeft;

    @Bind(R.id.llRight)
    LinearLayout llRight;
    @Bind(R.id.avatarRight)
    CircleImageView avatarRight;
    @Bind(R.id.tvNameRight)
    TextView tvNameRight;
    @Bind(R.id.tvContentRight)
    TextView tvContentRight;
    @Bind(R.id.imgStickerRight)
    ImageView imgStickerRight;
    @Bind(R.id.imgFileRight)
    ImageView imgFileRight;
    @Bind(R.id.tvStatusRight)
    TextView tvStatusRight;
    @Bind(R.id.tvTimeRight)
    TextView tvTimeRight;
    @Bind(R.id.llTextContentRight)
    LinearLayout llTextContentRight;
    @Bind(R.id.txtToday)
    TextView txtToday;

    public ChatContentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        tvNameLeft.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvNameRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvContentLeft.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvContentRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvStatusLeft.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvStatusRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvTimeLeft.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_ITALIC));
        tvTimeRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_ITALIC));
        txtToday.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    public void click(final ChatMessage item, final ChatAdapter.OnItemClickListener listener) {
        if(item.getType() == ChatType.IMAGE.getValue()) {
            if(item.isLeft()) {
                setClick(imgFileLeft, item, listener);
            } else {
                setClick(imgFileRight, item, listener);
            }
        }
    }

    public void longClick(final ChatMessage item, final int position, final ChatAdapter.OnItemClickListener listener) {
        if (!item.getChannelId().equalsIgnoreCase("")) {
            if (!item.isLeft()) {
                if (item.getType() == ChatType.TEXT.getValue()) {
                    setLongClick(tvContentRight, item, position, listener);
                } else if (item.getType() == ChatType.STICKER.getValue()) {
                    setLongClick(imgStickerRight, item, position, listener);
                } else {
                    setLongClick(imgFileRight, item, position, listener);
                }
            } else {
                if (item.getType() == ChatType.TEXT.getValue()) {
                    setLongClick(tvContentLeft, item, position, listener);
                }
            }
        }
    }

    private void setClick(View view, final ChatMessage item, final ChatAdapter.OnItemClickListener listener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(item);
                }
            }
        });
    }

    private void setLongClick(View view, final ChatMessage item, final int position, final ChatAdapter.OnItemClickListener listener) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongClick(item, position);
                }
                return true;
            }
        });
    }

    public LinearLayout getLlLeft() {
        return llLeft;
    }

    public CircleImageView getAvatarLeft() {
        return avatarLeft;
    }

    public TextView getTvNameLeft() {
        return tvNameLeft;
    }

    public TextView getTvContentLeft() {
        return tvContentLeft;
    }

    public ImageView getImgStickerLeft() {
        return imgStickerLeft;
    }

    public ImageView getImgFileLeft() {
        return imgFileLeft;
    }

    public TextView getTvStatusLeft() {
        return tvStatusLeft;
    }

    public TextView getTvTimeLeft() {
        return tvTimeLeft;
    }

    public LinearLayout getLlRight() {
        return llRight;
    }

    public CircleImageView getAvatarRight() {
        return avatarRight;
    }

    public TextView getTvNameRight() {
        return tvNameRight;
    }

    public TextView getTvContentRight() {
        return tvContentRight;
    }

    public ImageView getImgStickerRight() {
        return imgStickerRight;
    }

    public ImageView getImgFileRight() {
        return imgFileRight;
    }

    public TextView getTvStatusRight() {
        return tvStatusRight;
    }

    public TextView getTvTimeRight() {
        return tvTimeRight;
    }

    public LinearLayout getLlTextContentRight() {
        return llTextContentRight;
    }

    public LinearLayout getLlTextContentLeft() {
        return llTextContentLeft;
    }

    public TextView getTxtToday() {
        return txtToday;
    }

    public void setTxtToday(TextView txtToday) {
        this.txtToday = txtToday;
    }
}