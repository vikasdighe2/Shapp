package com.eysa.shapp;


import java.util.List;

class Products{

    String id;
    String product_name;
    String uploaded_by_name;
    String uploaded_by_id;
    String uploaded_in;
    String uploaded_by_image;
    int user_is_premium;
    String product_image;
    String product_description;
    int bill_included;
    int min_bid_price;
    int buy_now_price;
    int latest_bid;
    int views_count;
    int bids_count;
    int comments_count;
    int my_bid;
    int bid_closed;
    String bid_start;
    String bid_end;
    int rated_by_user;
    int ratings;
    List<ProductImages> images;
    int is_sold;
    int sold_to_user;
    String latitude;
    String longitude;


    Products(String id, String product_name, String uploaded_by_name, String uploaded_by_id, String uploaded_in, String uploaded_by_image, int user_is_premium, String product_image, String product_description, int bill_included, int min_bid_price, int buy_now_price, int latest_bid, int views_count, int bids_count, int comments_count, int my_bid, int bid_closed, String bid_start, String bid_end, int rated_by_user, int ratings, List<ProductImages> images, int is_sold, int sold_to_user, String latitude, String longitude){

        this.id = id;
        this.product_name = product_name;
        this.uploaded_by_name = uploaded_by_name;
        this.uploaded_by_id = uploaded_by_id;
        this.uploaded_in = uploaded_in;
        this.uploaded_by_image = uploaded_by_image;
        this.user_is_premium  = user_is_premium;
        this.product_image = product_image;
        this.product_description = product_description;
        this.bill_included = bill_included;
        this.min_bid_price = min_bid_price;
        this.buy_now_price = buy_now_price;
        this.latest_bid = latest_bid;
        this.views_count = views_count;
        this.bids_count = bids_count;
        this.comments_count = comments_count;
        this.my_bid = my_bid;
        this.bid_closed = bid_closed;
        this.bid_start = bid_start;
        this.bid_end = bid_end;
        this.rated_by_user = rated_by_user;
        this.ratings = ratings;
        this.images = images;
        this.is_sold = is_sold;
        this.sold_to_user = sold_to_user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}



class ProductImages{

String image_path;

    ProductImages( String image_path){
		this.image_path = image_path;
    }

}



class Notifications{
    String id;
    String created_at;
    String notification_string;
    String notification_type;


    Notifications(String id, String created_at, String notification_string, String notification_type){

        this.id = id;
        this.created_at = created_at;
        this.notification_string = notification_string;
        this.notification_type = notification_type;
    }

}




class Category{


    Category(){


    }
}



class Groups{

    String id;
    String group_name;
    String admin_name;
    String admin_location;
    String admin_photo;
    String group_image;
    String created_at;
    int members_count;
    int products_count;
    int followers_count;
    int in_group;
    int user_is_admin;
    int is_following;


    Groups(String id, String group_name, String admin_name, String admin_location, String admin_photo, String group_image, String created_at, int members_count, int products_count, int followers_count, int in_group, int user_is_admin, int is_following){

        this.id = id;
        this.group_name = group_name;
        this.admin_name = admin_name;
        this.admin_location = admin_location;
        this.admin_photo = admin_photo;
        this.group_image = group_image;
        this.created_at = created_at;
        this.members_count = members_count;
        this.products_count = products_count;
        this.followers_count = followers_count;
        this.in_group = in_group;
        this.user_is_admin = user_is_admin;
        this.is_following = is_following;

    }
}

class Followers{

    String id;
    String full_name;
    String avatar;
    String location;
    int products_count;
    int followers_count;
    int following_count;
    int is_following;
    int bids_count;
    int is_premium;
    int rated_by_user;
    int ratings;
    int is_online;


    Followers(String id, String full_name, String avatar, String location, int products_count, int followers_count, int following_count, int is_following, int bids_count, int is_premium, int rated_by_user, int ratings, int is_online){

        this.id = id;
        this.full_name = full_name;
        this.avatar = avatar;
        this.location = location;
        this.products_count = products_count;
        this.followers_count = followers_count;
        this.following_count = following_count;
        this.is_following = is_following;
        this.bids_count = bids_count;
        this.is_premium = is_premium;
        this.rated_by_user = rated_by_user;
        this.ratings = ratings;
        this.is_online = is_online;


    }
}

