package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.enums.EventStatus;
import com.ck.clump.enums.EventUserStatus;
import com.ck.clump.ui.activity.model.EventModel;
import com.ck.clump.ui.activity.model.Member;
import com.ck.clump.ui.activity.view_holder.IncomingEventViewHolder;
import com.ck.clump.util.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IncomingEventAdapter extends RecyclerView.Adapter<IncomingEventViewHolder> {

    private List<EventModel> data;
    private Context context;
    private OnItemClickListener listener;

    public IncomingEventAdapter(Context context, List<EventModel> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public IncomingEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incoming_event, null);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new IncomingEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IncomingEventViewHolder holder, int position) {
        EventModel event = data.get(position);
        holder.click(event, listener);
        holder.getTvDateTime().setText(Common.showDateTime(new Date(event.getStartTime())));
        holder.getTvName().setText(event.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(event.getStartTime()));
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            holder.getImvBackground().setImageResource(R.drawable.header_sun);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            holder.getImvBackground().setImageResource(R.drawable.header_afternoon);
        } else {
            holder.getImvBackground().setImageResource(R.drawable.header_night);
        }

        if (event.getLoginUserStatus().equals(EventUserStatus.COUNT_IN.getValue())
                || event.getLoginUserStatus().equals(EventUserStatus.OWNER.getValue())) {
            holder.getImvTag().setImageResource(R.drawable.ribbon_in);
            holder.getTvStatus().setText(EventUserStatus.COUNT_IN.getDisplay());
        } else if (event.getLoginUserStatus().equals(EventUserStatus.COUNT_OUT.getValue())) {
            holder.getImvTag().setImageResource(R.drawable.ribbon_out);
            holder.getTvStatus().setText(EventUserStatus.COUNT_OUT.getDisplay());
        } else {
            holder.getImvTag().setImageResource(R.drawable.ribbon_rsvp);
            holder.getTvStatus().setText(EventUserStatus.NOT_YET.getDisplay());
        }

        if (event.getStatus().equals(EventStatus.PAST.getValue())) {
            holder.getTvStatus().setVisibility(View.INVISIBLE);
            holder.getImvTag().setVisibility(View.INVISIBLE);
        } else {
            holder.getTvStatus().setVisibility(View.VISIBLE);
            holder.getImvTag().setVisibility(View.VISIBLE);
        }
        List<Member> members = event.getMemberList();
        List<Member> displayMembers;
        int count;
        if (members.size() > 3) {
            count = members.size() - 2;
            displayMembers = new ArrayList<>();
            displayMembers.add(members.get(0));
            displayMembers.add(members.get(1));
            Member member = members.get(2);
            member.setPlusCount(count);
            displayMembers.add(member);
        } else {
            displayMembers = members;
        }
        IncomingEventMemberAdapter adapter = new IncomingEventMemberAdapter(context, displayMembers);
        holder.getRvMembers().setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(EventModel Item);
    }

}
