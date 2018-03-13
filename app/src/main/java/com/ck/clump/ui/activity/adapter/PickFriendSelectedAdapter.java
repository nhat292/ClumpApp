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
import com.ck.clump.ui.activity.view_holder.PickFriendSelectedViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class PickFriendSelectedAdapter extends RecyclerView.Adapter<PickFriendSelectedViewHolder> {

    private final PickFriendSelectedAdapter.OnItemClickListener listener;
    private List<Friend> data;
    private Context context;

    public PickFriendSelectedAdapter(Context context, List<Friend> data, PickFriendSelectedAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public PickFriendSelectedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick_friend_selected, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new PickFriendSelectedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PickFriendSelectedViewHolder holder, int position) {
        Friend item = data.get(position);
        holder.click(item, listener);
        holder.getTvName().setText(item.getDisplayName());
        Glide.with(context).load(item.getAvatarPath())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.ic_image_default)
                        .placeholder(R.drawable.ic_image_default))
                .apply(overrideOf(100, 100))
                .into(holder.getAvatarImage());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(Friend Item);
    }

}
