package com.example.cocktailapp.service;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.cocktailapp.app.CocktailApp;
import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.base.Resource;
import com.example.cocktailapp.model.base.Status;
import com.example.cocktailapp.model.surrogate.CocktailSurrogate;
import com.example.cocktailapp.service.retrofit.CocktailDBResult;
import com.example.cocktailapp.service.retrofit.CocktailDBRetrofitService;
import com.example.cocktailapp.service.room.CocktailDBRoomDatabaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CocktailDBRepository {
    private CocktailDBRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CocktailDBRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitService = retrofit.create(CocktailDBRetrofitService.class);

        roomDatabaseService = Room.databaseBuilder(CocktailApp.getAppContext(), CocktailDBRoomDatabaseService.class, CocktailDBRoomDatabaseService.DATABASE_NAME)
                .build();
    }

    private CocktailDBRoomDatabaseService roomDatabaseService;
    private CocktailDBRetrofitService retrofitService;
    private MutableLiveData<Resource<List<Cocktail>>> cocktailsObservable = new MutableLiveData<>();
    private Status pendingStatus;

    private static CocktailDBRepository repository;

    public static CocktailDBRepository getInstance() {
        if (repository == null)
            repository = new CocktailDBRepository();

        return repository;
    }

    public void fetchData() {
        pendingStatus = Status.LOADING;

        this.loadCocktailsFromDB();
        this.loadCocktailsFromAPI();
    }

    public MutableLiveData<Resource<List<Cocktail>>> getCocktailsObservable() {
        return cocktailsObservable;
    }

    private void loadCocktailsFromAPI() {
        List<Cocktail> cocktails = new ArrayList<>();

        for (char letter = 'a'; letter <= 'z'; letter++) {
            retrofitService.getCocktailsByFirstLetter(Character.toString(letter)).enqueue(new Callback<CocktailDBResult>() {
                @Override
                public void onResponse(@NonNull Call<CocktailDBResult> call, @NonNull Response<CocktailDBResult> response) {
                    if (response.isSuccessful()) {
                        pendingStatus = Status.SUCCESS;
                        if (response.body() != null && ((CocktailDBResult) response.body()).drinks != null)
                            for (CocktailSurrogate surrogate : ((CocktailDBResult) response.body()).drinks) {
                                if (surrogate != null)
                                    cocktails.add(surrogate.toCocktail());
                            }

                        addCocktailsToDB(cocktails);
                    }
                    else
                        pendingStatus = Status.ERROR;
                }

                @Override
                public void onFailure(@NonNull Call<CocktailDBResult> call, @NonNull Throwable t) {
                    Log.d("API_ERROR", "Error fetching data", t);
                }
            });
        }
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
                    setCocktailsObservableData(cocktails, null);
                }
            }
        });
    }

    private void setCocktailsObservableData(List<Cocktail> cocktails, String message) {
        Status loadingStatus = pendingStatus;
        if (cocktailsObservable.getValue() != null) {
            loadingStatus = cocktailsObservable.getValue().status;
        }

        switch (loadingStatus) {
            case LOADING:
                cocktailsObservable.setValue(Resource.loading(cocktails));
                break;
            case ERROR:
                cocktailsObservable.setValue(Resource.error(message, cocktails));
                break;
            case SUCCESS:
                cocktailsObservable.setValue(Resource.success(cocktails));
                break;
        }
    }
}
