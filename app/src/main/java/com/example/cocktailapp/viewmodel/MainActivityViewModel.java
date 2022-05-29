package com.example.cocktailapp.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.model.base.Resource;
import com.example.cocktailapp.service.CocktailDBRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private MediatorLiveData<Resource<List<Cocktail>>> cocktailsObservable = new MediatorLiveData<>();

    public MainActivityViewModel() {
        super();

        cocktailsObservable.addSource(CocktailDBRepository.getInstance().getCocktailsObservable(), new Observer<Resource<List<Cocktail>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Cocktail>> cocktail) {
                cocktailsObservable.setValue(cocktail);
            }
        });
    }

    public void getData() {
        CocktailDBRepository.getInstance().fetchData();
    }

    public LiveData<Resource<List<Cocktail>>> getCocktailsObservable() {
        return cocktailsObservable;
    }
}
