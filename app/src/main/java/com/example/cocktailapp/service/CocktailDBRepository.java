package com.example.cocktailapp.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.cocktailapp.app.CocktailApp;
import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.surrogate.CocktailSurrogate;
import com.example.cocktailapp.service.retrofit.CocktailDBRetrofitService;
import com.example.cocktailapp.service.room.CocktailDBRoomDatabaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CocktailDBRepository {
    private CocktailDBRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("www.thecocktaildb.com/api/json/v1/1/")
                .build();
        retrofitService = retrofit.create(CocktailDBRetrofitService.class);

        roomDatabaseService = Room.databaseBuilder(CocktailApp.getAppContext(), CocktailDBRoomDatabaseService.class, "cocktail_database")
                .build();
    }

    private CocktailDBRetrofitService retrofitService;
    private CocktailDBRoomDatabaseService roomDatabaseService;

    private static CocktailDBRepository repository;

    public CocktailDBRepository getInstance() {
        if (repository == null)
            repository = new CocktailDBRepository();

        return repository;
    }

    public List<Cocktail> getAllCocktails(Boolean hasNetwork) {
        if (hasNetwork) {
            List<Cocktail> cocktails = new ArrayList<>();

            for (char letter = 'a'; letter <= 'z'; letter++) {
                retrofitService.getCocktailsByFirstLetter(Character.toString(letter)).enqueue(new Callback<List<CocktailSurrogate>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<CocktailSurrogate>> call, @NonNull Response<List<CocktailSurrogate>> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            for (CocktailSurrogate surrogate : response.body()){
                                cocktails.add(surrogate.toCocktail());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<CocktailSurrogate>> call, @NonNull Throwable t) {
                        Log.d("API_ERROR", "Error fetching data", t);
                    }
                });
            }

            return cocktails;
        } else
            return roomDatabaseService.cocktailDAO().getAllCocktails();
    }
}
