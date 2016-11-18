package com.eysa.shapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class DataAccess extends Activity {

    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    DatabaseHandler db;
    int status = 0;
    public JSONObject searchedObject = new JSONObject();
    String deviceLanguage = "en";
    String dev_lang = Locale.getDefault().getLanguage();
    String[] expected_lang = Constants.EXPECTED_LANG;
    String title;
    private static DataAccess objLogger;



    //public ArrayList<AllBrands> downloadedAllBrandsData = new ArrayList<AllBrands>();
    public Products productData;
    public ArrayList<Products> downloadedMyBidsData = new ArrayList<Products>();
    public ArrayList<Products> getProductData = new ArrayList<Products>();
    public ArrayList<Products> downloadedSavedShappsData = new ArrayList<Products>();
    public ArrayList<Products> downloadedMySalesData = new ArrayList<Products>();
    public ArrayList<Notifications> downloadedNotificationsData = new ArrayList<Notifications>();
    public ArrayList<Products> downloadedDiscoverData = new ArrayList<Products>();
    public ArrayList<Products> discoverData = new ArrayList<Products>();
    public ArrayList<Groups> downloadedGroupsData = new ArrayList<Groups>();
    public List<Groups> data = new ArrayList<>();
    public ArrayList<Followers> downloadedFollowersData = new ArrayList<Followers>();
    //public ArrayList<MenuSeminar> downloadedMenuSeminarData = new ArrayList<MenuSeminar>();

    AsyncTask async_updataAllData, async_updataAllLibraryData;
    public JSONArray searchResultArr;
    public JSONArray groupItemDetails;
    public JSONArray chatData;
    public JSONObject profileData;


    private DataAccess(Context context) {
        mContext = context;
      /*  fileCache=new FileCache(context);*/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Log.d("dev_lang", "" + dev_lang);
        for (int i = 0; i < expected_lang.length; i++) {
            if (expected_lang[i].contains(dev_lang)) {
                deviceLanguage = expected_lang[i];
            }
        }


    }

    public static DataAccess getInstance(Context context) {
        if (objLogger == null) {
            objLogger = new DataAccess(context);
        }
        return objLogger;
    }

    private Context mContext;

    public User getLoggedInUser() {
        DatabaseHandler db = DatabaseHandler.getInstance(mContext);
        User loggedInUser = db.getUser();
        return loggedInUser;
    }

    public BasicHttpClient basicClient() {
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);

        User loggedInUser = getLoggedInUser();
        if (loggedInUser != null) {
            //httpClientUserData.addHeader("HTTP_X_API_KEY", loggedInUser.api_key);
            //httpClientUserData.addHeader("HTTP_X_API_USEREMAIL", loggedInUser.email_address);
           httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
            httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);
            //httpClientUserData.addHeader("X-Api-Language", deviceLanguage);



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
        }
        return httpClientUserData;
    }




    /********************************************************/

    public void downloadMyBids() {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("mybids_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    downloadedMyBidsData.clear();
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("product_type", "my_bids");
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_MY_PRODUCTS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            JSONArray ProductsArr = (JSONArray) DataObject.get("products");

                            for (int i = 0; i < ProductsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject productObject = (JSONObject) ProductsArr.get(i);
                                ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                                if (productObject.has("images")) {
                                    JSONArray productImages = (JSONArray) productObject.get("images");
                                    Log.d("productImages", "" + productImages.toString());
                                    for (int j = 0; j < productImages.length(); j++) {
                                        String receivedProductImage = productImages.getString(j);
                                        ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                        imagesData.add(receivedImagesData);
                                    }
                                }

                                Products receivedProduct = new Products(productObject.getString("id")
                                        , productObject.getString("product_name")
                                        , productObject.getString("uploaded_by_name")
                                        , productObject.getString("uploaded_by_id")
                                        , productObject.getString("uploaded_in")
                                        , productObject.getString("uploaded_by_image")
                                        , productObject.getInt("user_is_premium")
                                        , productObject.getString("product_image")
                                        , productObject.getString("product_description")
                                        , productObject.getBoolean("bill_included") ? 1 : 0
                                        , productObject.getInt("min_bid_price")
                                        , productObject.getInt("buy_now_price")
                                        , productObject.getInt("latest_bid")
                                        , productObject.getInt("views_count")
                                        , productObject.getInt("bids_count")
                                        , productObject.getInt("comments_count")
                                        , productObject.getInt("my_bid")
                                        , productObject.getInt("bid_closed")
                                        , productObject.getString("bid_start")
                                        , productObject.getString("bid_end")
                                        , productObject.getInt("rated_by_user")
                                        , productObject.getInt("ratings")
                                        , imagesData
                                        , productObject.getInt("is_sold")
                                        , productObject.getInt("sold_to_user")
                                        , productObject.getString("latitude")
                                        , productObject.getString("longitude"));

                                if (!downloadedMyBidsData.contains(receivedProduct)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedMyBidsData.add(receivedProduct);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }
    /**************************************************/
    public ArrayList<Products> get_downloadedMyBidsData() {
        ArrayList<Products> productsList = new ArrayList<Products>();
        for (Products selectedMyBids : downloadedMyBidsData) {
            if (!productsList.contains(selectedMyBids)) {
                productsList.add(selectedMyBids);
            }
        }
        return productsList;
    }

    /**************************************************/


    public void downloadSavedShapps() {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("saved_shapps_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    downloadedSavedShappsData.clear();
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("product_type", "saved_shapps");
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_MY_PRODUCTS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            JSONArray ProductsArr = (JSONArray) DataObject.get("products");

                            for (int i = 0; i < ProductsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject productObject = (JSONObject) ProductsArr.get(i);
                                ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                                if (productObject.has("images")) {
                                    JSONArray productImages = (JSONArray) productObject.get("images");
                                    Log.d("productImages", "" + productImages.toString());
                                    for (int j = 0; j < productImages.length(); j++) {
                                        String receivedProductImage = productImages.getString(j);
                                        ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                        imagesData.add(receivedImagesData);
                                    }
                                }

                                Products receivedProduct = new Products(productObject.getString("id")
                                        , productObject.getString("product_name")
                                        , productObject.getString("uploaded_by_name")
                                        , productObject.getString("uploaded_by_id")
                                        , productObject.getString("uploaded_in")
                                        , productObject.getString("uploaded_by_image")
                                        , productObject.getInt("user_is_premium")
                                        , productObject.getString("product_image")
                                        , productObject.getString("product_description")
                                        , productObject.getBoolean("bill_included") ? 1 : 0
                                        , productObject.getInt("min_bid_price")
                                        , productObject.getInt("buy_now_price")
                                        , productObject.getInt("latest_bid")
                                        , productObject.getInt("views_count")
                                        , productObject.getInt("bids_count")
                                        , productObject.getInt("comments_count")
                                        , productObject.getInt("my_bid")
                                        , productObject.getInt("bid_closed")
                                        , productObject.getString("bid_start")
                                        , productObject.getString("bid_end")
                                        , productObject.getInt("rated_by_user")
                                        , productObject.getInt("ratings")
                                        , imagesData
                                        , productObject.getInt("is_sold")
                                        , productObject.getInt("sold_to_user")
                                        , productObject.getString("latitude")
                                        , productObject.getString("longitude"));


                                if (!downloadedSavedShappsData.contains(receivedProduct)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedSavedShappsData.add(receivedProduct);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }
    /**************************************************/
    public ArrayList<Products> get_downloadedSavedShappsData() {
        ArrayList<Products> productsList = new ArrayList<Products>();
        for (Products selectedMyBids : downloadedSavedShappsData) {
            if (!productsList.contains(selectedMyBids)) {
                productsList.add(selectedMyBids);
            }
        }
        return productsList;
    }


    /********************************************************/

    public void downloadMySales() {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("mysales_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    downloadedMySalesData.clear();
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("product_type", "my_sales");
                    httpClientUserData.setConnectionTimeout(60000);
                    httpClientUserData.setReadTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_MY_PRODUCTS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            JSONArray ProductsArr = (JSONArray) DataObject.get("products");

                            for (int i = 0; i < ProductsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject productObject = (JSONObject) ProductsArr.get(i);
                                ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                                if (productObject.has("images")) {
                                    JSONArray productImages = (JSONArray) productObject.get("images");
                                    Log.d("productImages", "" + productImages.toString());
                                    for (int j = 0; j < productImages.length(); j++) {
                                        String receivedProductImage = productImages.getString(j);
                                        ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                        imagesData.add(receivedImagesData);
                                    }
                                }

                                Products receivedProduct = new Products(productObject.getString("id")
                                        , productObject.getString("product_name")
                                        , productObject.getString("uploaded_by_name")
                                        , productObject.getString("uploaded_by_id")
                                        , productObject.getString("uploaded_in")
                                        , productObject.getString("uploaded_by_image")
                                        , productObject.getInt("user_is_premium")
                                        , productObject.getString("product_image")
                                        , productObject.getString("product_description")
                                        , productObject.getBoolean("bill_included") ? 1 : 0
                                        , productObject.getInt("min_bid_price")
                                        , productObject.getInt("buy_now_price")
                                        , productObject.getInt("latest_bid")
                                        , productObject.getInt("views_count")
                                        , productObject.getInt("bids_count")
                                        , productObject.getInt("comments_count")
                                        , productObject.getInt("my_bid")
                                        , productObject.getInt("bid_closed")
                                        , productObject.getString("bid_start")
                                        , productObject.getString("bid_end")
                                        , productObject.getInt("rated_by_user")
                                        , productObject.getInt("ratings")
                                        , imagesData
                                        , productObject.getInt("is_sold")
                                        , productObject.getInt("sold_to_user")
                                        , productObject.getString("latitude")
                                        , productObject.getString("longitude"));


                                if (!downloadedMySalesData.contains(receivedProduct)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedMySalesData.add(receivedProduct);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }
    /**************************************************/
    public ArrayList<Products> get_downloadedMySalesData() {
        ArrayList<Products> productsList = new ArrayList<Products>();
        for (Products selectedMyBids : downloadedMySalesData) {
            if (!productsList.contains(selectedMyBids)) {
                productsList.add(selectedMyBids);
            }
        }
        return productsList;
    }

    /**************************************************/


    public void download_productDetails(final String PRODUCT_ID) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("product_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("product_id", PRODUCT_ID);
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_PRODUCT, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject productObject = (JSONObject) DataObject.getJSONObject("product");
                                ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                                if (productObject.has("images")) {
                                    JSONArray productImages = (JSONArray) productObject.get("images");
                                    Log.d("productImages", "" + productImages.toString());
                                    for (int j = 0; j < productImages.length(); j++) {
                                        String receivedProductImage = productImages.getString(j);
                                        ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                        imagesData.add(receivedImagesData);
                                    }
                                }

                                productData = new Products(productObject.getString("id")
                                        , productObject.getString("product_name")
                                        , productObject.getString("uploaded_by_name")
                                        , productObject.getString("uploaded_by_id")
                                        , productObject.getString("uploaded_in")
                                        , productObject.getString("uploaded_by_image")
                                        , productObject.getInt("user_is_premium")
                                        , productObject.getString("product_image")
                                        , productObject.getString("product_description")
                                        , productObject.getBoolean("bill_included") ? 1 : 0
                                        , productObject.getInt("min_bid_price")
                                        , productObject.getInt("buy_now_price")
                                        , productObject.getInt("latest_bid")
                                        , productObject.getInt("views_count")
                                        , productObject.getInt("bids_count")
                                        , productObject.getInt("comments_count")
                                        , productObject.getInt("my_bid")
                                        , productObject.getInt("bid_closed")
                                        , productObject.getString("bid_start")
                                        , productObject.getString("bid_end")
                                        , productObject.getInt("rated_by_user")
                                        , productObject.getInt("ratings")
                                        , imagesData
                                        , productObject.getInt("is_sold")
                                        , productObject.getInt("sold_to_user")
                                        , productObject.getString("latitude")
                                        , productObject.getString("longitude"));


                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }

    /**************************************************/

    public Products get_downloadedProduct() {
         return productData;
    }

    /*********************************************************/


    public void downloadNotifications(final int PAGE_NUMBER) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("notifications_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    downloadedNotificationsData.clear();
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("page_number", String.valueOf(PAGE_NUMBER));
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_NOTIFICATIONS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            //Log.d("collections.length()", "" + collections.length());
                            JSONArray NotificationsArr = (JSONArray) DataObject.get("notifications");

                            for (int i = 0; i < NotificationsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject notificationObject = (JSONObject) NotificationsArr.get(i);

                                Notifications receivedNotification = new Notifications(notificationObject.getString("id")
                                        , notificationObject.getString("created_at")
                                        , notificationObject.getString("notification_string")
                                        , notificationObject.getString("notification_type"));


                                if (!downloadedNotificationsData.contains(receivedNotification)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedNotificationsData.add(receivedNotification);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }
                            }

                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }

    /**************************************************/


    public ArrayList<Notifications> get_downloadedNotifications() {
        ArrayList<Notifications> notificationsList = new ArrayList<Notifications>();
        for (Notifications selectedNotification : downloadedNotificationsData) {
            if (!notificationsList.contains(selectedNotification)) {
                notificationsList.add(selectedNotification);
            }
        }
        return notificationsList;
    }
    /*********************************************************/



    public void downloadDiscoverData(final int PAGE_NUMBER) {
                try {
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("page_number", String.valueOf(PAGE_NUMBER));
                    httpClientUserData.setConnectionTimeout(60000);
                    httpClientUserData.setReadTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_DISCOVER_PRODUCTS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            downloadedDiscoverData.remove(downloadedDiscoverData);
                            downloadedDiscoverData.clear();
                            JSONArray ProductsArr = (JSONArray) DataObject.get("products");

                            for (int i = 0; i < ProductsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject productObject = (JSONObject) ProductsArr.get(i);
                                ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                                if (productObject.has("images")) {
                                    JSONArray productImages = (JSONArray) productObject.get("images");
                                    Log.d("productImages", "" + productImages.toString());
                                    for (int j = 0; j < productImages.length(); j++) {
                                        String receivedProductImage = productImages.getString(j);
                                        ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                        imagesData.add(receivedImagesData);
                                    }
                                }

                                Products receivedProduct = new Products(productObject.getString("id")
                                        , productObject.getString("product_name")
                                        , productObject.getString("uploaded_by_name")
                                        , productObject.getString("uploaded_by_id")
                                        , productObject.getString("uploaded_in")
                                        , productObject.getString("uploaded_by_image")
                                        , productObject.getInt("user_is_premium")
                                        , productObject.getString("product_image")
                                        , productObject.getString("product_description")
                                        , productObject.getBoolean("bill_included") ? 1 : 0
                                        , productObject.getInt("min_bid_price")
                                        , productObject.getInt("buy_now_price")
                                        , productObject.getInt("latest_bid")
                                        , productObject.getInt("views_count")
                                        , productObject.getInt("bids_count")
                                        , productObject.getInt("comments_count")
                                        , productObject.getInt("my_bid")
                                        , productObject.getInt("bid_closed")
                                        , productObject.getString("bid_start")
                                        , productObject.getString("bid_end")
                                        , productObject.getInt("rated_by_user")
                                        , productObject.getInt("ratings")
                                        , imagesData
                                        , productObject.getInt("is_sold")
                                        , productObject.getInt("sold_to_user")
                                        , productObject.getString("latitude")
                                        , productObject.getString("longitude"));


                                if (!downloadedDiscoverData.contains(receivedProduct)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedDiscoverData.add(receivedProduct);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
    }

    /**************************************************/


    public ArrayList<Products> get_downloadedDiscoverData() {
        ArrayList<Products> discoverList = new ArrayList<Products>();
        for (Products selectedDiscover : downloadedDiscoverData) {
            if (!discoverList.contains(selectedDiscover)) {
                discoverList.add(selectedDiscover);
            }
        }
        return discoverList;
    }
    /*********************************************************/

    public void update_user_data(final String param_name, final String param_value) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                //Intent intent = new Intent("profile-downloaded");
                //LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                User loggedInUser = getLoggedInUser();
                BasicHttpClient leaderboardClient = basicClient();
                ParameterMap parameters = leaderboardClient.newParams().add(param_name, param_value);
                leaderboardClient.setConnectionTimeout(2000);
                HttpResponse httpResponse = leaderboardClient.post(Constants.UPDATE_PROFILE, parameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        //downloadUserPrifile();
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
            }
        }.execute();
    }



    /*********************************************************/
    /**************************************************/
    public void downloadUserDataFromServer() {

        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualDownload();
                return null;
            }

            protected void onPostExecute(Boolean result) {

                Log.d("Result: ", "" + result);
                try {
                    Log.d("onPostExecute", "userdata_downloaded");
                    Intent intent = new Intent("userdata_downloaded");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }/*END of onPostExecute()*/


            private void actualDownload() {
                User loggedInUser = getLoggedInUser();

                BasicHttpClient searchClient = basicClient();
                ParameterMap parameters = searchClient.newParams();
                searchClient.setConnectionTimeout(60000);
                HttpResponse httpResponse = searchClient.post(Constants.GET_USER_DATA, parameters);

                try {
                    Log.d("inside actualDownload", "User Notification: " + httpResponse.toString());

                    JSONObject userDataResult = new JSONObject(httpResponse.getBodyAsString());
                    JSONObject userDataObject = userDataResult.getJSONObject("user_data");
                    User userData = new User(
                            loggedInUser.id,
                            userDataObject.getString("first_name"),
                            userDataObject.getString("last_name"),
                            userDataObject.getString("email_address"),
                            userDataObject.getString("full_name"),
                            userDataObject.getInt("push_notification"),
                            userDataObject.getInt("auto_refresh"),
                            userDataObject.getInt("email_notification"),
                            userDataObject.getInt("longitude"),
                            userDataObject.getInt("latitude"),
                            userDataObject.getString("channel_key"),
                            userDataObject.getString("api_key"),
                            userDataObject.getInt("is_premium"),
                            userDataObject.getString("fb_id"),
                            userDataObject.getString("avatar"),
                            userDataObject.getString("phone_number"),
                            userDataObject.getString("category_id"),
                            userDataObject.getString("category_name"),
                            userDataObject.getString("quality_id"),
                            userDataObject.getString("quality_name"),
                            userDataObject.getInt("show_all"),
                            userDataObject.getInt("radius"),
                            userDataObject.getInt("price_range"),
                            userDataObject.getString("country_id"),
                            userDataObject.getString("country_name"),
                            userDataObject.getString("state_id"),
                            userDataObject.getString("state_name"),
                            userDataObject.getString("city_id"),
                            userDataObject.getString("city_name")
                    );

                    DatabaseHandler db = DatabaseHandler.getInstance(mContext);
                    Log.d("userDownload", "2");

                    db.updateUser(userData);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }/*end of actualDownload*/

        }.execute();

    }/*End of downloadnotificationsettings()*/


    /*********************************************************/
    public void downloadGroupsData() {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("groups_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("admin_id", loggedInUser.id);
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_GROUPS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
                    downloadedGroupsData.clear();

                    try {
                        if(DataObject.getInt("status") == 1) {
                            JSONArray groupsArr = (JSONArray) DataObject.get("items");

                            for (int i = 0; i < groupsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject groupObject = (JSONObject) groupsArr.get(i);


                                Groups receivedGroup = new Groups(groupObject.getString("id")
                                        , groupObject.getString("group_name")
                                        , groupObject.getString("admin_name")
                                        , groupObject.getString("admin_location")
                                        , groupObject.getString("admin_photo")
                                        , groupObject.getString("group_image")
                                        , groupObject.getString("created_at")
                                        , groupObject.getInt("members_count")
                                        , groupObject.getInt("products_count")
                                        , groupObject.getInt("followers_count")
                                        , groupObject.getInt("in_group")
                                        , groupObject.getInt("user_is_admin")
                                        , groupObject.getInt("is_following")
                                );


                                if (!downloadedGroupsData.contains(receivedGroup)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedGroupsData.add(receivedGroup);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }
    /**************************************************/
    public ArrayList<Groups> get_downloadedGroupsData() {
        ArrayList<Groups> groupsList = new ArrayList<Groups>();
        for (Groups selectedGroup : downloadedGroupsData) {
            if (!(groupsList.contains(selectedGroup))) {
                groupsList.add(selectedGroup);
            }
        }
        return groupsList;
    }



    /*********************************************************/
    public void create_group(final String group_name, final String group_image, final String group_type) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                //Intent intent = new Intent("profile-downloaded");
                //LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                User loggedInUser = getLoggedInUser();
                BasicHttpClient leaderboardClient = basicClient();
                ParameterMap parameters = leaderboardClient.newParams().add("group_name", group_name)
                        .add("group_image", group_image).add("is_android",""+1).add("group_type", group_type);
                leaderboardClient.setConnectionTimeout(2000);
                HttpResponse httpResponse = leaderboardClient.post(Constants.CREATE_GROUP, parameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        downloadGroupsData();
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
            }
        }.execute();
    }

    /*********************************************************/
    public void update_group(final String group_name, final String group_image, final String group_type, final String group_id) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                //Intent intent = new Intent("profile-downloaded");
                //LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                User loggedInUser = getLoggedInUser();
                BasicHttpClient leaderboardClient = basicClient();
                ParameterMap parameters = leaderboardClient.newParams().add("group_name", group_name)
                        .add("group_image", group_image).add("is_android",""+1).add("group_type", group_type).add("group_id", group_id);
                leaderboardClient.setConnectionTimeout(2000);
                HttpResponse httpResponse = leaderboardClient.post(Constants.UPDATE_GROUP, parameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        downloadGroupsData();
                    }else{
                        Toast.makeText(DataAccess.this, " "+ receivedResponse, Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
            }
        }.execute();
    }

    /*********************************************************/
    public void downloadFollowersData() {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent("followers_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            private void actualUpdate() {
                try {
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams();
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_USER_FOLLOWERS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            JSONArray followersArr = (JSONArray) DataObject.get("followers");

                            for (int i = 0; i < followersArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject followerObject = (JSONObject) followersArr.get(i);


                                Followers receivedFollower = new Followers(followerObject.getString("id")
                                        , followerObject.getString("full_name")
                                        , followerObject.getString("avatar")
                                        , followerObject.getString("location")
                                        , followerObject.getInt("products_count")
                                        , followerObject.getInt("followers_count")
                                        , followerObject.getInt("following_count")
                                        , followerObject.getBoolean("is_following") ? 1 : 0
                                        , followerObject.getInt("bids_count")
                                        , followerObject.getInt("is_premium")
                                        , followerObject.getInt("rated_by_user")
                                        , followerObject.getInt("ratings")
                                        , followerObject.getInt("is_online")
                                );


                                if (!downloadedFollowersData.contains(receivedFollower)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    downloadedFollowersData.add(receivedFollower);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
            }
        }.execute();
    }
    /**************************************************/
    public ArrayList<Followers> get_downloadedFollowersData() {
        ArrayList<Followers> followerList = new ArrayList<Followers>();
        for (Followers selectedFollower : downloadedFollowersData) {
            if (!followerList.contains(selectedFollower)) {
                followerList.add(selectedFollower);
            }
        }
        return followerList;
    }

    public int followUnfollowMethod(final String userId){
        User loggedInUser = getLoggedInUser();
        BasicHttpClient httpClientUserData = basicClient();
        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("user_id", userId);
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponse = httpClientUserData.post(Constants.FOLLOW_UNFOLLOW, httpClientUserDataParameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        status = 1;
                        downloadFollowersData();
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
        return status;
    }

    public int followUnfollowGroupMethod(final String groupId){
        User loggedInUser = getLoggedInUser();
        BasicHttpClient httpClientUserData = basicClient();
        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("group_id", groupId);
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponse = httpClientUserData.post(Constants.FOLLOW_UNFOLLOW_GROUP, httpClientUserDataParameters);
        try {
            JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
            if (receivedResponse.getInt("status") == 1) {
                status = 1;
                downloadGroupsData();
            }
        } catch (Throwable t) {
            Log.d("e", t.toString());
        }
        return status;
    }

    public int ReportProduct(final String productId, final String reportMessage){
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
               /* Intent intent = new Intent("followers_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
            }
            private void actualUpdate() {
                User loggedInUser = getLoggedInUser();
                BasicHttpClient leaderboardClient = basicClient();
                ParameterMap parameters = leaderboardClient.newParams().add("product_id", productId).add("report_text", reportMessage);
                leaderboardClient.setConnectionTimeout(2000);
                HttpResponse httpResponse = leaderboardClient.post(Constants.REPORT_PRODUCT, parameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        status = 1;
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
            }
        }.execute();
        return status;
    }

    public int DeleteGroup(final String groupId){
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
               /* Intent intent = new Intent("followers_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
            }
            private void actualUpdate() {
                User loggedInUser = getLoggedInUser();
                BasicHttpClient leaderboardClient = basicClient();
                ParameterMap parameters = leaderboardClient.newParams().add("group_id", groupId);
                leaderboardClient.setConnectionTimeout(2000);
                HttpResponse httpResponse = leaderboardClient.post(Constants.DELETE_GROUP, parameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        downloadGroupsData();
                        status = 1;
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
            }
        }.execute();
        return status;
    }


    /*********************************************************/
    public JSONArray getSearchResult(final String key, final String query) {
                try {
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("search_type", key).add("query", query);
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.SEARCH_ITEMS, httpClientUserDataParameters);
                    JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            searchResultArr = (JSONArray) DataObject.get("items");
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }

        return searchResultArr;
    }


    /*********************************************************/
    public JSONObject getUserProfileDetails(final String user_id) {
        try {
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("user_id", user_id);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_USER_PROFILE_DATA, httpClientUserDataParameters);
            JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if(DataObject.getInt("status") == 1) {
                    profileData = (JSONObject) DataObject.get("user_data");
                }
            } catch(Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return profileData;
    }


    public int BuyProduct(final String productId) {
        try {
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("product_id", productId);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.BUY_PRODUCT, httpClientUserDataParameters);
            JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if(DataObject.getInt("status") == 1) {
                    status =1;
                }
            } catch(Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return status;
    }


    public int AddRemoveSavedShapp(final String productId) {
        try {
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("product_id", productId).add("add_remove",""+1);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.ADD_REMOVE_SAVED_SHAPP, httpClientUserDataParameters);
            JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if(DataObject.getInt("status") == 1) {
                    downloadedSavedShappsData.clear();
                    status =1;
                    downloadSavedShapps();
                }
            } catch(Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return status;
    }



    public int UpdateSearchFilter(final String showAll, final String radius, final String priceRange, final String catId){
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                actualUpdate();
                return null;
            }
            protected void onPostExecute(Boolean result) {
               /* Intent intent = new Intent("followers_downloaded");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
            }
            private void actualUpdate() {
                User loggedInUser = getLoggedInUser();
                BasicHttpClient leaderboardClient = basicClient();
                ParameterMap parameters = leaderboardClient.newParams()
                        .add("show_all",showAll)
                        .add("radius",radius)
                        .add("price_range", priceRange).add("category_id",catId);
                leaderboardClient.setConnectionTimeout(2000);
                HttpResponse httpResponse = leaderboardClient.post(Constants.UPDATE_SEARCH_PROFILE, parameters);
                try {
                    JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                    if (receivedResponse.getInt("status") == 1) {
                        status = 1;
                    }
                } catch (Throwable t) {
                    Log.d("e", t.toString());
                }
            }
        }.execute();
        return status;
    }

    public List<Groups> getGroupData(final String PROFILE_ID) {
         try {
                    data.clear();
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("admin_id", PROFILE_ID);
                    httpClientUserData.setConnectionTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_GROUPS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
                    downloadedGroupsData.clear();

                    try {
                        if (DataObject.getInt("status") == 1) {
                            JSONArray groupsArr = (JSONArray) DataObject.get("items");

                            for (int i = 0; i < groupsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject groupObject = (JSONObject) groupsArr.get(i);


                                Groups receivedGroup = new Groups(groupObject.getString("id")
                                        , groupObject.getString("group_name")
                                        , groupObject.getString("admin_name")
                                        , groupObject.getString("admin_location")
                                        , groupObject.getString("admin_photo")
                                        , groupObject.getString("group_image")
                                        , groupObject.getString("created_at")
                                        , groupObject.getInt("members_count")
                                        , groupObject.getInt("products_count")
                                        , groupObject.getInt("followers_count")
                                        , groupObject.getInt("in_group")
                                        , groupObject.getInt("user_is_admin")
                                        , groupObject.getInt("is_following")
                                );


                                if (!data.contains(receivedGroup)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    data.add(receivedGroup);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch (Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }

        return data;
    }

    public List<Products> getProductData(final String query, final String id) {
        try {
            getProductData.clear();
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                    .add("user_id", id)
                    .add("product_type", query);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_MY_PRODUCTS, httpClientUserDataParameters);
            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if (DataObject.getInt("status") == 1) {
                    JSONArray ProductsArr = (JSONArray) DataObject.get("products");

                    for (int i = 0; i < ProductsArr.length(); i++) {
                        //Log.d("collections.length()", "" + collections.length());
                        JSONObject productObject = (JSONObject) ProductsArr.get(i);
                        ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                        if (productObject.has("images")) {
                            JSONArray productImages = (JSONArray) productObject.get("images");
                            Log.d("productImages", "" + productImages.toString());
                            for (int j = 0; j < productImages.length(); j++) {
                                String receivedProductImage = productImages.getString(j);
                                ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                imagesData.add(receivedImagesData);
                            }
                        }

                        Products receivedProduct = new Products(productObject.getString("id")
                                , productObject.getString("product_name")
                                , productObject.getString("uploaded_by_name")
                                , productObject.getString("uploaded_by_id")
                                , productObject.getString("uploaded_in")
                                , productObject.getString("uploaded_by_image")
                                , productObject.getInt("user_is_premium")
                                , productObject.getString("product_image")
                                , productObject.getString("product_description")
                                , productObject.getBoolean("bill_included") ? 1 : 0
                                , productObject.getInt("min_bid_price")
                                , productObject.getInt("buy_now_price")
                                , productObject.getInt("latest_bid")
                                , productObject.getInt("views_count")
                                , productObject.getInt("bids_count")
                                , productObject.getInt("comments_count")
                                , productObject.getInt("my_bid")
                                , productObject.getInt("bid_closed")
                                , productObject.getString("bid_start")
                                , productObject.getString("bid_end")
                                , productObject.getInt("rated_by_user")
                                , productObject.getInt("ratings")
                                , imagesData
                                , productObject.getInt("is_sold")
                                , productObject.getInt("sold_to_user")
                                , productObject.getString("latitude")
                                , productObject.getString("longitude"));


                        if (!getProductData.contains(receivedProduct)) {
                            //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                            getProductData.add(receivedProduct);
                        } else {
                            //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                        }

                    }
                }
            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return getProductData;
    }

    /*********************************************************/
    public JSONArray getGroupItemDetails(final String group_id, final String item_type) {
        try {
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("group_id", group_id).add("item_type", item_type);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_GROUP_ITEM, httpClientUserDataParameters);
            JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if(DataObject.getInt("status") == 1) {
                    groupItemDetails = (JSONArray) DataObject.get("items");
                }
            } catch(Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return groupItemDetails;
    }

    public ArrayList<Products> discoverData(final int PAGE_NUMBER) {
                try {
                    User loggedInUser = getLoggedInUser();
                    BasicHttpClient httpClientUserData = basicClient();
                    ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                            .add("page_number", String.valueOf(PAGE_NUMBER));
                    httpClientUserData.setConnectionTimeout(60000);
                    httpClientUserData.setReadTimeout(60000);
                    HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_DISCOVER_PRODUCTS, httpClientUserDataParameters);
                    final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                    try {
                        if(DataObject.getInt("status") == 1) {
                            downloadedDiscoverData.remove(downloadedDiscoverData);
                            downloadedDiscoverData.clear();
                            JSONArray ProductsArr = (JSONArray) DataObject.get("products");

                            for (int i = 0; i < ProductsArr.length(); i++) {
                                //Log.d("collections.length()", "" + collections.length());
                                JSONObject productObject = (JSONObject) ProductsArr.get(i);
                                ArrayList<ProductImages> imagesData = new ArrayList<ProductImages>();

                                if (productObject.has("images")) {
                                    JSONArray productImages = (JSONArray) productObject.get("images");
                                    Log.d("productImages", "" + productImages.toString());
                                    for (int j = 0; j < productImages.length(); j++) {
                                        String receivedProductImage = productImages.getString(j);
                                        ProductImages receivedImagesData = new ProductImages(receivedProductImage);
                                        imagesData.add(receivedImagesData);
                                    }
                                }

                                Products receivedProduct = new Products(productObject.getString("id")
                                        , productObject.getString("product_name")
                                        , productObject.getString("uploaded_by_name")
                                        , productObject.getString("uploaded_by_id")
                                        , productObject.getString("uploaded_in")
                                        , productObject.getString("uploaded_by_image")
                                        , productObject.getInt("user_is_premium")
                                        , productObject.getString("product_image")
                                        , productObject.getString("product_description")
                                        , productObject.getBoolean("bill_included") ? 1 : 0
                                        , productObject.getInt("min_bid_price")
                                        , productObject.getInt("buy_now_price")
                                        , productObject.getInt("latest_bid")
                                        , productObject.getInt("views_count")
                                        , productObject.getInt("bids_count")
                                        , productObject.getInt("comments_count")
                                        , productObject.getInt("my_bid")
                                        , productObject.getInt("bid_closed")
                                        , productObject.getString("bid_start")
                                        , productObject.getString("bid_end")
                                        , productObject.getInt("rated_by_user")
                                        , productObject.getInt("ratings")
                                        , imagesData
                                        , productObject.getInt("is_sold")
                                        , productObject.getInt("sold_to_user")
                                        , productObject.getString("latitude")
                                        , productObject.getString("longitude"));


                                if (!discoverData.contains(receivedProduct)) {
                                    //Log.d("TRUE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                    discoverData.add(receivedProduct);
                                } else {
                                    //Log.d("FALSE", i + " == !downloadedCollectionData.contains(receivedCollection)");
                                }

                            }
                        }
                    } catch(Exception e) {
                        Log.w("Error", e.getMessage());
                    }


                } catch (Throwable t) {
                    //showMessageWithToast("Your email and password combination do not match");
                }
        return discoverData;
    }

    public JSONArray getChatList(final String key) {
        try {
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("item_type", key);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_CHAT_LIST, httpClientUserDataParameters);
            JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if(DataObject.getInt("status") == 1) {
                    chatData = (JSONArray) DataObject.get("chat_users");
                }
            } catch(Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return chatData;
    }

    public int forgetPassword(final String email_id) {
        try {
            User loggedInUser = getLoggedInUser();
            BasicHttpClient httpClientUserData = basicClient();
            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("email_id", email_id);
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.FORGET_PASSWORD, httpClientUserDataParameters);
            JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

            try {
                if(DataObject.getInt("status") == 1) {
                  status = 1;
                }
            } catch(Exception e) {
                Log.w("Error", e.getMessage());
            }


        } catch (Throwable t) {
            //showMessageWithToast("Your email and password combination do not match");
        }

        return status;
    }

}
