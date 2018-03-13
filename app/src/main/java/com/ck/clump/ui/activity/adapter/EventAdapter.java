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
import com.ck.clump.enums.EventStatus;
import com.ck.clump.enums.EventUserStatus;
import com.ck.clump.ui.activity.model.Event;
import com.ck.clump.ui.activity.model.ObjectItem;
import com.ck.clump.ui.activity.view_holder.EventHeaderViewHolder;
import com.ck.clump.ui.activity.view_holder.EventViewHolder;
import com.ck.clump.util.Common;

import java.util.Date;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0x01;
    public static final int TYPE_ITEM = 0x02;

    private final OnItemClickListener listener;
    private List<ObjectItem> data;
    private Context context;

    public EventAdapter(Context context, List<ObjectItem> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_header, null);
            return new EventHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, null);
            return new EventViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object objectData = data.get(position).getObject();
        if (holder instanceof EventHeaderViewHolder) {
            EventHeaderViewHolder eventHeaderViewHolder = (EventHeaderViewHolder) holder;
            if (objectData instanceof String) {
                eventHeaderViewHolder.getTvHeader().setText((String) objectData);
            }
        } else if (holder instanceof EventViewHolder) {
            EventViewHolder eventViewHolder = (EventViewHolder) holder;
            if (objectData instanceof Event) {
                Event event = (Event) objectData;
                eventViewHolder.click(event, listener);
                eventViewHolder.getTvDesc().setText(event.getName());
                if (event.getStartTime() > 0) {
                    eventViewHolder.getTvName().setText(Common.showDateTime(new Date(event.getStartTime())));
                } else {
                    eventViewHolder.getTvName().setText(Common.showDateTime(new Date(event.getCreatedAt())));
                }
                String imagePath = event.getCreator().getAvatarPath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    Glide.with(context).load(BuildConfig.BASEURL + imagePath)
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .error(R.drawable.default_profile_user)
                                    .placeholder(R.drawable.default_profile_user))
                            .apply(overrideOf(100, 100))
                            .into(eventViewHolder.getProfileImage());
                }
                if (event.getLoginUserStatus().equals(EventUserStatus.COUNT_IN.getValue())
                        || event.getLoginUserStatus().equals(EventUserStatus.OWNER.getValue())) {
                    eventViewHolder.getContentTag().setVisibility(View.VISIBLE);
                    eventViewHolder.getImvTag().setImageResource(R.drawable.ribbon_in);
                    eventViewHolder.getTvTag().setText(EventUserStatus.COUNT_IN.getDisplay());
                    eventViewHolder.getRootContentItem().setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundGray));
                } else if (event.getLoginUserStatus().equals(EventUserStatus.COUNT_OUT.getValue())) {
                    eventViewHolder.getContentTag().setVisibility(View.VISIBLE);
                    eventViewHolder.getImvTag().setImageResource(R.drawable.ribbon_out);
                    eventViewHolder.getTvTag().setText(EventUserStatus.COUNT_OUT.getDisplay());
                    eventViewHolder.getRootContentItem().setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundGray));
                } else {
                    eventViewHolder.getContentTag().setVisibility(View.VISIBLE);
                    eventViewHolder.getImvTag().setImageResource(R.drawable.ribbon_rsvp);
                    eventViewHolder.getTvTag().setText(EventUserStatus.NOT_YET.getDisplay());
                    eventViewHolder.getRootContentItem().setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundGray));
                }

                if (event.getStatus().equals(EventStatus.PAST.getValue())) {
                    eventViewHolder.getImvTag().setVisibility(View.INVISIBLE);
                    eventViewHolder.getTvTag().setVisibility(View.INVISIBLE);
                } else {
                    eventViewHolder.getImvTag().setVisibility(View.VISIBLE);
                    eventViewHolder.getTvTag().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(Event Item);
    }

}
