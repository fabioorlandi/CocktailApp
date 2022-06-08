package com.example.cocktailapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cocktailapp.R;
import com.example.cocktailapp.model.base.CocktailActivityResult;

public class CocktailDetailsActivity extends AppCompatActivity {

    private String id;
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_dialog_title)
                        .setMessage(R.string.delete_dialog_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(CocktailActivityResult.DELETE.getValue(), getIntent());
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cocktail_details, menu);

        return true;
    }
}