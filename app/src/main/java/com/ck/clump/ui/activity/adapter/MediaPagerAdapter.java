package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.widget.ProgressWheel;
import com.ck.clump.ui.widget.photo_view.PhotoView;
import com.ck.clump.ui.widget.photo_view.PhotoViewAttacher;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nhat on 7/17/17.
 */

public class MediaPagerAdapter extends PagerAdapter {

    private List<Media> items;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public MediaPagerAdapter(Context context, List<Media> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.media_pager_item, container, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Media item = items.get(position);
        Glide.with(context)
                .load(BuildConfig.BASEURL + item.getPath())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        holder.imageView.setImageDrawable(drawable);
                        holder.progressLoading.setVisibility(View.GONE);
                    }
                });
        holder.imageView.getmAttacher().setPhotoClickListener(new PhotoViewAttacher.PhotoClickListener() {
            @Override
            public void onClick() {
                listener.onClick();
            }
        });
        container.addView(view);
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.progressLoading)
        ProgressWheel progressLoading;
        @Bind(R.id.imageView)
        PhotoView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onClick();
    }
}
