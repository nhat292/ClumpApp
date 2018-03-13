package com.ck.clump.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.ui.activity.model.Country;
import com.ck.clump.ui.activity.view_holder.CountryViewHolder;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryViewHolder> {
    private final OnItemClickListener listener;
    private List<Country> data;

    public CountryAdapter(Context context, List<Country> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        Country country = data.get(position);
        holder.click(country, listener);
        holder.getTvName().setText(country.getName() + " " + country.getCode());
        if (country.isChecked()) {
            holder.getImChecked().setVisibility(View.VISIBLE);
        } else {
            holder.getImChecked().setVisibility(View.GONE);
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
        void onClick(Country Item);
    }

}
