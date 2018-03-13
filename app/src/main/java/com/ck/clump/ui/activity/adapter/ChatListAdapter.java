package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.enums.ChatType;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.activity.model.Contact;
import com.ck.clump.ui.activity.view_holder.ChatViewHolder;
import com.ck.clump.ui.widget.swiperecyclerview.SwipeMenuLayout;
import com.ck.clump.ui.widget.swiperecyclerview.listener.SimpleSwipeSwitchListener;
import com.ck.clump.util.Common;

import java.util.Date;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class ChatListAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private final OnItemListener listener;
    private List<Chat> data;
    private Context context;

    public ChatListAdapter(Context context, List<Chat> data, OnItemListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_swipe, null);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, final int position) {
        final Chat chat = data.get(position);
        holder.action(chat, position, listener);
        holder.getTvName().setText(chat.getName());
        if (chat.getDate() != null) {
            holder.getTvDate().setVisibility(View.VISIBLE);
            holder.getTvDate().setText(Common.showDate(chat.getDate()));
        } else {
            holder.getTvDate().setVisibility(View.GONE);
        }

        int placeholder = chat.getType() == Contact.TYPE_GROUP ? R.drawable.default_profile_group : R.drawable.default_profile_user;
        Glide.with(context).load(BuildConfig.BASEURL + chat.getAvatar())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(placeholder)
                        .placeholder(placeholder))
                .apply(overrideOf(100, 100))
                .into(holder.getProfileImage());

        if (chat.getType() == Chat.TYPE_GROUP) {
            holder.getTypeImage().setVisibility(View.VISIBLE);
            if (chat.getMessage() != null) {
                holder.getTvDate().setVisibility(View.VISIBLE);
                holder.getTvDesc1().setVisibility(View.VISIBLE);
                holder.getTvDesc2().setVisibility(View.VISIBLE);
                holder.getTvDesc1().setText(chat.getMessage().getUserName());

                Date messageDate = new Date(chat.getMessage().getTimeSend() * 1000L);
                if (Common.isYesterday(messageDate)) {
                    holder.getTvDate().setText("Yesterday");
                } else if (Common.isToday(messageDate)) {
                    holder.getTvDate().setText(Common.showTime(messageDate));
                } else {
                    holder.getTvDate().setText(Common.showDate(messageDate));
                }

                if (chat.getMessage().getType() == ChatType.TEXT.getValue()) {
                    holder.getTvDesc2().setText(Common.getChatContent(chat.getMessage().getContent()));
                } else if (chat.getMessage().getType() == ChatType.IMAGE.getValue()) {
                    holder.getTvDesc2().setText(context.getString(R.string.has_sent_a_message));
                } else {
                    holder.getTvDesc2().setText(context.getString(R.string.has_sent_a_sticker));
                }
            } else {
                holder.getTvDate().setVisibility(View.GONE);
                holder.getTvDesc1().setVisibility(View.GONE);
                holder.getTvDesc2().setVisibility(View.GONE);
            }
        } else {
            holder.getTypeImage().setVisibility(View.GONE);
            holder.getTvDesc1().setVisibility(View.GONE);
            if (chat.getMessage() != null) {
                holder.getTvDate().setVisibility(View.VISIBLE);
                holder.getTvDesc2().setVisibility(View.VISIBLE);

                Date messageDate = new Date(chat.getMessage().getTimeSend() * 1000L);
                if (Common.isYesterday(messageDate)) {
                    holder.getTvDate().setText("Yesterday");
                } else if (Common.isToday(messageDate)) {
                    holder.getTvDate().setText(Common.showTime(messageDate));
                } else {
                    holder.getTvDate().setText(Common.showDate(messageDate));
                }

                if (chat.getMessage().getType() == ChatType.TEXT.getValue()) {
                    holder.getTvDesc2().setText(Common.getChatContent(chat.getMessage().getContent()).replace("\\\\", "\\"));
                } else if (chat.getMessage().getType() == ChatType.IMAGE.getValue()) {
                    holder.getTvDesc2().setText(context.getString(R.string.has_sent_a_message));
                } else {
                    holder.getTvDesc2().setText(context.getString(R.string.has_sent_a_sticker));
                }
            } else {
                holder.getTvDate().setVisibility(View.GONE);
                holder.getTvDesc2().setVisibility(View.GONE);
            }
        }
        if (chat.getAmount() > 0) {
            holder.getTvAmount().setVisibility(View.VISIBLE);
            holder.getTvAmount().setText(String.valueOf(chat.getAmount()));
            //holder.getTvName().setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        } else {
            holder.getTvAmount().setVisibility(View.GONE);
            //holder.getTvName().setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        }

        holder.getSml().setSwipeEnable(true);
        if (!chat.isOpen()) {
            if (holder.getSml().isMenuOpen()) {
                holder.getSml().smoothCloseMenu();
            }
        }
        holder.getSml().setSwipeListener(new SimpleSwipeSwitchListener() {
            @Override
            public void beginMenuClosed(SwipeMenuLayout swipeMenuLayout) {
                Log.d("MENU", "Begin Closed");
            }

            @Override
            public void beginMenuOpened(SwipeMenuLayout swipeMenuLayout) {
                Log.d("MENU", "Begin Opened");
            }

            @Override
            public void endMenuClosed(SwipeMenuLayout swipeMenuLayout) {
                Log.d("MENU", "End Closed");
                chat.setOpen(false);
            }

            @Override
            public void endMenuOpened(SwipeMenuLayout swipeMenuLayout) {
                Log.d("MENU", "End Opened");
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getId().equals(chat.getId())) {
                        data.get(i).setOpen(true);
                    } else {
                        if (data.get(i).isOpen()) {
                            data.get(i).setOpen(false);
                            notifyItemChanged(i);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemListener {
        void onClick(Chat Item, int position);

        void onDelete(Chat Item, int position);
    }

}
