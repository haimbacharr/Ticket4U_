package com.example.ticket4u.Utils;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Constant {
        public static int INDEX=-1;
    public static boolean getAdminLoginStatus(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("Admin", false);
    }

    public static void setAdminLoginStatus(Context context , boolean s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("Admin", s).commit();
    }
    public static boolean getUserLoginStatus(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("User", false);
    }

    public static void setUserLoginStatus(Context context , boolean s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("User", s).commit();
    }
    public static String getUsername(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("name","");
    }
    public static void setUsername(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("name", s).commit();
    }

    public static String getUserEmail(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("email","");
    }

    public static void setUserEmail(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("email", s).commit();
    }
    public static String getUserId(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("id","");
    }

    public static void setUserId(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("id", s).commit();
    }
    public static String getUserNumber(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("number","");
    }

    public static void setUserNumber(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("number", s).commit();
    }
    public static String getSort(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("sort","name");
    }

    public static void setSort(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("sort", s).commit();
    }
    public static String getUserCity(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("city","");
    }

    public static void setUserCity(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("city", s).commit();
    }

    public static String getUserInterest(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("Category","");
    }

    public static void setUserInterest(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("Category", s).commit();
    }
    public static String getUserLatitude(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("Latitude","");
    }

    public static double getKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;
        return round(6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1)),2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void setUserLatitude(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("Latitude", s).commit();
    }
    public static String getUserLongitude(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("Longitude","");
    }

    public static void setUserLongitude(Context context , String s){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("Longitude", s).commit();
    }

    public static void ShowMessageDialogWithOkBtn(Context context, String message,
                                                  DialogInterface.OnClickListener positiveBtnListner){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",positiveBtnListner);

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
