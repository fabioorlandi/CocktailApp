package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.cocktailapp.model.Cocktail;

import java.util.List;

@Dao
public interface CocktailDAO {
    @Query("SELECT * FROM cocktail")
    List<Cocktail> getAllCocktails();

    @Insert
    Long insertCocktail(Cocktail cocktail);

    @Update
    Integer updateCocktail(Cocktail cocktail);
}
