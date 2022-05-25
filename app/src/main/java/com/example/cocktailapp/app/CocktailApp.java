package com.example.cocktailapp.app;

import android.app.Application;
import android.content.Context;

public class CocktailApp extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        CocktailApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return CocktailApp.context;
    }
}
