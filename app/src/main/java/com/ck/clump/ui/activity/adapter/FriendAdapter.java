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
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.activity.view_holder.FriendViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private final FriendAdapter.OnItemClickListener listener;
    private List<Friend> data;
    private Context context;

    public FriendAdapter(Context context, List<Friend> data, FriendAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend item = data.get(position);
        holder.click(item, listener);
        if (position == 0) {
            holder.getTvName().setText(context.getString(R.string.add));
            holder.getAvatarImage().setImageResource(R.drawable.add);
        } else {
            holder.getTvName().setText(item.getDisplayName());
            Glide.with(context).load(BuildConfig.BASEURL + item.getAvatarPath())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .error(R.drawable.default_profile_user)
                            .placeholder(R.drawable.default_profile_user))
                    .apply(overrideOf(100, 100))
                    .into(holder.getAvatarImage());

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
