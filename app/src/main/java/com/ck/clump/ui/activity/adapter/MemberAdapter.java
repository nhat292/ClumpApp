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
import com.ck.clump.enums.EventUserStatus;
import com.ck.clump.ui.activity.model.Member;
import com.ck.clump.ui.activity.view_holder.MemberViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder> {

    private final MemberAdapter.OnItemClickListener listener;
    private List<Member> data;
    private Context context;

    public MemberAdapter(Context context, List<Member> data, MemberAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        Member item = data.get(position);
        holder.click(item, listener);
        if (position == 0) {
            holder.getTvName().setText(context.getString(R.string.add));
            holder.getAvatarImage().setImageResource(R.drawable.add);
            holder.getJoin().setVisibility(View.GONE);
        } else {
            holder.getJoin().setVisibility(View.VISIBLE);
            if (item.getEventConfirm() != null) {
                if (item.getEventConfirm().equals(EventUserStatus.COUNT_IN.getValue())) {
                    holder.getJoin().setImageResource(R.drawable.badge_check);
                } else if (item.getEventConfirm().equals(EventUserStatus.COUNT_OUT.getValue())) {
                    holder.getJoin().setImageResource(R.drawable.badge_out);
                } else {
                    holder.getJoin().setImageResource(R.drawable.badge_check_grey);
                }
            } else {
                holder.getJoin().setImageResource(R.drawable.badge_check_grey);
            }
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
        void onClick(Member Item);
    }

}
