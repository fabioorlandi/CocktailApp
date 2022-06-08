package com.example.cocktailapp.service;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.cocktailapp.app.CocktailApp;
import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.CocktailWithIngredients;
import com.example.cocktailapp.model.Ingredient;
import com.example.cocktailapp.model.IngredientWithQuantity;
import com.example.cocktailapp.model.base.Resource;
import com.example.cocktailapp.model.base.Status;
import com.example.cocktailapp.model.surrogate.CocktailSurrogate;
import com.example.cocktailapp.model.surrogate.IngredientSurrogate;
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
    private MutableLiveData<Resource<List<CocktailWithIngredients>>> cocktailsObservable = new MutableLiveData<>();
    private MutableLiveData<Resource<List<IngredientWithQuantity>>> ingredientsObservable = new MutableLiveData<>();
    private Status pendingStatus;
    private Integer cocktailsAPICalls;

    private static CocktailDBRepository repository;

    public static CocktailDBRepository getInstance() {
        if (repository == null)
            repository = new CocktailDBRepository();

        return repository;
    }

    public void fetchData() {
        pendingStatus = Status.LOADING;

        this.loadCocktailsFromDB();
        this.loadIngredientsFromDB();

        this.loadCocktailsFromAPI();
        this.loadIngredientsFromAPI();
    }

    public void syncData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                roomDatabaseService.cocktailDAO().drop();
                roomDatabaseService.ingredientDAO().drop();

                fetchData();
            }
        });
    }

    public void updateCocktail(Long id, String name, String directions) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                CocktailWithIngredients cocktailWithIngredients = roomDatabaseService.cocktailDAO().getCocktail(id);
                cocktailWithIngredients.cocktail.name = name;
                cocktailWithIngredients.cocktail.directions = directions;
                roomDatabaseService.cocktailDAO().updateCocktail(cocktailWithIngredients.cocktail);
                loadIngredientsFromDB();
            }
        });
    }

    public void deleteCocktail(Long id) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                CocktailWithIngredients cocktailWithIngredients = roomDatabaseService.cocktailDAO().getCocktail(id);
                roomDatabaseService.cocktailDAO().deleteCocktail(cocktailWithIngredients.cocktail);
                loadIngredientsFromDB();
            }
        });
    }

    public MutableLiveData<Resource<List<CocktailWithIngredients>>> getCocktailsObservable() {
        return cocktailsObservable;
    }

    public MutableLiveData<Resource<List<IngredientWithQuantity>>> getIngredientsObservable() {
        return ingredientsObservable;
    }

    private void loadCocktailsFromAPI() {
        List<Cocktail> cocktails = new ArrayList<>();

        cocktailsAPICalls = 0;

//        for (char letter = 'a'; letter <= 'z'; letter++) {
            retrofitService.getCocktailsByFirstLetter(Character.toString('a')).enqueue(new Callback<CocktailDBResult>() {
                @Override
                public void onResponse(@NonNull Call<CocktailDBResult> call, @NonNull Response<CocktailDBResult> response) {
                    if (response.isSuccessful()) {
                        cocktailsAPICalls++;
                        pendingStatus = Status.SUCCESS;
                        if (response.body() != null && ((CocktailDBResult) response.body()).drinks != null)
                            for (CocktailSurrogate surrogate : ((CocktailDBResult) response.body()).drinks) {
                                if (surrogate != null)
                                    cocktails.add(surrogate.toCocktail());
                            }

//                        if (cocktailsAPICalls > 26)
                            addCocktailsToDB(cocktails);
                    } else
                        pendingStatus = Status.ERROR;
                }

                @Override
                public void onFailure(@NonNull Call<CocktailDBResult> call, @NonNull Throwable t) {
                    Log.d("API_ERROR", "Error fetching cocktails data", t);
                }
            });
//        }
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
                List<CocktailWithIngredients> cocktails = roomDatabaseService.cocktailDAO().getAllCocktails();

                if (cocktails != null && cocktails.size() > 0) {
                    setCocktailsObservableData(cocktails, null);
                }
            }
        });
    }

    private void setCocktailsObservableData(List<CocktailWithIngredients> cocktails, String message) {
        Status loadingStatus = pendingStatus;
        if (cocktailsObservable.getValue() != null) {
            loadingStatus = cocktailsObservable.getValue().status;
        }

        switch (loadingStatus) {
            case LOADING:
                cocktailsObservable.postValue(Resource.loading(cocktails));
                break;
            case ERROR:
                cocktailsObservable.postValue(Resource.error(message, cocktails));
                break;
            case SUCCESS:
                cocktailsObservable.postValue(Resource.success(cocktails));
                break;
        }
    }

    private void loadIngredientsFromAPI() {
        List<Ingredient> ingredients = new ArrayList<>();

        retrofitService.getAllIngredientsNames().enqueue(new Callback<CocktailDBResult>() {
            @Override
            public void onResponse(@NonNull Call<CocktailDBResult> call, @NonNull Response<CocktailDBResult> response) {
                if (response.isSuccessful()) {
                    pendingStatus = Status.SUCCESS;
                    if (response.body() != null && ((CocktailDBResult) response.body()).drinks != null)
                        for (CocktailSurrogate surrogate : ((CocktailDBResult) response.body()).drinks) {
                            if (surrogate != null && surrogate.strIngredient1 != null)
                                retrofitService.getIngredientsByName(surrogate.strIngredient1).enqueue(new Callback<CocktailDBResult>() {
                                    @Override
                                    public void onResponse(Call<CocktailDBResult> call, Response<CocktailDBResult> response) {
                                        if (response.isSuccessful()) {
                                            pendingStatus = Status.SUCCESS;
                                            for (IngredientSurrogate surrogate : ((CocktailDBResult) response.body()).ingredients) {
                                                if (surrogate != null)
                                                    ingredients.add(surrogate.toIngredient());
                                            }

                                            addIngredientsToDB(ingredients);
                                        } else
                                            pendingStatus = Status.ERROR;
                                    }

                                    @Override
                                    public void onFailure(Call<CocktailDBResult> call, Throwable t) {
                                        Log.d("API_ERROR", "Error fetching ingredients data", t);
                                    }
                                });
                        }
                } else
                    pendingStatus = Status.ERROR;
            }

            @Override
            public void onFailure(@NonNull Call<CocktailDBResult> call, @NonNull Throwable t) {
                Log.d("API_ERROR", "Error fetching ingredients names data", t);
            }
        });

    }

    private void addIngredientsToDB(List<Ingredient> ingredients) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Boolean needsUpdate = false;
                for (Ingredient ingredient : ingredients) {
                    Long inserted = roomDatabaseService.ingredientDAO().insertIngredient(ingredient);
                    if (inserted == -1) {
                        Integer updated = roomDatabaseService.ingredientDAO().updateIngredient(ingredient);
                        if (updated > 0) {
                            needsUpdate = true;
                        }
                    } else {
                        needsUpdate = true;
                    }
                }

                if (needsUpdate)
                    loadIngredientsFromDB();
            }
        });
    }

    private void loadIngredientsFromDB() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<IngredientWithQuantity> ingredients = roomDatabaseService.ingredientDAO().getAllIngredients();

                if (ingredients != null && ingredients.size() > 0) {
                    setIngredientsObservableData(ingredients, null);
                }
            }
        });
    }

    private void setIngredientsObservableData(List<IngredientWithQuantity> ingredients, String message) {
        Status loadingStatus = pendingStatus;
        if (ingredientsObservable.getValue() != null) {
            loadingStatus = ingredientsObservable.getValue().status;
        }

        switch (loadingStatus) {
            case LOADING:
                ingredientsObservable.postValue(Resource.loading(ingredients));
                break;
            case ERROR:
                ingredientsObservable.postValue(Resource.error(message, ingredients));
                break;
            case SUCCESS:
                ingredientsObservable.postValue(Resource.success(ingredients));
                break;
        }
    }
}
