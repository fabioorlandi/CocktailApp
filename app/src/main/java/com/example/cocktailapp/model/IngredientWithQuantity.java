package com.example.cocktailapp.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class IngredientWithQuantity {
    @Embedded
    public Ingredient ingredient;
    @Relation(
            parentColumn = "ingredientId",
            entityColumn = "ingredient_id"
    )
    public Quantity quantity;
}
