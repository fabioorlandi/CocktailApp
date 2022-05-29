package com.example.cocktailapp.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.CocktailWithIngredients;
import com.example.cocktailapp.model.IngredientWithQuantity;
import com.example.cocktailapp.model.base.Resource;
import com.example.cocktailapp.service.CocktailDBRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private MediatorLiveData<Resource<List<CocktailWithIngredients>>> cocktailsObservable = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<IngredientWithQuantity>>> ingredientsObservable = new MediatorLiveData<>();

    public MainActivityViewModel() {
        super();

        cocktailsObservable.addSource(CocktailDBRepository.getInstance().getCocktailsObservable(), new Observer<Resource<List<CocktailWithIngredients>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<CocktailWithIngredients>> cocktail) {
                cocktailsObservable.setValue(cocktail);
            }
        });

        ingredientsObservable.addSource(CocktailDBRepository.getInstance().getIngredientsObservable(), new Observer<Resource<List<IngredientWithQuantity>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<IngredientWithQuantity>> ingredient) {
                ingredientsObservable.setValue(ingredient);
            }
        });
    }

    public void getData() {
        CocktailDBRepository.getInstance().fetchData();
    }

    public LiveData<Resource<List<CocktailWithIngredients>>> getCocktailsObservable() {
        return cocktailsObservable;
    }

    public LiveData<Resource<List<IngredientWithQuantity>>> getIngredientsObservable() {
        return ingredientsObservable;
    }
}
