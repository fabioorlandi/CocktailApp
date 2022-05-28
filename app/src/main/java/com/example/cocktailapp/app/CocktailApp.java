package com.example.cocktailapp.app;

import android.app.Application;
import android.content.Context;

import com.example.cocktailapp.module.AppComponent;
import com.example.cocktailapp.module.AppModule;
import com.example.cocktailapp.module.RetrofitModule;
import com.example.cocktailapp.module.RoomDBModule;

public class CocktailApp extends Application {
    private static Context context;
    private static CocktailApp myApplication;

    private AppComponent mainActivityViewModelComponent;

    public void onCreate() {
        super.onCreate();
        myApplication = this;
        CocktailApp.context = getApplicationContext();

        initDIComponents();
    }

    private void initDIComponents(){
        mainActivityViewModelComponent = DaggerAppComponent.builder()
                .retrofitModule(new RetrofitModule())
                .appModule(new AppModule(this))
                .roomDbModule(new RoomDBModule(this))
                .build();
    }

    public AppComponent getMainActivityViewModelComponent() {
        return mainActivityViewModelComponent;
    }

    public static CocktailApp getMyApplication() {
        return myApplication;
    }

    public static Context getAppContext() {
        return CocktailApp.context;
    }
}
