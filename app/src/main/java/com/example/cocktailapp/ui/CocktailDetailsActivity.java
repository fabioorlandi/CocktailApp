package com.example.cocktailapp.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailapp.R;
import com.example.cocktailapp.model.base.CocktailActivityResult;

import java.io.ByteArrayOutputStream;

public class CocktailDetailsActivity extends AppCompatActivity {

    private String id;
    private TextView cocktailName, IBACategory, directions;
    private ImageView thumbnail;

    public ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case 4:
                            Long id = result.getData().getExtras().getLong("ID");
                            String editedName = result.getData().getExtras().getString("EditedCocktailName");
                            String editedDirections = result.getData().getExtras().getString("EditedDirections");
                            cocktailName.setText(editedName);
                            directions.setText(editedDirections);
                            break;
                    }
                }
            }
    );

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
        Long id = intent.getExtras().getLong("ID");
        String cocktailName = intent.getExtras().getString("CocktailName");
        String IBACategory = intent.getExtras().getString("IBACategory");
        String directions = intent.getExtras().getString("Directions");

        if (intent.hasExtra("Thumbnail")) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    intent.getByteArrayExtra("Thumbnail"), 0,
                    intent.getByteArrayExtra("Thumbnail").length);
            this.thumbnail.setImageBitmap(bitmap);
        }

        this.id = id.toString();
        this.cocktailName.setText(cocktailName);
        this.IBACategory.setText(IBACategory);
        this.directions.setText(directions);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("ID", Long.parseLong(this.id));
                resultIntent.putExtra("EditedCocktailName", this.cocktailName.getText().toString());
                resultIntent.putExtra("EditedDirections", this.directions.getText().toString());
                setResult(CocktailActivityResult.UPDATE.getValue(), resultIntent);

                finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, CocktailEditDetailsActivity.class);
                intent.putExtra("ID", Long.parseLong(this.id));
                intent.putExtra("CocktailName", this.cocktailName.getText().toString());
                intent.putExtra("Directions", this.directions.getText().toString());

                Bitmap bitmap = ((BitmapDrawable)this.thumbnail.getDrawable()).getBitmap();

                if (bitmap != null) {
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    intent.putExtra("Thumbnail", bs.toByteArray());
                }

                activityResultLauncher.launch(intent);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_dialog_title)
                        .setMessage(R.string.delete_dialog_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(CocktailDetailsActivity.this, R.string.cocktail_deleted, Toast.LENGTH_SHORT).show();
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