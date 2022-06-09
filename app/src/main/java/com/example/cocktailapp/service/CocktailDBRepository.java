package com.example.cocktailapp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CocktailDBRepository {
    private CocktailDBRepository() {
        final OkHttpClient okHttpClient = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CocktailDBRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
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
    private Boolean cocktailDBRunning = false;

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
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                setCocktailsObservableData(new ArrayList<>(), "");

                roomDatabaseService.cocktailDAO().drop();
                roomDatabaseService.ingredientDAO().drop();

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                fetchData();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void updateCocktail(Long id, String name, String directions) {
        new AsyncTask<Long, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Long... longs) {
                CocktailWithIngredients cocktailWithIngredients = roomDatabaseService.cocktailDAO().getCocktail(id);
                cocktailWithIngredients.cocktail.name = name;
                cocktailWithIngredients.cocktail.directions = directions;
                roomDatabaseService.cocktailDAO().updateCocktail(cocktailWithIngredients.cocktail);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                loadCocktailsFromDB();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    public void deleteCocktail(Long id) {
        new AsyncTask<Long, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Long... longs) {
                CocktailWithIngredients cocktailWithIngredients = roomDatabaseService.cocktailDAO().getCocktail(id);
                roomDatabaseService.cocktailDAO().deleteCocktail(cocktailWithIngredients.cocktail);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                loadCocktailsFromDB();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    public MutableLiveData<Resource<List<CocktailWithIngredients>>> getCocktailsObservable() {
        return cocktailsObservable;
    }

    public MutableLiveData<Resource<List<IngredientWithQuantity>>> getIngredientsObservable() {
        return ingredientsObservable;
    }

    private void loadCocktailsFromAPI() {
        for (char letter = 'c'; letter <= 'm'; letter++) {
            retrofitService.getCocktailsByFirstLetter(Character.toString(letter)).enqueue(new Callback<CocktailDBResult>() {
                @Override
                public void onResponse(@NonNull Call<CocktailDBResult> call, @NonNull Response<CocktailDBResult> response) {
                    if (response.isSuccessful()) {
                        pendingStatus = Status.SUCCESS;
                        if (response.body() != null && ((CocktailDBResult) response.body()).drinks != null)
                            for (CocktailSurrogate surrogate : ((CocktailDBResult) response.body()).drinks) {
                                if (surrogate != null) {
                                    new AsyncTask<Void, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(Void... voids) {
                                            try {
                                                URL url = new URL(surrogate.strDrinkThumb);
                                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                connection.setDoInput(true);
                                                connection.connect();
                                                InputStream input = connection.getInputStream();
                                                Bitmap bitmap = BitmapFactory.decodeStream(input);

                                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);

                                                return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                                            } catch (IOException e) {
                                                Log.d("BITMAP_CREATION_ERROR", "Error creating Bitmap");
                                                return null;
                                            }
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap bitmap) {
                                            Cocktail cocktail = surrogate.toCocktail();

                                            cocktail.thumbnail = bitmap;

                                            addCocktailToDB(cocktail);
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }
                    } else
                        pendingStatus = Status.ERROR;
                }

                @Override
                public void onFailure(@NonNull Call<CocktailDBResult> call, @NonNull Throwable t) {
                    Log.d("API_ERROR", "Error fetching cocktails data", t);
                    pendingStatus = Status.ERROR;
                }
            });
        }
    }

    private void addCocktailToDB(Cocktail cocktail) {
        new AsyncTask<Cocktail, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Cocktail... lists) {
                Boolean needsUpdate = false;
                Cocktail cocktail = lists[0];
                Long inserted = roomDatabaseService.cocktailDAO().insertCocktail(cocktail);
                if (inserted == -1) {
                    Integer updated = roomDatabaseService.cocktailDAO().updateCocktail(cocktail);
                    if (updated > 0) {
                        needsUpdate = true;
                    }
                } else {
                    needsUpdate = true;
                }
                return needsUpdate;
            }

            @Override
            protected void onPostExecute(Boolean needUpdate) {
                if (needUpdate) {
                    loadCocktailsFromDB();
                } else {
                    setCocktailsObservableStatus(pendingStatus, null);
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cocktail);
    }

    private void loadCocktailsFromDB() {
        new AsyncTask<Void, Void, List<CocktailWithIngredients>>() {
            @Override
            protected List<CocktailWithIngredients> doInBackground(Void... voids) {
                return roomDatabaseService.cocktailDAO().getAllCocktails();
            }

            @Override
            protected void onPostExecute(List<CocktailWithIngredients> results) {
                setCocktailsObservableData(results, null);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setCocktailsObservableData(List<CocktailWithIngredients> cocktails, String
            message) {
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

    private void setCocktailsObservableStatus(Status status, String message) {
        List<CocktailWithIngredients> cocktails = null;

        if (cocktailsObservable.getValue() != null) {
            cocktails = cocktailsObservable.getValue().data;
        }

        switch (status) {
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
                                                    addIngredientsToDB(surrogate.toIngredient());
                                            }

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

    private void addIngredientsToDB(Ingredient ingredient) {
        new AsyncTask<Ingredient, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Ingredient... lists) {
                Boolean needsUpdate = false;
                Ingredient ingredient = lists[0];
                Long inserted = roomDatabaseService.ingredientDAO().insertIngredient(ingredient);
                if (inserted == -1) {
                    Integer updated = roomDatabaseService.ingredientDAO().updateIngredient(ingredient);
                    if (updated > 0) {
                        needsUpdate = true;
                    }
                } else {
                    needsUpdate = true;
                }
                return needsUpdate;
            }

            @Override
            protected void onPostExecute(Boolean needUpdate) {
                if (needUpdate) {
                    loadCocktailsFromDB();
                } else {
                    setIngredientsObservableStatus(pendingStatus, null);
                }

            }
        }.execute(ingredient);
    }

    private void loadIngredientsFromDB() {
        new AsyncTask<Void, Void, List<IngredientWithQuantity>>() {
            @Override
            protected List<IngredientWithQuantity> doInBackground(Void... voids) {
                return roomDatabaseService.ingredientDAO().getAllIngredients();
            }

            @Override
            protected void onPostExecute(List<IngredientWithQuantity> results) {
                setIngredientsObservableData(results, null);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setIngredientsObservableData
            (List<IngredientWithQuantity> ingredients, String message) {
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

    private void setIngredientsObservableStatus(Status status, String message) {
        List<IngredientWithQuantity> ingredients = null;
        if (ingredientsObservable.getValue() != null) {
            ingredients = ingredientsObservable.getValue().data;
        }

        switch (status) {
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
