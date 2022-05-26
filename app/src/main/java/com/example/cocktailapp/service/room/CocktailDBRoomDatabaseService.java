package com.example.cocktailapp.service.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.DAO.CocktailDAO;
import com.example.cocktailapp.model.DAO.IngredientDAO;
import com.example.cocktailapp.model.Ingredient;
import com.example.cocktailapp.model.converter.BitmapConverter;
import com.example.cocktailapp.model.converter.DateConverter;

@Database(entities = {Cocktail.class, Ingredient.class}, version = 1)
@TypeConverters({DateConverter.class, BitmapConverter.class})
public abstract class CocktailDBRoomDatabaseService extends RoomDatabase {
    public abstract CocktailDAO cocktailDAO();

    public abstract IngredientDAO ingredientDAO();
}
