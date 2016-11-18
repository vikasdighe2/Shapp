package com.eysa.shapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class User {

    //private variables
    String id;
	String first_name;
    String last_name;
    String email_address;
    String full_name;
    int push_notification;
    int auto_refresh;
    int email_notification;
    int longitude;
    int latitude;
    String channel_key;
    String api_key;
    int is_premium;
    String fb_id;
    String avatar;
    String phone_number;
    String category_id;
    String category_name;
    String quality_id;
    String quality_name;
    int show_all;
    int radius;
    int price_range;
    String country_id;
    String country_name;
    String state_id;
    String state_name;
    String city_id;
    String city_name;

    // Empty constructor
    public User(){

    }

    // constructor
    public User(String id, String first_name, String last_name, String email_address, String full_name, int push_notification, int auto_refresh, int email_notification, int longitude, int latitude, String channel_key, String api_key, int is_premium, String fb_id, String avatar, String phone_number, String category_id, String category_name, String quality_id, String quality_name, int show_all, int radius, int price_range, String country_id, String country_name, String state_id, String state_name, String city_id, String city_name
    ){

        this.id = id;
		this.first_name =  first_name;
		this.last_name = last_name;
		this.email_address = email_address;
		this.full_name = full_name;
		this.push_notification = push_notification;
		this.auto_refresh = auto_refresh;
		this.email_notification = email_notification;
		this.longitude = longitude;
		this.latitude = latitude;
		this.channel_key = channel_key;
		this.api_key = api_key;
		this.is_premium = is_premium;
		this.fb_id = fb_id;
		this.avatar = avatar;
		this.phone_number = phone_number;
		this.category_id = category_id;
		this.category_name = category_name;
		this.quality_id = quality_id;
		this.quality_name = quality_name;
		this.show_all = show_all;
		this.radius = radius;
		this.price_range = price_range;
		this.country_id = country_id;
		this.country_name = country_name;
		this.state_id = state_id;
		this.state_name = state_name;
		this.city_id = city_id;
		this.city_name = city_name;

    }



    /*********GET METHODS***************/

    public String getID(){
        return this.id;
    }

    public String getemail(){
        return this.email_address;
    }

    public String getfirstname(){
        return this.first_name;
    }

    public String getlastname(){
        return this.last_name;
    }


    public String getfullname(){
        return this.full_name;
    }
    public int getpushnoti(){
        return this.push_notification;
    }
    public int getautorefresh(){
        return this.auto_refresh;
    }
    public int getemailnoti(){
        return this.email_notification;
    }
    public int getlongitude(){
        return this.longitude;
    }
    public int getlatitude(){
        return this.latitude;
    }
    public String getchannelkey(){
        return this.channel_key;
    }
    public String getapikey(){
        return this.api_key;
    }
    public int getispremium(){
        return this.is_premium;
    }
    public String getfbid(){
        return this.fb_id;
    }
    public String getavatar(){
        return this.avatar;
    }
    public String getphonenumber(){
        return this.phone_number;
    }
    public String getcategoryid(){
        return this.category_id;
    }
    public String getcategoryname(){
        return this.category_name;
    }
    public String getqualityid(){
        return this.quality_id;
    }

    public String getqualityname(){
        return this.quality_name;
    }
    public int getshowall(){
        return this.show_all;
    }
    public int getradius(){
        return this.radius;
    }
    public int getpricerange(){
        return this.price_range;
    }
    public String getcountryid(){
        return this.country_id;
    }
    public String getcountryname(){
        return this.country_name;
    }
    public String getstateid(){
        return this.state_id;
    }
    public String getstatename(){
        return this.state_name;
    }
    public String getcityid(){
        return this.city_id;
    }
    public String getcityname(){
        return this.city_name;
    }


    /*********SET METHODS***************/


    public void setID(String id){
        this.id = id;
    }


    public void setemail(String new_email){
        this.email_address = new_email;
    }

    public void setfirstname(String fname){
        this.first_name = fname;
    }

    public void setlastname(String lname){
        this.last_name = lname;
    }

    public void setprofilepic(String profilePicture){
        this.avatar = profilePicture;
    }


}
