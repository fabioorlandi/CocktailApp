package com.example.cocktailapp.model.surrogate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.Ingredient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CocktailSurrogate {
    public String idDrink;
    public String strDrink;
    public String strIBA;
    public String strAlcoholic;
    public String strGlass;
    public String strInstructions;
    public String strDrinkThumb;
    public String strImageSource;
    public String strIngredient1;
    public String strIngredient2;
    public String strIngredient3;
    public String strIngredient4;
    public String strIngredient5;
    public String strIngredient6;
    public String strIngredient7;
    public String strIngredient8;
    public String strIngredient9;
    public String strIngredient10;
    public String strMeasure1;
    public String strMeasure2;
    public String strMeasure3;
    public String strMeasure4;
    public String strMeasure5;
    public String strMeasure6;
    public String strMeasure7;
    public String strMeasure8;
    public String strMeasure9;
    public String strMeasure10;
    public String dateModified;

    public Cocktail toCocktail() {
        Cocktail cocktail = new Cocktail();
        cocktail.cocktailId = Long.parseLong(idDrink);
        cocktail.name = strDrink;
        cocktail.IBACategory = strIBA;
        cocktail.alcoholicType = strAlcoholic;
        cocktail.glassType = strGlass;
        cocktail.directions = strInstructions;
        cocktail.thumbnail = this.createBitmap(strDrinkThumb);
        cocktail.image = this.createBitmap(strImageSource);
        cocktail.dateModified = this.getDate(dateModified);

        return cocktail;
    }

    private Bitmap createBitmap(String imageURL) {
        final Bitmap[] bitmap = new Bitmap[1];

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    Log.d("BITMAP_CREATION_ERROR", "Error creating Bitmap");
                    bitmap[0] = null;
                }
            }
        });

        return bitmap[0];
    }


    private Date getDate(String dateModified) {
        if (dateModified != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
            try {
                return format.parse(dateModified);
            } catch (ParseException e) {
                Log.d("DATE_PARSE_ERROR", "Error parsing date");
            }
        }

        return null;
    }
}
