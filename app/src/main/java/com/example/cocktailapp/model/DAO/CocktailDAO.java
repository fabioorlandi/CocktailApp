package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.cocktailapp.model.Cocktail;

import java.util.List;

@Dao
public interface CocktailDAO {
    @Query("SELECT * FROM cocktail")
    List<Cocktail> getAllCocktails();
}
