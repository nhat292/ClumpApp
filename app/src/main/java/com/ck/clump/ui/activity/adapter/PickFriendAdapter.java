package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.activity.view_holder.PickFriendViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class PickFriendAdapter extends RecyclerView.Adapter<PickFriendViewHolder> {

    private final PickFriendAdapter.OnItemClickListener listener;
    private List<Friend> data;
    private Context context;

    public PickFriendAdapter(Context context, List<Friend> data, PickFriendAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public PickFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick_friend, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new PickFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PickFriendViewHolder holder, int position) {
        Friend item = data.get(position);
        holder.click(item, listener);
        holder.getTvName().setText(item.getDisplayName());
        Glide.with(context).load(item.getAvatarPath())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.default_profile_user)
                        .placeholder(R.drawable.default_profile_user))
                .apply(overrideOf(100, 100))
                .into(holder.getAvatarImage());
        if (item.isChecked()) {
            holder.getImChecked().setImageResource(R.drawable.badge_check);
        } else {
            holder.getImChecked().setImageResource(R.drawable.badge_check_grey);
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
        void onClick(Friend Item);
    }

}
