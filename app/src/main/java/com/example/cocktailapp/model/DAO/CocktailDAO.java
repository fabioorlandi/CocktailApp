package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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

    @Query("SELECT * FROM cocktail WHERE cocktailId = :id")
    CocktailWithIngredients getCocktail(Long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertCocktail(Cocktail cocktail);

    @Update
    Integer updateCocktail(Cocktail cocktail);

    @Delete
    void deleteCocktail(Cocktail cocktail);

    @Query("DELETE FROM cocktail")
    void drop();
}
