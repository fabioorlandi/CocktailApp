package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cocktailapp.model.Cocktail;

import java.util.List;

@Dao
public interface CocktailDAO {
    @Transaction
    @Query("SELECT * FROM cocktail")
    List<Cocktail> getAllCocktails();
}
