package com.example.guest.DMVBuddy.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guest on 7/15/16.
 */
public class FormatDate {

    public static String formatDate(String date){
        String formattedDate = date;
        try{
            SimpleDateFormat sdfSource = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy");
            Date newdate = null;
            try {
                newdate = sdfSource.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(newdate);
            cal2.setTime(new Date());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if(sameDay){
                formattedDate = new SimpleDateFormat("h:m a").format(newdate);
            }else{
                formattedDate = new SimpleDateFormat("MMMM d, h:m a").format(newdate);
            }

        }catch(NullPointerException e){
           e.printStackTrace();
        }

        return formattedDate;
    }
}
