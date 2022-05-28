package com.example.cocktailapp.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.cocktailapp.model.Cocktail;
import com.example.cocktailapp.service.CocktailDBRepository;

import java.util.List;

import javax.inject.Inject;

public class MainActivityViewModel extends ViewModel {

    @Inject
    CocktailDBRepository repository;

    private MediatorLiveData<List<Cocktail>> cocktailsObservable = new MediatorLiveData<List<Cocktail>>();

    public MainActivityViewModel() {
        super();

        cocktailsObservable.addSource(repository.getCocktailsObservable(), new Observer<List<Cocktail>>() {
            @Override
            public void onChanged(@Nullable List<Cocktail> cocktail) {
                cocktailsObservable.setValue(cocktail);
            }
        });
    }

    public void getData() {
        repository.fetchData();
    }

    public LiveData<List<Cocktail>> getCocktailsObservable() {
        return cocktailsObservable;
    }
}
