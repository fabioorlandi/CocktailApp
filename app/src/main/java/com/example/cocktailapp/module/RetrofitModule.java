package com.example.cocktailapp.module;

import com.example.cocktailapp.service.CocktailDBRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import dagger.Module;
import dagger.Provides;

@Module
public class RetrofitModule {
    private static final String BASE_URL = "www.thecocktaildb.com/api/json/v1/1/";

    @Provides
    @Singleton
    public CocktailDBRepository getCocktailDBService(Retrofit getClient) {
        return getClient.create(CocktailDBRepository.class);
    }

    @Provides
    public Retrofit getClient(GsonConverterFactory gsonConverterFactory, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    @Provides
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    @Provides
    public GsonConverterFactory gsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }
}
