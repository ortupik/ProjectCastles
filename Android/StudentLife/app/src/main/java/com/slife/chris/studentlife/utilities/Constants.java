package com.slife.chris.studentlife.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Locale;


public class Constants {

    public static final String CHAT_SERVER_URL = "http://192.168.43.233:8080";//http://testcastle.herokuapp.comhttp://192.168.43.233:8080//10.0.2.2:8080
    public static final String SERVER_URL = "http://192.168.43.233:8000";//http://testcastle.herokuapp.comhttp://192.168.43.233:8080//10.0.2.2:8080

    public static final String IMG_URL = Constants.CHAT_SERVER_URL + "/profile_images/";
    public static final String UPLOAD_URL = Constants.CHAT_SERVER_URL + "/android_uploads/";
    public static final String HOSTEL_URL = Constants.CHAT_SERVER_URL + "/image_uploads/hostels/";

    private static  Typeface typeface, robotoMedium, robotoLight, robotoBold;

    public  static Typeface getTypeFace(Context context) {
        AssetManager assetManager = context.getAssets();
        typeface = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", "materialdrawerfont-font-v5.0.0.ttf"));
        return typeface;
    }
    public static  Typeface getRobotoMedium(Context context) {
        AssetManager assetManager = context.getAssets();
        robotoMedium = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", "Roboto-Medium.ttf"));
        return robotoMedium;
    }
    public static  Typeface getRobotoLight(Context context) {
        AssetManager assetManager = context.getAssets();
        robotoLight = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", "Roboto-Light.ttf"));
        return robotoLight;
    }
    public static Typeface getRobotoBold(Context context) {
        AssetManager assetManager = context.getAssets();
        robotoBold = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", "Roboto-Bold.ttf"));
        return robotoBold;
    }
}
