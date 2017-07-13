package com.creadigol.drivehere;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.LocationTracker;
import com.creadigol.drivehere.util.PreferenceSettings;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by ADMIN on 22-05-2017.
 */

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();
    public static final String APP_TYPE = "drivehere";

    private static MyApplication singleton;
    private static ImageLoader mImageLoader;
    private LocationTracker locationTracker;
    private PreferenceSettings mPreferenceSettings;
    private RequestQueue mRequestQueue;
    private int MY_SOCKET_TIMEOUT_MS = 20000;

    public static synchronized MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        mPreferenceSettings = getPreferenceSettings();
        locationTracker = new LocationTracker(this);
    }

    public LocationTracker getLocationTracker() {
        if (locationTracker == null) {
            locationTracker = new LocationTracker(this);
        }
        return locationTracker;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            // mRequestQueue = Volley.newRequestQueue
            //   (this, new OkHttpStack(new OkHttpClient()));
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        req.setShouldCache(false);

        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            // UNIVERSAL IMAGE LOADER SETUP
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getApplicationContext())
                    .defaultDisplayImageOptions(getDefaultOptions())
                    .memoryCache(new WeakMemoryCache())
                    .diskCacheSize(100 * 1024 * 1024).build();

            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(config);
            // END - UNIVERSAL IMAGE LOADER SETUP
        }
        return mImageLoader;
    }

    public DisplayImageOptions getDefaultOptions() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        return defaultOptions;
    }

    public PreferenceSettings getPreferenceSettings() {
        if (mPreferenceSettings == null) {
            mPreferenceSettings = new PreferenceSettings(getApplicationContext());
        }
        return mPreferenceSettings;
    }

    public boolean needPermissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            return true;
        }
        return false;
    }

}
