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
        cocktail.ingredients = this.createIngredients();
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

    private List<Ingredient> createIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

        Ingredient ingredient;

        if (strIngredient1 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient1;
            ingredient.quantity = strMeasure1;

            ingredients.add(ingredient);
        }

        if (strIngredient2 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient2;
            ingredient.quantity = strMeasure2;

            ingredients.add(ingredient);
        }

        if (strIngredient3 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient3;
            ingredient.quantity = strMeasure3;

            ingredients.add(ingredient);
        }

        if (strIngredient4 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient4;
            ingredient.quantity = strMeasure4;

            ingredients.add(ingredient);
        }

        if (strIngredient5 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient5;
            ingredient.quantity = strMeasure5;

            ingredients.add(ingredient);
        }

        if (strIngredient6 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient6;
            ingredient.quantity = strMeasure6;

            ingredients.add(ingredient);
        }

        if (strIngredient7 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient7;
            ingredient.quantity = strMeasure7;

            ingredients.add(ingredient);
        }

        if (strIngredient8 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient8;
            ingredient.quantity = strMeasure8;

            ingredients.add(ingredient);
        }

        if (strIngredient9 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient9;
            ingredient.quantity = strMeasure9;

            ingredients.add(ingredient);
        }

        if (strIngredient10 != null) {
            ingredient = new Ingredient();
            ingredient.name = strIngredient10;
            ingredient.quantity = strMeasure10;

            ingredients.add(ingredient);
        }

        return ingredients;
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
