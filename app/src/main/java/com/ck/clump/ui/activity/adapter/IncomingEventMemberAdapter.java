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
import com.ck.clump.ui.activity.model.Member;
import com.ck.clump.ui.activity.view_holder.IncomingEventMemberViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class IncomingEventMemberAdapter extends RecyclerView.Adapter<IncomingEventMemberViewHolder> {

    private List<Member> data;
    private Context context;

    public IncomingEventMemberAdapter(Context context, List<Member> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public IncomingEventMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incoming_event_member, null);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new IncomingEventMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomingEventMemberViewHolder holder, int position) {
        Member member = data.get(position);
        Glide.with(context).load(BuildConfig.BASEURL + member.getAvatarPath())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.default_profile_user)
                        .placeholder(R.drawable.default_profile_user))
                .apply(overrideOf(100, 100))
                .into(holder.getAvatarImage());

        if (member.getPlusCount() != 0) {
            holder.getViewAddition().setVisibility(View.VISIBLE);
            holder.getTxtAddition().setVisibility(View.VISIBLE);
            holder.getTxtAddition().setText("+" + member.getPlusCount());
        } else {
            holder.getViewAddition().setVisibility(View.GONE);
            holder.getTxtAddition().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
