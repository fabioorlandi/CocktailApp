package com.example.cocktailapp.service.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CocktailDBRetrofitService {
    String BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/";

    @GET("search.php?")
    Call<CocktailDBResult> getCocktailsByFirstLetter(@Query("f") String letter);

    @GET("list.php?i=list")
    Call<CocktailDBResult> getAllIngredientsNames();

    @GET("search.php?")
    Call<CocktailDBResult> getIngredientsByName(@Query("i") String name);
}
