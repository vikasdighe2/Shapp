package com.eysa.shapp;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
public class GroupCardAdapter extends RecyclerView.Adapter<GroupCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Groups> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView group_name, members, products;
        public MyViewHolder(View view) {
            super(view);
            group_name = (TextView) view.findViewById(R.id.group_name);
            members = (TextView) view.findViewById(R.id.member_count);
            products = (TextView) view.findViewById(R.id.product_count);
        }
    }


    public GroupCardAdapter(Context mContext, List<Groups> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Groups album = albumList.get(position);
        holder.group_name.setText(albumList.get(position).group_name);
        holder.members.setText(""+albumList.get(position).members_count);
        holder.products.setText(""+albumList.get(position).products_count);

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
