package com.eysa.shapp;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES10;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;


public class ImageLoader {

    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    int SRC_FLAG=0, VIEW_WIDTH=0;
    ExecutorService executorService;
    Handler handler = new Handler();
    Bitmap bitmap;

    /* *******************
    EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
    int[] vers = new int[2];
    EGL14.eglInitialize(dpy, vers, 0, vers, 1);

    int[] configAttr = {
            EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
            EGL14.EGL_LEVEL, 0,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_NONE
    };
    EGLConfig[] configs = new EGLConfig[1];
    int[] numConfig = new int[1];
    EGL14.eglChooseConfig(dpy, configAttr, 0, configs, 0, 1, numConfig, 0);
    if (numConfig[0] == 0) {
        // TROUBLE! No config found.
    }
    EGLConfig config = configs[0];

    int[] surfAttr = {
            EGL14.EGL_WIDTH, 64,
            EGL14.EGL_HEIGHT, 64,
            EGL14.EGL_NONE
    };
    EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

    int[] ctxAttrib = {
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
    };
    EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);

    EGL14.eglMakeCurrent(dpy, surf, surf, ctx);

    int[] maxSize = new int[1];
    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);

    /*EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                     EGL14.EGL_NO_CONTEXT);
EGL14.eglDestroySurface(dpy, surf);
EGL14.eglDestroyContext(dpy, ctx);
EGL14.eglTerminate(dpy);
    ******************* */
    private static ImageLoader objLogger;

    public static ImageLoader getInstance(Context context, Activity act) {
        if (objLogger == null) {
            objLogger = new ImageLoader(context, act);
        }
        return objLogger;
    }

    private Context mContext;
    private Activity mActivity;

    public ImageLoader(Context context, Activity act){
        System.gc();
        mContext = context;
        mActivity = act;
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }

    public void DisplayImage(String urlRaw, final ImageView imageView)
    {

        String url = urlRaw;

        if(Constants.API_STAGE == 0)
        {
            url = Constants.BASE_URL+""+urlRaw;
        }
        System.out.println("__________________-URLout_______________"+url);
        imageViews.put(imageView, url);
        bitmap=memoryCache.get(url);
        System.out.println("__________________-bitmap_______________"+bitmap);
        if(bitmap!=null) {
            //getActivity().runOnUiThread(new Runnable() {

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    imageView.setImageBitmap(bitmap);
                }
            });
            //imageView.setImageBitmap(bitmap);
        }
        else
        {
            queuePhoto(url, imageView);
        }
    }


    public void DisplayImage(String urlRaw, final ImageView imageView, int src_flag, int view_width)
    {
        /* src_flag:
        0: default none.
        1: library page coverart;
        2: ?
        */
        SRC_FLAG = src_flag;
        VIEW_WIDTH = view_width;
        Log.d("DisplayImage", SRC_FLAG+",  "+VIEW_WIDTH);


        String url = urlRaw;

        if(Constants.API_STAGE == 0)
        {
            url = Constants.BASE_URL+""+urlRaw;
        }
        System.out.println("__________________-URLout_______________" + url);
        imageViews.put(imageView, url);
        bitmap=memoryCache.get(url);
        System.out.println("__________________-bitmap_______________"+bitmap);

        if(bitmap!=null) {
            if(SRC_FLAG==1){
                bitmap = get_library_coverart_bitmap(bitmap);
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    imageView.setImageBitmap(bitmap);
                }
            });
            //imageView.setImageBitmap(bitmap);
        }
        else
        {
            queuePhoto(url, imageView);
        }
    }


    public Bitmap get_library_coverart_bitmap(Bitmap bitmap){
        int pixels = 10;

        Bitmap output = bitmap;

        try {
            pixels = (20 * bitmap.getWidth() / VIEW_WIDTH);
            Log.d("get_library_coverart", pixels+"");

            int EXP_HEIGHT = bitmap.getHeight();
            int EXP_WIDTH = bitmap.getWidth();

            /*if(EXP_WIDTH > VIEW_WIDTH){
                double width_ratio = VIEW_WIDTH / EXP_WIDTH;
                double EXP_HGT = EXP_HEIGHT * width_ratio;

                EXP_HEIGHT = (int) Math.round(EXP_HGT);
                EXP_WIDTH = VIEW_WIDTH;
            }*/



            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            //Canvas canvas = new Canvas(bitmap);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, EXP_WIDTH, EXP_HEIGHT);
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;
            final Rect topRightRect = new Rect(EXP_WIDTH / 2, 0, EXP_HEIGHT, EXP_HEIGHT / 2);
            final Rect bottomRect = new Rect(0, EXP_HEIGHT / 2, EXP_WIDTH, EXP_HEIGHT);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            // Fill in upper right corner
            //canvas.drawRect(topRightRect, paint);
            // Fill in bottom corners
            canvas.drawRect(bottomRect, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return output;
        //return bitmap;

    } /* end of get_library_coverart_bitmap(); */



    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }


    public Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;

        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.

            Log.i("glinfo", "Max texture size = " + getMaxTextureSize() );

            int REQUIRED_SIZE= getMaxTextureSize();
            if(SRC_FLAG==1){
                REQUIRED_SIZE= 720;
            }

            Integer EXP_HEIGHT, EXP_WIDTH;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp<REQUIRED_SIZE && height_tmp<REQUIRED_SIZE){
                    break;
                }
                else{

                    width_tmp/=2;
                    height_tmp/=2;
                    scale*=2;

                }

            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

        } catch (FileNotFoundException e) {}
        return null;
    }


    public static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    }





    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u;
            imageView=i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad)) {
                return;
            }
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);

            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null) {

                if(SRC_FLAG==1){
                    bitmap = get_library_coverart_bitmap(bitmap);
                }
                photoToLoad.imageView.setImageBitmap(bitmap);

            } else {

            }


        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
