package com.example.cocktailapp.model.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.cocktailapp.model.Ingredient;
import com.example.cocktailapp.model.IngredientWithQuantity;

import java.util.List;

@Dao
public interface IngredientDAO {
    @Transaction
    @Query("SELECT * FROM ingredient")
    List<IngredientWithQuantity> getAllIngredients();

    @Insert
    Long insertIngredient(Ingredient ingredient);

    @Update
    Integer updateIngredient(Ingredient ingredient);
}
