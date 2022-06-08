package com.example.cocktailapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailapp.R;
import com.example.cocktailapp.model.base.CocktailActivityResult;

public class CocktailEditDetailsActivity extends AppCompatActivity {

    private String id;
    private EditText cocktailName, directions;
    private ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_edit_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.cocktailName = findViewById(R.id.cocktail_name_edit_details_id);
        this.directions = findViewById(R.id.cocktail_direction_edit_details_id);
        this.thumbnail = findViewById(R.id.cocktail_thumb_edit_details_id);

        Intent intent = getIntent();
        Long id = intent.getExtras().getLong("ID");
        String cocktailName = intent.getExtras().getString("CocktailName");
        String directions = intent.getExtras().getString("Directions");
        Bitmap thumbnail = intent.getParcelableExtra("Thumbnail");

        this.id = id.toString();
        this.cocktailName.setText(cocktailName, TextView.BufferType.EDITABLE);
        this.directions.setText(directions, TextView.BufferType.EDITABLE);
        this.thumbnail.setImageBitmap(thumbnail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_cancel:
                Toast.makeText(this, R.string.operation_canceled, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case R.id.action_save:
                Intent intent = new Intent();
                intent.putExtra("EditedCocktailName", this.cocktailName.getText().toString());
                intent.putExtra("EditedDirections", this.directions.getText().toString());
                intent.putExtra("ID", Long.parseLong(this.id));

                setResult(CocktailActivityResult.SAVE.getValue(), intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cocktail_edit_details, menu);

        return true;
    }
}
