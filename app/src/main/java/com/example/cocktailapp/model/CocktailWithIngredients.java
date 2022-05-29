package com.example.cocktailapp.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CocktailWithIngredients {
    @Embedded
    public Cocktail cocktail;

    @Relation(
            parentColumn = "cocktailId",
            entityColumn = "ingredientId",
            associateBy = @Junction(CocktailIngredientCrossRef.class)
    )
    public List<Ingredient> ingredients;
}
