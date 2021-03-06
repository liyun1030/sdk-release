package com.mobileapptracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

class MATDeferredDplinkr {
    private String advertiserId;
    private String conversionKey;
    private String packageName;
    private String googleAdvertisingId;
    private int isLATEnabled;
    private String androidId;
    private String userAgent;
    private MATResponse delegate;
    
    private static volatile MATDeferredDplinkr dplinkr;
    
    private MATDeferredDplinkr() {
        advertiserId = null;
        conversionKey = null;
        packageName = null;
        googleAdvertisingId = null;
        isLATEnabled = 0;
        androidId = null;
        userAgent = null;
        delegate = null;
    }
    
    public static synchronized MATDeferredDplinkr initialize(String advertiserId, String conversionKey, String packageName) {
        dplinkr = new MATDeferredDplinkr();
        dplinkr.advertiserId = advertiserId;
        dplinkr.conversionKey = conversionKey;
        dplinkr.packageName = packageName;
        return dplinkr;
    }
    
    public static synchronized MATDeferredDplinkr getInstance() {
        return dplinkr;
    }
    
    public void setAdvertiserId(String advertiserId) {
        dplinkr.advertiserId = advertiserId;
    }
    
    public String getAdvertiserId() {
        return dplinkr.advertiserId;
    }
    
    public void setConversionKey(String conversionKey) {
        dplinkr.conversionKey = conversionKey;
    }
    
    public String getConversionKey() {
        return dplinkr.conversionKey;
    }
    
    public void setPackageName(String packageName) {
        dplinkr.packageName = packageName;
    }
    
    public String getPackageName() {
        return dplinkr.packageName;
    }
    
    public void setUserAgent(String userAgent) {
        dplinkr.userAgent = userAgent;
    }
    
    public String getUserAgent() {
        return dplinkr.userAgent;
    }
    
    public void setGoogleAdvertisingId(String googleAdvertisingId, int isLATEnabled) {
        dplinkr.googleAdvertisingId = googleAdvertisingId;
        dplinkr.isLATEnabled = isLATEnabled;
    }
    
    public String getGoogleAdvertisingId() {
        return dplinkr.googleAdvertisingId;
    }
    
    public int getGoogleAdTrackingLimited() {
        return dplinkr.isLATEnabled;
    }
    
    public void setAndroidId(String androidId) {
        dplinkr.androidId = androidId;
    }
    
    public String getAndroidId() {
        return dplinkr.androidId;
    }
    
    public void setDelegate(MATResponse response) {
        dplinkr.delegate = response;
    }
    
    public MATResponse getDelegate() {
        return dplinkr.delegate;
    }
    
    public String checkForDeferredDeeplink(Context context, MATUrlRequester urlRequester, int timeout) {
        String deeplink = "";
        // If advertiser ID, conversion key, or package name were not set, return
        if (dplinkr.advertiserId == null || dplinkr.conversionKey == null || dplinkr.packageName == null) {
            return deeplink;
        }

        // If no device identifiers collected, return
        if (dplinkr.googleAdvertisingId == null && dplinkr.androidId == null) {
            return deeplink;
        }

        // Query for deeplink url and open
        try {
            deeplink = urlRequester.requestDeeplink(dplinkr, timeout);
            if (deeplink.length() != 0) {
                // Notify delegate of deeplink url
                if (delegate != null) {
                    delegate.didReceiveDeeplink(deeplink);
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(deeplink));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (Exception e) {
        }
        return deeplink;
    }
}
