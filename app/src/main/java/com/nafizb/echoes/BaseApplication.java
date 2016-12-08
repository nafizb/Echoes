package com.nafizb.echoes;

/**
 * Created by Nafiz on 22.08.2016.
 */

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nafizb.echoes.activities.NavigationActivity;
import com.nafizb.echoes.restful.APIService;
import com.nafizb.echoes.restful.ServiceGenerator;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class BaseApplication extends Application {
    private Tracker mTracker;
    private APIService restService;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public APIService getRestService() {
        if(restService == null) {
            restService = ServiceGenerator.createService(APIService.class);
        }

        return restService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
}
