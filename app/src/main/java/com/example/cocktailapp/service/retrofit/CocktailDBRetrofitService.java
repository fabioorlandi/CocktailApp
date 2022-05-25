package com.example.cocktailapp.service.retrofit;

import com.example.cocktailapp.model.surrogate.CocktailSurrogate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CocktailDBRetrofitService {
    @GET("search.php?f={letter}")
    Call<List<CocktailSurrogate>> getCocktailsByFirstLetter(@Path("letter") String letter);
}
