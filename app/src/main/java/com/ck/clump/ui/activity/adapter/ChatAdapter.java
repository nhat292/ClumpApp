package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.enums.ChatType;
import com.ck.clump.ui.activity.model.ChatMessage;
import com.ck.clump.ui.activity.view_holder.ChatContentViewHolder;
import com.ck.clump.util.Common;

import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.overrideOf;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnItemClickListener listener;
    private List<ChatMessage> data;
    private Context context;
    private boolean enableLazyLoad = true;
    private boolean isLoading = false;
    private boolean haveTodayAlready = false;
    private boolean isGroup;

    public ChatAdapter(Context context, List<ChatMessage> data, OnItemClickListener listener, boolean isGroup) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.isGroup = isGroup;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(layoutParams);
        return new ChatContentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = data.get(position);
        ChatContentViewHolder contentHolder = (ChatContentViewHolder) holder;
        contentHolder.click(message, listener);
        contentHolder.longClick(message, position, listener);
        int type = getItemViewType(position);
        if (message.isLeft()) {
            if (message.getUserAvatarUrl() != null && !message.getUserAvatarUrl().isEmpty()) {
                Glide.with(context).load(BuildConfig.BASEURL + message.getUserAvatarUrl())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .error(R.drawable.default_profile_user)
                                .placeholder(R.drawable.default_profile_user))
                        .apply(overrideOf(100, 100))
                        .into(contentHolder.getAvatarLeft());

            }
            contentHolder.getTvTimeLeft().setText(Common.showTime(new Date(message.getTimeSend() * 1000L)));
            //contentHolder.getTvStatusLeft().setText(Common.showDate(new Date(message.getTimeSend() * 1000L)));
            contentHolder.getTvNameLeft().setText(message.getUserName());
            if (type == ChatType.TEXT.getValue()) {
                setupView(message.getChannelId(), true, false, contentHolder, type);
                String text = Common.getChatContent(message.getContent());
                contentHolder.getTvContentLeft().setText(text);
            } else if (type == ChatType.STICKER.getValue()) {
                setupView(message.getChannelId(), true, false, contentHolder, type);
                int resID = context.getResources().getIdentifier(message.getContent(), "drawable", context.getPackageName());
                contentHolder.getImgStickerLeft().setImageResource(resID);
            } else {
                setupView(message.getChannelId(), true, false, contentHolder, type);
                Glide.with(context).load(BuildConfig.BASEURL + message.getContent())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .error(R.drawable.ic_image_default)
                                .placeholder(R.drawable.ic_image_default))
                        .apply(overrideOf(400, 400))
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(50, 0)))
                        .into(contentHolder.getImgFileLeft());
            }
        } else {
            contentHolder.getTvTimeRight().setText(Common.showTime(new Date(message.getTimeSend() * 1000L)));
            if (!isGroup) {
                if (position == 0) {
                    contentHolder.getTvStatusRight().setVisibility(View.VISIBLE);
                    if (message.isRead()) {
                        contentHolder.getTvStatusRight().setText(context.getString(R.string.read));
                    } else {
                        contentHolder.getTvStatusRight().setText("");
                    }
                } else {
                    contentHolder.getTvStatusRight().setVisibility(View.GONE);
                }
            }

            if (type == ChatType.TEXT.getValue()) {
                setupView(message.getChannelId(), false, false, contentHolder, type);
                String text = Common.getChatContent(message.getContent());
                contentHolder.getTvContentRight().setText(text);
            } else if (type == ChatType.STICKER.getValue()) {
                setupView(message.getChannelId(), false, false, contentHolder, type);
                int resIDR = context.getResources().getIdentifier(message.getContent(), "drawable", context.getPackageName());
                contentHolder.getImgStickerRight().setImageResource(resIDR);
            } else {
                setupView(message.getChannelId(), false, false, contentHolder, type);
                Glide.with(context).load(BuildConfig.BASEURL + message.getContent())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .error(R.drawable.ic_image_default)
                                .placeholder(R.drawable.ic_image_default))
                        .apply(overrideOf(400, 400))
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(50, 0)))
                        .into(contentHolder.getImgFileRight());

            }
        }

        if (position == data.size() - 1 && enableLazyLoad && !isLoading) {
            isLoading = true;
            listener.onLazyLoad(data.get(position));
        }

    }

    private void setupView(String channelID, boolean isLeft, boolean isGroup, ChatContentViewHolder contentHolder, int type) {
        if (channelID.equalsIgnoreCase("")) {
            contentHolder.getLlLeft().setVisibility(View.GONE);
            contentHolder.getLlRight().setVisibility(View.GONE);
            contentHolder.getTxtToday().setVisibility(View.VISIBLE);
        } else {
            contentHolder.getTxtToday().setVisibility(View.GONE);
            if (isLeft) {
                contentHolder.getLlRight().setVisibility(View.GONE);
                contentHolder.getLlLeft().setVisibility(View.VISIBLE);
                //contentHolder.getTvStatusLeft().setVisibility(View.VISIBLE);
                contentHolder.getAvatarLeft().setVisibility(View.VISIBLE);
                contentHolder.getTvNameLeft().setVisibility(View.VISIBLE);
                if (type == ChatType.TEXT.getValue()) {
                    contentHolder.getLlTextContentLeft().setVisibility(View.VISIBLE);
                    contentHolder.getImgStickerLeft().setVisibility(View.GONE);
                    contentHolder.getImgFileLeft().setVisibility(View.GONE);
                } else if (type == ChatType.STICKER.getValue()) {
                    contentHolder.getLlTextContentLeft().setVisibility(View.GONE);
                    contentHolder.getImgStickerLeft().setVisibility(View.VISIBLE);
                    contentHolder.getImgFileLeft().setVisibility(View.GONE);
                } else {
                    contentHolder.getLlTextContentLeft().setVisibility(View.GONE);
                    contentHolder.getImgStickerLeft().setVisibility(View.GONE);
                    contentHolder.getImgFileLeft().setVisibility(View.VISIBLE);
                }
            } else {
                contentHolder.getLlLeft().setVisibility(View.GONE);
                contentHolder.getLlRight().setVisibility(View.VISIBLE);
                if (isGroup) {
                    contentHolder.getAvatarRight().setVisibility(View.VISIBLE);
                    contentHolder.getTvNameRight().setVisibility(View.VISIBLE);
                } else {
                    contentHolder.getAvatarRight().setVisibility(View.GONE);
                    contentHolder.getTvNameRight().setVisibility(View.GONE);
                }
                if (type == ChatType.TEXT.getValue()) {
                    contentHolder.getLlTextContentRight().setVisibility(View.VISIBLE);
                    contentHolder.getImgStickerRight().setVisibility(View.GONE);
                    contentHolder.getImgFileRight().setVisibility(View.GONE);
                } else if (type == ChatType.STICKER.getValue()) {
                    contentHolder.getLlTextContentRight().setVisibility(View.GONE);
                    contentHolder.getImgStickerRight().setVisibility(View.VISIBLE);
                    contentHolder.getImgFileRight().setVisibility(View.GONE);
                } else {
                    contentHolder.getLlTextContentRight().setVisibility(View.GONE);
                    contentHolder.getImgStickerRight().setVisibility(View.GONE);
                    contentHolder.getImgFileRight().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setEnableLazyLoad(boolean enableLazyLoad) {
        this.enableLazyLoad = enableLazyLoad;
    }

    public interface OnItemClickListener {
        void onClick(ChatMessage Item);

        void onLazyLoad(ChatMessage Item);

        void onLongClick(ChatMessage Item, int position);
    }

    public boolean isHaveTodayAlready() {
        return haveTodayAlready;
    }

    public void setHaveTodayAlready(boolean haveTodayAlready) {
        this.haveTodayAlready = haveTodayAlready;
    }
}
