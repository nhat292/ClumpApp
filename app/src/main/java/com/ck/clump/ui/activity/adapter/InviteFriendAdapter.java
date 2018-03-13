package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.ui.activity.model.InviteFriend;
import com.ck.clump.ui.activity.view_holder.InviteFriendViewHolder;

import java.util.List;

public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendViewHolder> {

    private final InviteFriendAdapter.OnItemClickListener listener;
    private List<InviteFriend> data;
    private Context context;

    public InviteFriendAdapter(Context context, List<InviteFriend> data, InviteFriendAdapter.OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public InviteFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_friend, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new InviteFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InviteFriendViewHolder holder, int position) {
        InviteFriend item = data.get(position);
        holder.click(item, listener);
        holder.getTvName().setText(item.getName());
        switch (item.getType()) {
            case InviteFriend.TYPE_UNSELECTED:
                holder.getImChecked().setImageResource(R.drawable.badge_check_grey);
                break;
            case InviteFriend.TYPE_SELECTED:
                holder.getImChecked().setImageResource(R.drawable.badge_check);
                break;
            case InviteFriend.TYPE_CLUMP:
                holder.getImChecked().setImageResource(R.drawable.clump_icon);
                break;
        }
        if (position == data.size() - 1) {
            holder.getLine().setVisibility(View.GONE);
        } else {
            holder.getLine().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(InviteFriend Item);
    }

}
