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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertCocktail(Cocktail cocktail);

    @Update
    Integer updateCocktail(Cocktail cocktail);

    @Query("UPDATE cocktail SET edited_by_user = 1, cocktail_name = :name, directions = :directions WHERE cocktailId = :id")
    Integer updateCocktail(Long id, String name, String directions);

    @Query("UPDATE cocktail SET deleted_by_user = 1 WHERE cocktailId = :id")
    void deleteCocktail(Long id);

    @Query("DELETE FROM cocktail")
    void drop();
}
