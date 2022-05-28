package com.example.cocktailapp.service.retrofit;

import com.example.cocktailapp.model.surrogate.CocktailSurrogate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CocktailDBRetrofitService {
    String BASE_URL = "http://www.thecocktaildb.com/api/json/v1/1/";

    @GET("search.php?f={letter}")
    Call<List<CocktailSurrogate>> getCocktailsByFirstLetter(@Path("letter") String letter);
}
