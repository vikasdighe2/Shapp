package com.eysa.shapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Products> albumList;
    ImageLoader_fragment imageLoader_fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_location, followers_text, products_text, members_ext;
        public ImageView product_background;
        public MyViewHolder(View view) {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            product_location = (TextView) view.findViewById(R.id.product_location);
            followers_text = (TextView) view.findViewById(R.id.followers_text);
            products_text = (TextView) view.findViewById(R.id.products_text);
            members_ext = (TextView) view.findViewById(R.id.members_ext);
            product_background = (ImageView) view.findViewById(R.id.product_background);
        }
    }


    public ProductCardAdapter(Context mContext, List<Products> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        imageLoader_fragment = new ImageLoader_fragment(mContext);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.product_name.setText(albumList.get(position).product_name);
        holder.product_location.setText(""+albumList.get(position).uploaded_in);
        holder.followers_text.setText(""+albumList.get(position).bids_count);
        holder.products_text.setText(""+albumList.get(position).comments_count);
        holder.members_ext.setText(""+albumList.get(position).views_count);
        imageLoader_fragment.DisplayImage(albumList.get(position).product_image, holder.product_background);
    }



    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
