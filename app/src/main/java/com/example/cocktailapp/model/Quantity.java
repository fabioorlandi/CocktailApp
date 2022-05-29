package com.example.cocktailapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quantity {
    @PrimaryKey
    public Long quantityId;

    @ColumnInfo(name = "unit_of_measurement")
    public String unitOfMeasurement;

    @ColumnInfo(name = "value")
    public String value;

    @ColumnInfo(name = "ingredient_id")
    public Long ingredientId;
}
