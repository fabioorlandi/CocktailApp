package com.example.cocktailapp.model;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Cocktail {
    @PrimaryKey
    public Long cocktailId;

    @ColumnInfo(name = "cocktail_name")
    public String name;

    @ColumnInfo(name = "international_bar_association_category")
    public String IBACategory;

    @ColumnInfo(name = "is_alcoholic")
    @Nullable
    public Boolean isAlcoholic;

    @ColumnInfo(name = "glass_type")
    public String glassType;

    @ColumnInfo(name = "instructions")
    public String instructions;

    @ColumnInfo(name = "thumbnail")
    public Bitmap thumbnail;

    @ColumnInfo(name = "image")
    public Bitmap image;

    @ColumnInfo(name = "date_modified")
    public Date dateModified;
}
