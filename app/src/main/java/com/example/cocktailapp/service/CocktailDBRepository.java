package com.example.cocktailapp.service;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.surrogate.CocktailSurrogate;
import com.example.cocktailapp.service.retrofit.CocktailDBRetrofitService;
import com.example.cocktailapp.service.room.CocktailDBRoomDatabaseService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CocktailDBRepository {
    @Inject
    public CocktailDBRepository(CocktailDBRoomDatabaseService roomDatabaseService, CocktailDBRetrofitService retrofitService) {
        this.roomDatabaseService = roomDatabaseService;
        this.retrofitService = retrofitService;

    }

    private CocktailDBRoomDatabaseService roomDatabaseService;
    private CocktailDBRetrofitService retrofitService;
    private MutableLiveData<List<Cocktail>> cocktailsObservable = new MutableLiveData<List<Cocktail>>();

    public void fetchData() {
        List<Cocktail> loadingList = null;

        if (cocktailsObservable.getValue() != null) {
            loadingList = cocktailsObservable.getValue();
        }

        cocktailsObservable.setValue(loadingList);

        this.loadCocktailsFromDB();
        this.loadCocktailsFromAPI();
    }

    public MutableLiveData<List<Cocktail>> getCocktailsObservable() {
        return cocktailsObservable;
    }

    private void loadCocktailsFromAPI() {
        List<Cocktail> cocktails = new ArrayList<>();

        for (char letter = 'a'; letter <= 'z'; letter++) {
            retrofitService.getCocktailsByFirstLetter(Character.toString(letter)).enqueue(new Callback<List<CocktailSurrogate>>() {
                @Override
                public void onResponse(@NonNull Call<List<CocktailSurrogate>> call, @NonNull Response<List<CocktailSurrogate>> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        for (CocktailSurrogate surrogate : response.body()) {
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

        this.addCocktailsToDB(cocktails);
    }

    private void addCocktailsToDB(List<Cocktail> cocktails) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Boolean needsUpdate = false;
                for (Cocktail cocktail : cocktails) {
                    Long inserted = roomDatabaseService.cocktailDAO().insertCocktail(cocktail);
                    if (inserted == -1) {
                        Integer updated = roomDatabaseService.cocktailDAO().updateCocktail(cocktail);
                        if (updated > 0) {
                            needsUpdate = true;
                        }
                    } else {
                        needsUpdate = true;
                    }
                }

                if (needsUpdate)
                    loadCocktailsFromDB();
            }
        });
    }

    private void loadCocktailsFromDB() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Cocktail> cocktails = roomDatabaseService.cocktailDAO().getAllCocktails();

                if (cocktails != null && cocktails.size() > 0) {
                    setCocktailsObservableData(cocktails);
                }
            }
        });
    }

    private void setCocktailsObservableData(List<Cocktail> cocktails) {
        if (cocktailsObservable.getValue() != null) {
            cocktailsObservable.setValue(cocktails);
        }
    }
}
