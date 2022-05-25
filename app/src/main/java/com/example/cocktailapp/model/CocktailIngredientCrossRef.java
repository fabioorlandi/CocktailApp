package com.example.cocktailapp.model;

import androidx.room.Entity;

@Entity(primaryKeys = {"cocktailId", "ingredientId"})
public class CocktailIngredientCrossRef {
    public Long cocktailId;
    public Long ingredientId;
}
