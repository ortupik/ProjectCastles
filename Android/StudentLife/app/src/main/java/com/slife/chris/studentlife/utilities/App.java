package com.slife.chris.studentlife.utilities;

import android.app.Application;

/**
 * Created by Chris on 11/20/2016.
 */
public class App {
    private static Application application ;

    public App(Application application){
         this.application = application;
    }
    public static Application getApplication() {
        return application;
    }
}
