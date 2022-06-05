package com.example.cocktailapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cocktailapp.R;

public class CocktailDetailsActivity extends AppCompatActivity {

    private TextView cocktailName, IBACategory, directions;
    private ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.cocktailName = findViewById(R.id.cocktail_name_details_id);
        this.IBACategory = findViewById(R.id.cocktail_category_details_id);
        this.directions = findViewById(R.id.cocktail_direction_details_id);
        this.thumbnail = findViewById(R.id.cocktail_thumb_details_id);

        Intent intent = getIntent();
        String cocktailName = intent.getExtras().getString("CocktailName");
        String IBACategory = intent.getExtras().getString("IBACategory");
        String directions = intent.getExtras().getString("Directions");
        Bitmap thumbnail = intent.getParcelableExtra("Thumbnail");

        this.cocktailName.setText(cocktailName);
        this.IBACategory.setText(IBACategory);
        this.directions.setText(directions);
        this.thumbnail.setImageBitmap(thumbnail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}