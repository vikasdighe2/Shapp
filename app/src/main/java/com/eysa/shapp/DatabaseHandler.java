package com.eysa.shapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler mInstance = null;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "beautyforce_6";
    private static final String TABLE_USER = "user";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_FIRST_NAME = "first_name";
    private static final String KEY_USER_LAST_NAME = "last_name";
    private static final String KEY_USER_EMAIL = "email_address";
    private static final String KEY_USER_FULL_NAME = "full_name";
    private static final String KEY_USER_PUSH_NOTI = "push_notification";
    private static final String KEY_USER_AUTO_REFRESH = "auto_refresh";
    private static final String KEY_USER_EMAIL_NOTI = "email_notification";
    private static final String KEY_USER_LONGITUDE = "longitude";
    private static final String KEY_USER_LATITUDE = "latitude";
    private static final String KEY_USER_CHANNEL_KEY = "channel_key";
    private static final String KEY_USER_API_KEY = "api_key";
    private static final String KEY_USER_IS_PREMIUM = "is_premium";
    private static final String KEY_USER_FB_ID = "fb_id";
    private static final String KEY_USER_AVATAR = "avatar";
    private static final String KEY_USER_PHONE_NUMBER = "phone_number";
    private static final String KEY_USER_CATEGORY_ID = "category_id";
    private static final String KEY_USER_CATEGORY_NAME = "category_name";
    private static final String KEY_USER_QUALITY_ID = "quality_id";
    private static final String KEY_USER_QUALITY_NAME = "quality_name";
    private static final String KEY_USER_SHOW_ALL = "show_all";
    private static final String KEY_USER_RADIUS = "radius";
    private static final String KEY_USER_PRICE_RANGE = "price_range";
    private static final String KEY_USER_COUNTRY_ID = "country_id";
    private static final String KEY_USER_COUNTRY_NAME = "country_name";
    private static final String KEY_USER_STATE_ID = "state_id";
    private static final String KEY_USER_STATE_NAME = "state_name";
    private static final String KEY_USER_CITY_ID = "city_id";
    private static final String KEY_USER_CITY_NAME = "city_name";
	/**************************************************/


    public static DatabaseHandler getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " TEXT PRIMARY KEY,"
                + KEY_USER_FIRST_NAME + " TEXT, "
				+ KEY_USER_LAST_NAME + " TEXT, "
                + KEY_USER_EMAIL + " TEXT, "
                + KEY_USER_FULL_NAME + " TEXT, "
                + KEY_USER_PUSH_NOTI + " INTEGER, "
                + KEY_USER_AUTO_REFRESH + " INTEGER, "
                + KEY_USER_EMAIL_NOTI + " INTEGER, "
                + KEY_USER_LONGITUDE + " INTEGER, "
                + KEY_USER_LATITUDE + " INTEGER, "
                + KEY_USER_CHANNEL_KEY + " TEXT, "
                + KEY_USER_API_KEY + " TEXT, "
                + KEY_USER_IS_PREMIUM + " INTEGER, "
                + KEY_USER_FB_ID + " TEXT, "
                + KEY_USER_AVATAR + " TEXT, "
                + KEY_USER_PHONE_NUMBER + " TEXT, "
                + KEY_USER_CATEGORY_ID + " TEXT, "
                + KEY_USER_CATEGORY_NAME + " TEXT, "
                + KEY_USER_QUALITY_ID + " TEXT, "
                + KEY_USER_QUALITY_NAME + " TEXT, "
                + KEY_USER_SHOW_ALL + " INTEGER, "
                + KEY_USER_RADIUS + " INTEGER, "
                + KEY_USER_PRICE_RANGE + " INTEGER, "
                + KEY_USER_COUNTRY_ID + " TEXT, "
                + KEY_USER_COUNTRY_NAME + " TEXT, "
                + KEY_USER_STATE_ID + " TEXT, "
                + KEY_USER_STATE_NAME + " TEXT, "
                + KEY_USER_CITY_ID + " TEXT, "
                + KEY_USER_CITY_NAME + " TEXT "
				
				+")";
        //Log.d("String:", CREATE_USERS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);


    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    /*******************************************************************************/
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_USER_FIRST_NAME, user.getfirstname());
        values.put(KEY_USER_LAST_NAME, user.getlastname());
        values.put(KEY_USER_EMAIL, user.getemail());
        values.put(KEY_USER_FULL_NAME, user.getfullname());
        values.put(KEY_USER_PUSH_NOTI, user.getpushnoti());
        values.put(KEY_USER_AUTO_REFRESH, user.getautorefresh());
        values.put(KEY_USER_EMAIL_NOTI, user.getemailnoti());
        values.put(KEY_USER_LONGITUDE, user.getlongitude());
        values.put(KEY_USER_LATITUDE, user.getlatitude());
        values.put(KEY_USER_CHANNEL_KEY, user.getchannelkey());
        values.put(KEY_USER_API_KEY, user.getapikey());
        values.put(KEY_USER_IS_PREMIUM, user.getispremium());
        values.put(KEY_USER_FB_ID, user.getfbid());
        values.put(KEY_USER_AVATAR, user.getavatar());
        values.put(KEY_USER_PHONE_NUMBER, user.getphonenumber());
        values.put(KEY_USER_CATEGORY_ID, user.getcategoryid());
        values.put(KEY_USER_CATEGORY_NAME, user.getcategoryname());
        values.put(KEY_USER_QUALITY_ID, user.getqualityid());
        values.put(KEY_USER_QUALITY_NAME, user.getqualityname());
        values.put(KEY_USER_SHOW_ALL, user.getshowall());
        values.put(KEY_USER_RADIUS, user.getradius());
        values.put(KEY_USER_PRICE_RANGE, user.getpricerange());
        values.put(KEY_USER_COUNTRY_ID, user.getcountryid());
        values.put(KEY_USER_COUNTRY_NAME, user.getcountryname());
        values.put(KEY_USER_STATE_ID, user.getstateid());
        values.put(KEY_USER_STATE_NAME, user.getstatename());
        values.put(KEY_USER_CITY_ID, user.getcityid());
        values.put(KEY_USER_CITY_NAME, user.getcityname());




        db.insert(TABLE_USER, null, values);
        Log.d("User created", "Created");
        db.close();
    }

    User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_USER + " LIMIT 1", null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst())
                cursor.moveToFirst();
            User user = new User(cursor.getString(0)
				, cursor.getString(1)
				, cursor.getString(2)
				, cursor.getString(3)
				, cursor.getString(4)
				, Integer.parseInt(cursor.getString(5))
				, Integer.parseInt(cursor.getString(6))
				, Integer.parseInt(cursor.getString(7))
				, Integer.parseInt(cursor.getString(8))
				, Integer.parseInt(cursor.getString(9))
				, cursor.getString(10)
				, cursor.getString(11)
				, Integer.parseInt(cursor.getString(12))
				, cursor.getString(13)
				, cursor.getString(14)
				, cursor.getString(15)
				, cursor.getString(16)
				, cursor.getString(17)
				, cursor.getString(18)
				, cursor.getString(19)
				, Integer.parseInt(cursor.getString(20))
				, Integer.parseInt(cursor.getString(21))
				, Integer.parseInt(cursor.getString(22))
				, cursor.getString(23)
				, cursor.getString(24)
				, cursor.getString(25)
				, cursor.getString(26)
				, cursor.getString(27)
				, cursor.getString(28)
				);
            return user;
        } else {
            return null;
        }
    }


    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_USER_FIRST_NAME, user.getfirstname());
        values.put(KEY_USER_LAST_NAME, user.getlastname());
        values.put(KEY_USER_EMAIL, user.getemail());
        values.put(KEY_USER_FULL_NAME, user.getfullname());
        values.put(KEY_USER_PUSH_NOTI, user.getpushnoti());
        values.put(KEY_USER_AUTO_REFRESH, user.getautorefresh());
        values.put(KEY_USER_EMAIL_NOTI, user.getemailnoti());
        values.put(KEY_USER_LONGITUDE, user.getlongitude());
        values.put(KEY_USER_LATITUDE, user.getlatitude());
        values.put(KEY_USER_CHANNEL_KEY, user.getchannelkey());
        values.put(KEY_USER_API_KEY, user.getapikey());
        values.put(KEY_USER_IS_PREMIUM, user.getispremium());
        values.put(KEY_USER_FB_ID, user.getfbid());
        values.put(KEY_USER_AVATAR, user.getavatar());
        values.put(KEY_USER_PHONE_NUMBER, user.getphonenumber());
        values.put(KEY_USER_CATEGORY_ID, user.getcategoryid());
        values.put(KEY_USER_CATEGORY_NAME, user.getcategoryname());
        values.put(KEY_USER_QUALITY_ID, user.getqualityid());
        values.put(KEY_USER_QUALITY_NAME, user.getqualityname());
        values.put(KEY_USER_SHOW_ALL, user.getshowall());
        values.put(KEY_USER_RADIUS, user.getradius());
        values.put(KEY_USER_PRICE_RANGE, user.getpricerange());
        values.put(KEY_USER_COUNTRY_ID, user.getcountryid());
        values.put(KEY_USER_COUNTRY_NAME, user.getcountryname());
        values.put(KEY_USER_STATE_ID, user.getstateid());
        values.put(KEY_USER_STATE_NAME, user.getstatename());
        values.put(KEY_USER_CITY_ID, user.getcityid());
        values.put(KEY_USER_CITY_NAME, user.getcityname());


        return db.update(TABLE_USER, values, KEY_USER_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
    }

    // Deleting single contact
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_USER_ID + " = ?", new String[] { String.valueOf(user.getID()) });
        db.close();
    }

    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    /****************************************************************/


}
