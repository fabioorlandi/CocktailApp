package com.example.cocktailapp.service.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.DAO.CocktailDAO;
import com.example.cocktailapp.model.DAO.IngredientDAO;
import com.example.cocktailapp.model.Ingredient;

@Database(entities = {Cocktail.class, Ingredient.class}, version = 1)
public abstract class CocktailDBRoomDatabaseService extends RoomDatabase {
    public abstract CocktailDAO cocktailDAO();

    public abstract IngredientDAO ingredientDAO();
}
