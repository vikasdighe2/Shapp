package com.eysa.shapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailsActivity extends Activity {

    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;
    private RecyclerView recyclerView;
    private GroupCardAdapter adapter;
    private ProductCardAdapter productAdapter;
    private List<Groups> albumList;
    private List<Products> productData;

    TextView group_details_page_header_text, admin_name, group_location_text, pro_count_text,
            mem_count_text, follow_count_count, group_details,to_sell, sold;
    CircleImageView group_admin_image;
    ImageView group_details_page_close_image, group_background, follo_unfollow;

    String PROFILE_ID, ADMIN_NAME, ADMIN_PHOTO, ADMIN_LOCATION, QUERY;
    int FOLLOWERS_COUNT, PRODUCTS_COUNT, MEMBERS_COUNT, IS_SEARCH = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details_view);

        imageLoader = new ImageLoader(getApplicationContext(), this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            PROFILE_ID = extras.getString("profile_id");
            ADMIN_NAME = extras.getString("admin_name");
            ADMIN_PHOTO = extras.getString("admin_photo");
            ADMIN_LOCATION = extras.getString("profile_location");
            QUERY = extras.getString("query");
            IS_SEARCH = extras.getInt("is_search", 0);
        }
        pro_count_text = (TextView) this.findViewById(R.id.pro_count_text);
        mem_count_text = (TextView) this.findViewById(R.id.mem_count_text);
        follow_count_count = (TextView) this.findViewById(R.id.follow_count_count);
        group_details_page_header_text = (TextView) this.findViewById(R.id.group_details_page_header_text);
        admin_name = (TextView) this.findViewById(R.id.admin_name);
        group_location_text = (TextView) this.findViewById(R.id.group_location_text);
        group_details = (TextView) this.findViewById(R.id.group_details);
        to_sell = (TextView) this.findViewById(R.id.to_sell);
        sold = (TextView) this.findViewById(R.id.sold);
        group_details_page_close_image = (ImageView) this.findViewById(R.id.back_image);
        follo_unfollow = (ImageView) this.findViewById(R.id.follo_unfollow);
        group_admin_image = (CircleImageView) this.findViewById(R.id.group_admin_image);

        albumList = new ArrayList<>();
        adapter = new GroupCardAdapter(ProfileDetailsActivity.this, albumList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ProfileDetailsActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        group_details.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
        recyclerView.setAdapter(adapter);
        prepareAlbums();
        group_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_details.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
                to_sell.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                sold.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                recyclerView.setAdapter(adapter);
                prepareAlbums();
            }
        });
        productData = new ArrayList<>();
        productAdapter = new ProductCardAdapter(ProfileDetailsActivity.this, productData);
        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(ProfileDetailsActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        to_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_sell.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
                sold.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                group_details.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                recyclerView.setAdapter(productAdapter);
                getSellProductData();
            }
        });
        productData = new ArrayList<>();
        productAdapter = new ProductCardAdapter(ProfileDetailsActivity.this, productData);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(ProfileDetailsActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sold.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
                to_sell.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                group_details.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                recyclerView.setAdapter(productAdapter);
                getSoldProductData();
            }
        });

        group_details_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_SEARCH == 1){
                    Intent GroupDetailsClass = new Intent(ProfileDetailsActivity.this, SearchActivity.class);
                    GroupDetailsClass.putExtra("flag", 3);
                    GroupDetailsClass.putExtra("query", QUERY);
                    startActivity(GroupDetailsClass);
                }else {
                    finish();
                }
            }
        });

        imageLoader.DisplayImage(ADMIN_PHOTO, group_admin_image);
        imageLoader.DisplayImage(ADMIN_PHOTO, group_background);
        final JSONObject profileData = dataAccess.getUserProfileDetails(PROFILE_ID);
        group_details_page_header_text.setText(""+ADMIN_NAME);
        admin_name.setText(""+ADMIN_NAME);
        group_location_text.setText(""+ADMIN_LOCATION);
        try {
            follow_count_count.setText(""+profileData.getInt("ratings"));
             pro_count_text.setText(""+profileData.getInt("products_count"));
              mem_count_text.setText(""+profileData.getInt("followers_count"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (loggedInUser.id.equals(PROFILE_ID)) {
            follo_unfollow.setVisibility(View.INVISIBLE);
        }
        try {
            if( profileData.getBoolean("is_following")){
                follo_unfollow.setImageResource(R.drawable.follow_icon);
            } else {
                follo_unfollow.setImageResource(R.drawable.unfollow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        follo_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if( profileData.getBoolean("is_following")){
                        follo_unfollow.setImageResource(R.drawable.unfollow);
                        int status = dataAccess.followUnfollowMethod(PROFILE_ID);
                    } else {
                        follo_unfollow.setImageResource(R.drawable.follow_icon);
                        int status = dataAccess.followUnfollowMethod(PROFILE_ID);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    } /* end of onCreate() */



    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void prepareAlbums() {
        albumList.clear();
     List<Groups> listGroupData = dataAccess.getGroupData(PROFILE_ID);
        for(Groups groupData : listGroupData){
            albumList.add(groupData);
        }
        //albumList = dataAccess.get_downloadedGroupsData();
        adapter.notifyDataSetChanged();
    }
    private void getSellProductData() {
        if(productData.size() > 0) {
            productData.clear();
        }
        List<Products> listProductData = dataAccess.getProductData("my_products", PROFILE_ID);
        for(Products data : listProductData){
        productData.add(data);
        }
        //albumList = dataAccess.get_downloadedGroupsData();
        productAdapter.notifyDataSetChanged();
    }

    private void getSoldProductData() {
        if(productData.size() > 0) {
            productData.clear();
        }
        List<Products> listProductData = dataAccess.getProductData("sold_products", PROFILE_ID);
        for(Products data : listProductData){
            productData.add(data);
        }
        //albumList = dataAccess.get_downloadedGroupsData();
        productAdapter.notifyDataSetChanged();
    }
}
