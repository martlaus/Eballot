package me.martl.e_ballot;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Mart on 9.05.2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "0SH1e22UbDC0ak9X1YLYUbNFF6EwOSaPUXh58lW2", "fe9NYmkiX3A47j9oFG6tidqYbc6DdY6mJc3VNteG");

    }
}
