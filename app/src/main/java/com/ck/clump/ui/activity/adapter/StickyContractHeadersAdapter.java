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
import com.ck.clump.ui.activity.model.Contact;
import com.ck.clump.ui.activity.view_holder.ContactHeaderViewHolder;
import com.ck.clump.ui.activity.view_holder.ContactViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class StickyContractHeadersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    public static final int TYPE_CONTACT = 0;
    public static final int TYPE_INVITE_FRIENDS = 1;

    private Context context;
    private List<Contact> data;
    private final OnItemClickListener listener;
    private boolean isPick;
    private int type = TYPE_CONTACT;

    public StickyContractHeadersAdapter(Context context, List<Contact> data, OnItemClickListener listener, boolean isPick) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.isPick = isPick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
        Contact contact = data.get(position);
        contactViewHolder.getTvName().setText(contact.getName());

        int placeholder = contact.getType() == Contact.TYPE_GROUP ? R.drawable.default_profile_group : R.drawable.default_profile_user;

        Glide.with(context).load(BuildConfig.BASEURL + contact.getAvatar())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(placeholder)
                        .placeholder(placeholder))
                .apply(overrideOf(100, 100))
                .into(contactViewHolder.getProfileImage());

        if (type == TYPE_INVITE_FRIENDS) {
            contactViewHolder.getTypeImage().setVisibility(View.GONE);
            contactViewHolder.itemClick(contact, listener);
        } else {
            contactViewHolder.click(contact, listener);
            if (contact.getType() == Contact.TYPE_GROUP) {
                contactViewHolder.getTypeImage().setVisibility(View.VISIBLE);
            } else {
                contactViewHolder.getTypeImage().setVisibility(View.GONE);
            }
        }
        if (isPick) {
            if (contact.isPick()) {
                contactViewHolder.getImvInfo().setImageResource(R.drawable.badge_check);
            } else {
                contactViewHolder.getImvInfo().setImageResource(R.drawable.badge_check_grey);
            }
        } else {
            contactViewHolder.getImvInfo().setImageResource(R.drawable.badge_info);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getHeaderId(int position) {
        return data.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_header, parent, false);
        return new ContactHeaderViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContactHeaderViewHolder viewHolder = (ContactHeaderViewHolder) holder;
        Contact contact = data.get(position);
        if (contact.getType() == Contact.TYPE_GROUP) {
            viewHolder.getTvHeader().setText(context.getString(R.string.groups));
        } else {
            viewHolder.getTvHeader().setText(context.getString(R.string.friends));
        }
    }

    public interface OnItemClickListener {
        void onClick(Contact Item, boolean isInfoClick);
    }

    public void setType(int type) {
        this.type = type;
    }
}
