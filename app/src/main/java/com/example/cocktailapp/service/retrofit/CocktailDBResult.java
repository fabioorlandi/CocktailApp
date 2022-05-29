package com.example.cocktailapp.service.retrofit;

import com.example.cocktailapp.model.surrogate.CocktailSurrogate;
import com.example.cocktailapp.model.surrogate.IngredientSurrogate;

import java.util.List;

public class CocktailDBResult {
    public List<CocktailSurrogate> drinks;

    public List<IngredientSurrogate> ingredients;
}
