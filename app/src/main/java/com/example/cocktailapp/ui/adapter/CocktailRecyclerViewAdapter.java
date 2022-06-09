package com.example.cocktailapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailapp.R;
import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.CocktailWithIngredients;
import com.example.cocktailapp.ui.CocktailDetailsActivity;
import com.example.cocktailapp.ui.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CocktailRecyclerViewAdapter extends RecyclerView.Adapter<CocktailRecyclerViewAdapter.CocktailViewHolder> implements Filterable {

    public Context context;
    public List<CocktailWithIngredients> filteredCocktails;
    public List<CocktailWithIngredients> allCockails;

    public CocktailRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setCocktailsToShow(List<CocktailWithIngredients> cocktails) {
        this.allCockails = cocktails;
        this.filteredCocktails = new ArrayList<>(cocktails);
        notifyDataSetChanged();
    }

    public List<CocktailWithIngredients> getCocktailsShown() {
        return filteredCocktails;
    }

    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater laytoutInflater = LayoutInflater.from(this.context);
        View view = laytoutInflater.inflate(R.layout.cardview_item, parent, false);

        return new CocktailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailViewHolder holder, int position) {
        holder.cocktailName.setText(filteredCocktails.get(holder.getAdapterPosition()).cocktail.name);
        holder.cocktailThumbnail.setImageBitmap(filteredCocktails.get(holder.getAdapterPosition()).cocktail.thumbnail);
        holder.cocktailsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CocktailDetailsActivity.class);
                intent.putExtra("ID", filteredCocktails.get(holder.getAdapterPosition()).cocktail.cocktailId);
                intent.putExtra("CocktailName", filteredCocktails.get(holder.getAdapterPosition()).cocktail.name);
                intent.putExtra("IBACategory", filteredCocktails.get(holder.getAdapterPosition()).cocktail.IBACategory);
                intent.putExtra("Directions", filteredCocktails.get(holder.getAdapterPosition()).cocktail.directions);

                Bitmap bitmap = filteredCocktails.get(holder.getAdapterPosition()).cocktail.thumbnail;

                if (bitmap != null) {
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    intent.putExtra("Thumbnail", bs.toByteArray());
                }

                ((MainActivity) context).activityResultLauncher.launch(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (filteredCocktails == null)
            return 0;

        return filteredCocktails.size();
    }

    @Override
    public Filter getFilter() {
        return cocktailFilter;
    }

    private Filter cocktailFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CocktailWithIngredients> filteredCocktails = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredCocktails.addAll(allCockails);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                for (CocktailWithIngredients cocktail : allCockails) {
                    if (cocktail.cocktail.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredCocktails.add(cocktail);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredCocktails;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (filteredCocktails != null) {
                filteredCocktails.clear();
                filteredCocktails.addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };

    public static class CocktailViewHolder extends RecyclerView.ViewHolder {
        TextView cocktailName;
        ImageView cocktailThumbnail;
        CardView cocktailsCardView;

        public CocktailViewHolder(@NonNull View itemView) {
            super(itemView);

            cocktailName = itemView.findViewById(R.id.cocktail_name_id);
            cocktailThumbnail = itemView.findViewById(R.id.cocktail_image_id);
            cocktailsCardView = itemView.findViewById(R.id.cardview_id);
        }
    }
}
