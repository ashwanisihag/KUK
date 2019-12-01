package com.track24x7.kuk.util;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Map;

public class UtilFunction {
    public static String getYear(String dt){
        try{
            String date=dt.split("T")[0];
            return date.split("-")[0];
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    public static void printAllValues(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // do stuff
            Log.d(TagUtils.getTag(), key + " : " + value);
        }
    }

    public static double[] getLocation(Context context) {
        GPSTracker gps;
        gps = new GPSTracker(context);
        double latitude = 0.00;
        double longitude = 0.00;

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Log.d(TagUtils.getTag(), "location:-latitude:-" + latitude);
            Log.d(TagUtils.getTag(), "location:-longitude:-" + longitude);

//            Pref.SetStringPref(context, StringUtils.CURRENT_LATITUDE, String.valueOf(latitude));
//            Pref.SetStringPref(context, StringUtils.CURRENT_LONGITUDE, String.valueOf(longitude));
        } else {
//            gps.showSettingsAlert();
        }

        double[] loc = new double[]{latitude, longitude};
        return loc;
    }

}
