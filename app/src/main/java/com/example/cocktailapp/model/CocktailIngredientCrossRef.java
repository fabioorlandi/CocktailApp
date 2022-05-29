package com.example.cocktailapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"cocktailId", "ingredientId"})
public class CocktailIngredientCrossRef {
    @NonNull
    public Long cocktailId;
    @NonNull
    public Long ingredientId;
}
