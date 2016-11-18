package com.eysa.shapp;


public class Constants {


        static int API_STAGE = 0; /* 0:development; 1:production*/

        static String BASE_URL = "http://www.shapp.dk/";
        static String API_URL = "http://www.shapp.dk/v1/api/";

        //static String BASE_URL = "http://192.168.1.100/";
        //static String API_URL = "http://192.168.1.100/v1/api/";

        static String[] EXPECTED_LANG = {"en", "fr", "es"};


        static String CHECK_LOGIN = "check_login";
        static String FB_LOGIN = "check_login_by_facebook";
        static String CREATE_ACCOUNT = "create_account";
        static String GET_MY_PRODUCTS = "get_my_products";
        static String GET_PRODUCT = "get_product";
        static String GET_NOTIFICATIONS = "get_notifications";
        static String UPDATE_SEARCH_PROFILE = "update_search_profile";
        static String GET_DISCOVER_PRODUCTS = "discover_products";
        static String GET_QUALITIES = "get_qualities";
        static String GET_CATEGORIES = "get_categories";
        static String GET_BRANDS = "get_brands";
        static String GET_MODELS = "get_models";
        static String CREATE_PRODUCT = "create_product";
        static String GET_PRODUCT_COMMENTS = "product_comments";
        static String ADD_BID_TO_PRODUCT = "add_bid_to_product";
        static String ADD_RATING_TO_PRODUCT = "add_rating";
        static String ADD_COMMENT_TO_PRODUCT = "add_comment";
        static String UPDATE_PROFILE = "update_profile";
        static String SEARCH_COUNTRIES = "get_countries";
        static String SEARCH_STATES = "get_states";
        static String SEARCH_CITIES = "get_cities";
        static String SEARCH_GROUP = "search_groups";
        static String GET_USER_DATA = "get_user_data";
        static String GET_USER_PROFILE_DATA = "get_user_profile";
        static String GET_GROUPS = "get_groups";
        static String CREATE_GROUP = "create_group";
        static String UPDATE_GROUP = "update_group";
        static String GET_USER_FOLLOWERS = "get_user_followers";
        static String FOLLOW_UNFOLLOW = "follow_unfollow";
        static String FOLLOW_UNFOLLOW_GROUP = "follow_unfollow_group";
        static String REPORT_PRODUCT = "report_product";
        static String DELETE_GROUP = "delete_group";
        static String SEARCH_ITEMS = "search_items";
        static String BUY_PRODUCT = "buy_product";
        static String ADD_REMOVE_SAVED_SHAPP = "add_remove_saved_shapp";
        static String GET_GROUP_ITEM = "get_group_items";
        static String GET_CHAT_LIST = "get_chat_list";
        static String FORGET_PASSWORD = "forget_password";
        static String INVITE_TO_GROUP = "invite_to_group";


        static int PUSH_NOTIFICATION = 1;
        static int EMAIL_NOTIFICATION = 2;

        static int FREE_PRODUCT_IMAGES = 3;
        static int FREE_PRODUCT_VIDEOS = 1;
        static int MAX_PRODUCT_IMAGES = 30;

        static int BF_VIDEOHOMEWORKS_MENU_TYPE = 4;
        static int BF_OTHERHOMEWORKS_MENU_TYPE = 5;
        static int BF_PDFHOMEWORKS_MENU_TYPE = 6;



        /*X-Api-Key,
                X-Api-UserEmail,
                X-Api-HardwareID,
                X-Api-PushEnabled,
                X-Api-PushToken,
                X-Api-DeviceType,
                X-Api-DeviceModel,
                X-Api-Language,
                Content-Type,
                Accept
            */
/*
"1 - ScissorBoy Admin"
"2 - VIP"
"3 - School Admin"
"4 - Student"
"5 - Paid User"
"6 - School Owner"
"7 - General Users"
"8 - Free User"
"9 - Manager"
"10 - Stylist"
"11 - Receptionist / Manager"
"12 - Instructor / Manager"
"13 - Salon Owner"
"14 - App"
"15 - Telephony"
"16 - Scheduling"
"17 - Educational Director"
"18 - None"
"19 - Master Admin"
"20 - Account Admin"
"21 - Educator"
"22 - Sales Representative"
"23 - Content Manager"
"24 - Super Admin"

*/




}
