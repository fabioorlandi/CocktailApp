package com.example.cocktailapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailapp.R;
import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.CocktailWithIngredients;

import java.util.List;

public class CocktailRecyclerViewAdapter extends RecyclerView.Adapter<CocktailRecyclerViewAdapter.CocktailViewHolder> {

    public Context context;
    public List<CocktailWithIngredients> cocktails;

    public CocktailRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setCocktailsToShow(List<CocktailWithIngredients> cocktails) {
        this.cocktails = cocktails;
        notifyDataSetChanged();
    }

    public List<CocktailWithIngredients> getCocktailsShown() {
        return cocktails;
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
        holder.cocktailName.setText(cocktails.get(position).cocktail.name);
        holder.cocktailThumbnail.setImageBitmap(cocktails.get(position).cocktail.thumbnail);
    }

    @Override
    public int getItemCount() {
        if (cocktails == null)
            return 0;

        return cocktails.size();
    }

    public static class CocktailViewHolder extends RecyclerView.ViewHolder{
        TextView cocktailName;
        ImageView cocktailThumbnail;

        public CocktailViewHolder(@NonNull View itemView) {
            super(itemView);

            cocktailName = itemView.findViewById(R.id.cocktail_name_id);
            cocktailThumbnail = itemView.findViewById(R.id.cocktail_image_id);
        }
    }
}
