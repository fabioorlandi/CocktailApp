package com.example.cocktailapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ingredient {

    @PrimaryKey
    public Long ingredientId;

    @ColumnInfo(name = "ingredient_name")
    public String name;

    @ColumnInfo(name = "ingredient_description")
    public String description;

    @ColumnInfo(name = "ingredient_type")
    public IngredientType ingredientType;

    @ColumnInfo(name = "is_alcoholic")
    public Boolean isAlcoholic;

    @Embedded
    public Quantity quantity;

    @ColumnInfo(name = "alcohol_by_volume")
    public Integer ABV;
}
