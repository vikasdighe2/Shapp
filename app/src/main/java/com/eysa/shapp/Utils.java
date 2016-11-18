package com.eysa.shapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public long getTimeInMiliSeconds(String endTime) throws ParseException {
        long timeInMiliseconds = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date now = new Date();
        String strDate = df.format(now);
        Date startDate = (Date)df.parse(strDate);
        Date endDate = (Date)df.parse(endTime);
        long stime = endDate.getTime();
        long etime = startDate.getTime();
        timeInMiliseconds = endDate.getTime() - startDate.getTime();

        return timeInMiliseconds;
    }

    public long getTimeInMiliSecondsForChat(String chatTime) throws ParseException {
        long timeInMiliseconds = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date now = new Date();
        String strDate = df.format(now);
        Date currentTime = (Date)df.parse(strDate);
        Date chatEndTime = (Date)df.parse(chatTime);
        timeInMiliseconds = currentTime.getTime() - chatEndTime.getTime();
        return timeInMiliseconds;
    }



}
