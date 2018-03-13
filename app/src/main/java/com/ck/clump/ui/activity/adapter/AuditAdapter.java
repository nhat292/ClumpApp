package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.Audit;
import com.ck.clump.ui.activity.view_holder.AuditViewHolder;

import java.util.List;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class AuditAdapter extends RecyclerView.Adapter<AuditViewHolder> {

    private final OnItemClickListener listener;
    private List<Audit> data;
    private Context context;

    public AuditAdapter(Context context, List<Audit> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public AuditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audit, null);
        return new AuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuditViewHolder holder, int position) {
        Audit activity = data.get(position);
        holder.click(activity, listener);
        String content = activity.getDescription() + " <b>" + activity.getEventName() + "</b>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.getTvName().setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.getTvName().setText(Html.fromHtml(content));
        }
        holder.getTvDate().setText(activity.getPointOfTime());
        if (position == data.size() - 1) {
            holder.getLine().setVisibility(View.GONE);
        } else {
            holder.getLine().setVisibility(View.VISIBLE);
        }
        Glide.with(context).load(BuildConfig.BASEURL + activity.getAvatarPath())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.default_profile_user)
                        .placeholder(R.drawable.default_profile_user))
                .apply(overrideOf(100, 100))
                .into(holder.getProfileImage());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(Audit Item);
    }

}
