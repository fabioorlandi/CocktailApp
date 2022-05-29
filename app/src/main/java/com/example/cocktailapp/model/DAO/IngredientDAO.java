package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cocktailapp.model.Ingredient;

import java.util.List;

@Dao
public interface IngredientDAO {
    @Query("SELECT * FROM ingredient")
    List<Ingredient> getAllIngredients();

    @Insert
    Long insertIngredient(Ingredient ingredient);

    @Update
    Integer updateIngredient(Ingredient ingredient);
}
