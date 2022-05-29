package com.example.cocktailapp.model.surrogate;

import com.example.cocktailapp.model.Ingredient;

public class IngredientSurrogate {

    public String idIngredient;
    public String strIngredient;
    public String strDescription;
    public String strType;
    public String strAlcohol;
    public String strABV;

    public Ingredient toIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.ingredientId = Long.parseLong(idIngredient);
        ingredient.name = strIngredient;
        ingredient.description = strDescription;
        ingredient.ingredientType = strType;
        ingredient.isAlcoholic = Boolean.parseBoolean(strAlcohol);

        if (strABV != null)
            ingredient.ABV = Double.parseDouble(strABV);

        return ingredient;
    }
}
