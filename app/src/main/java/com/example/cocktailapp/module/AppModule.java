package com.example.cocktailapp.module;

import android.content.Context;
import com.example.cocktailapp.app.CocktailApp;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {
    CocktailApp mApplication;

    public AppModule(CocktailApp application) {
        mApplication = application;
    }

    @Provides
    public Context getAppContext(){
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    CocktailApp providesApplication() {
        return mApplication;
    }
}
