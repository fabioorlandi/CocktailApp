package com.example.cocktailapp.model.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppConnection {
    public static AppConnectionStatus getConnectionState(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return AppConnectionStatus.ONLINE;
                } else return AppConnectionStatus.OFFLINE;
            }
        }
        return AppConnectionStatus.OFFLINE;
    }
}
