package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.CocktailWithIngredients;

import java.util.List;

@Dao
public interface CocktailDAO {
    @Transaction
    @Query("SELECT * FROM cocktail")
    List<CocktailWithIngredients> getAllCocktails();

    @Insert
    Long insertCocktail(Cocktail cocktail);

    @Update
    Integer updateCocktail(Cocktail cocktail);
}
