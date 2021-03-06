package com.example.cocktailapp.service.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.CocktailIngredientCrossRef;
import com.example.cocktailapp.model.DAO.CocktailDAO;
import com.example.cocktailapp.model.DAO.IngredientDAO;
import com.example.cocktailapp.model.Ingredient;
import com.example.cocktailapp.model.Quantity;
import com.example.cocktailapp.model.converter.BitmapConverter;
import com.example.cocktailapp.model.converter.DateConverter;

@Database(entities = {Cocktail.class, Ingredient.class, Quantity.class, CocktailIngredientCrossRef.class}, version = 1)
@TypeConverters({DateConverter.class, BitmapConverter.class})
public abstract class CocktailDBRoomDatabaseService extends RoomDatabase {
    public static final String DATABASE_NAME = "cocktail_database";

    public abstract CocktailDAO cocktailDAO();

    public abstract IngredientDAO ingredientDAO();
}
