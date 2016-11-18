package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ProductDetailsActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener{
    ArrayList<Bitmap> mResources = new ArrayList<Bitmap>();
    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;
    ImageLoader_fragment imageLoader_fragment;
    int status = 0;
    ViewPager mViewPager;
    private CustomPagerAdapter mAdapter;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    WindowManager wm;
    Display display;
    Point size;
    int width, height, PRO_BID_FLAG=0;

    String SOURCE_PAGE, PRODUCT_ID;
    TextView product_name_text, productdetails_header_text, bid_btn_text, member_location_text, product_details_text
            , username_text, report_product_text;
    ImageView product_image, product_share_image, productdetails_page_close_image, productdetails_star_btn, comment_button;
    TableLayout profile_details;
    CircleImageView product_memberImage;

    Button buy_button, min_bid_button, bid_button;


    Products downloadedProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_view);

        imageLoader = new ImageLoader(getApplicationContext(), ProductDetailsActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        imageLoader_fragment = new ImageLoader_fragment(this);
        Integer EXP_WIDTH, EXP_HEIGHT;
        double ORG_THUMB_HEIGHT = 866.0;
        double ORG_THUMB_WIDTH = 1536.0;
        Log.d("ORG width and height:", ORG_THUMB_HEIGHT + "x" + ORG_THUMB_WIDTH);
        double width_ratio = width / ORG_THUMB_WIDTH;
        double EXP_HGT = ORG_THUMB_HEIGHT * width_ratio;

        EXP_HEIGHT = (int) Math.round(EXP_HGT);
        EXP_WIDTH = width;
        Log.d("width and height:", EXP_HEIGHT + "x" + EXP_WIDTH);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();

        product_share_image = (ImageView) this.findViewById(R.id.product_share_image);
        comment_button = (ImageView) this.findViewById(R.id.comment_button);
        product_name_text = (TextView) this.findViewById(R.id.product_name_text);
        productdetails_header_text = (TextView) this.findViewById(R.id.productdetails_header_text);
        buy_button = (Button) this.findViewById(R.id.buy_button);
        min_bid_button = (Button) this.findViewById(R.id.min_bid_button);
        bid_button = (Button) this.findViewById(R.id.bid_button);
        member_location_text = (TextView) this.findViewById(R.id.member_location_text);
        report_product_text = (TextView) this.findViewById(R.id.report_product_text);
        product_details_text = (TextView) this.findViewById(R.id.product_details_text);
        username_text = (TextView) this.findViewById(R.id.username_text);
        product_memberImage = (CircleImageView) this.findViewById(R.id.memberImage);
        productdetails_page_close_image = (ImageView) this.findViewById(R.id.productdetails_page_close_image);
        productdetails_star_btn = (ImageView) this.findViewById(R.id.productdetails_star_btn);
        product_image = (ImageView) this.findViewById(R.id.product_image);
        profile_details = (TableLayout) this.findViewById(R.id.profile_details);

        productdetails_header_text.setText(R.string.my_product);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            SOURCE_PAGE = extras.getString("SOURCE_PAGE");
            PRODUCT_ID = extras.getString("product_id");
        }

        if(!PRODUCT_ID.equals("")){
            dataAccess.download_productDetails(PRODUCT_ID);
        }

        report_product_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivity.this);
                alertDialog.setTitle("Shapp App");

                // Setting Dialog Message
                alertDialog.setMessage("Reason to report");
                final EditText input = new EditText(ProductDetailsActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Report",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                    status = dataAccess.ReportProduct(PRODUCT_ID, input.getText().toString());
                                if (status == 1){
                                    Toast.makeText(getApplicationContext(), "Product reported to admin", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

        productdetails_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("product_downloaded"));


    } /* end of ProductDetailsActivity() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ProductDetailsActivity.this != null) {
                DisplayProductDetails();
            }
        }
    };



    public void DisplayProductDetails() {

        downloadedProduct = dataAccess.get_downloadedProduct();

        if(downloadedProduct!=null) {
            try {

                productdetails_header_text.setText(downloadedProduct.product_name);
                product_name_text.setText(downloadedProduct.product_name);
                imageLoader.DisplayImage(downloadedProduct.product_image, product_image);
                imageLoader.DisplayImage(downloadedProduct.uploaded_by_image, product_memberImage);
                List<ProductImages> imageData = downloadedProduct.images;
                for(ProductImages image : imageData){
                   Bitmap bim = imageLoader.getBitmap("http://www.shapp.dk/"+image.image_path);
                    mResources.add(bim);
            }
                mViewPager = (ViewPager) findViewById(R.id.viewpager);
                pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
                mAdapter = new CustomPagerAdapter(this, mResources);
                mViewPager.setAdapter(mAdapter);
                mViewPager.setCurrentItem(0);
                mViewPager.setOnPageChangeListener(this);
                setPageViewIndicator();

                //  comment_button.setText(downloadedProduct.comments_count+ " Comments");
                min_bid_button.setText("Min Bid " + downloadedProduct.min_bid_price + " Kr");
                buy_button.setText("Buy " + downloadedProduct.buy_now_price + " Kr");
                product_details_text.setText(downloadedProduct.product_description);
                username_text.setText(downloadedProduct.uploaded_by_name);
                member_location_text.setText(downloadedProduct.uploaded_in);


                profile_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent GroupDetailsClass = new Intent(ProductDetailsActivity.this,ProfileDetailsActivity.class);
                            GroupDetailsClass.putExtra("profile_id", downloadedProduct.uploaded_by_id);
                            GroupDetailsClass.putExtra("admin_name", downloadedProduct.uploaded_by_name);
                            GroupDetailsClass.putExtra("admin_photo", downloadedProduct.uploaded_by_image);
                            GroupDetailsClass.putExtra("profile_location", downloadedProduct.uploaded_in);
                        startActivity(GroupDetailsClass);
                    }
                });

                    if (downloadedProduct.bid_closed == 1) {
                        bid_button.setText("Bid Closed");
                        PRO_BID_FLAG = 0;
                    } else {
                        bid_button.setText("Bid");
                        PRO_BID_FLAG = 1;
                        bid_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent biddingClass = new Intent(ProductDetailsActivity.this, BiddingActivity.class);
                                biddingClass.putExtra("product_id",downloadedProduct.id);
                                biddingClass.putExtra("min_bid_amt", downloadedProduct.min_bid_price);
                                biddingClass.putExtra("latest_bid_amt", downloadedProduct.latest_bid);
                                biddingClass.putExtra("pro_image", downloadedProduct.product_image);
                                biddingClass.putExtra("product_name", downloadedProduct.product_name);
                                biddingClass.putExtra("buy_now", downloadedProduct.buy_now_price);
                                startActivity(biddingClass);
                            }
                        });
                    }

                productdetails_star_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent starratingClass = new Intent(getApplicationContext(), StarRatingActivity.class);
                        starratingClass.putExtra("product_id", downloadedProduct.id);
                        starratingClass.putExtra("min_bid_amt", downloadedProduct.min_bid_price);
                        starratingClass.putExtra("latest_bid_amt", downloadedProduct.latest_bid);
                        starratingClass.putExtra("pro_image", downloadedProduct.product_image);
                        starratingClass.putExtra("product_name", downloadedProduct.product_name);
                        starratingClass.putExtra("rated_by_user", downloadedProduct.rated_by_user);
                        starratingClass.putExtra("ratings", downloadedProduct.ratings);
                        startActivity(starratingClass);

                    }
                });

                comment_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsClass = new Intent(getApplicationContext(), CommentsActivity.class);
                        commentsClass.putExtra("product_id", downloadedProduct.id);
                        startActivity(commentsClass);

                    }
                });


                bid_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (PRO_BID_FLAG == 1) {
                            Intent biddingClass = new Intent(getApplicationContext(), BiddingActivity.class);
                            biddingClass.putExtra("product_id", downloadedProduct.id);
                            biddingClass.putExtra("min_bid_amt", downloadedProduct.min_bid_price);
                            biddingClass.putExtra("latest_bid_amt", downloadedProduct.latest_bid);
                            biddingClass.putExtra("pro_image", downloadedProduct.product_image);
                            biddingClass.putExtra("product_name", downloadedProduct.product_name);
                            startActivity(biddingClass);
                        }


                    }
                });


                product_share_image.setClickable(true);
                product_share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
                        SaveImage(img);
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Shapp");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("/storage/emulated/0/saved_images/image.jpg"));
                        sharingIntent.setType("image/jpeg");
                        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });


            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private void SaveImage(Bitmap finalBitmap) {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();
            String fname = "image.jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.e("ExternalStorage", "Scanned " + path + ":");
                            Log.e("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {

    }


    public class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<Bitmap>  mResources;

        public CustomPagerAdapter(Context context,ArrayList<Bitmap>  resources) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mResources = resources;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View itemView = mLayoutInflater.inflate(R.layout.walk_through_swip_layout,container,false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageBitmap(mResources.get(position));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
           /* LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(950, 950);
            imageView.setLayoutParams(layoutParams);*/
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void setPageViewIndicator() {

        Log.d("###setPageViewIndicator", " : called");
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            final int presentPosition = i;
            dots[presentPosition].setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mViewPager.setCurrentItem(presentPosition);
                    return true;
                }

            });


            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.item_selected));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Log.d("###onPageSelected, pos ", String.valueOf(position));
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_unselected));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.item_selected));

        if (position + 1 == dotsCount) {

        } else {

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
