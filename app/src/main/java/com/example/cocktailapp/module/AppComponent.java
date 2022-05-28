package com.example.cocktailapp.module;

import com.example.cocktailapp.service.retrofit.CocktailDBRetrofitService;
import com.example.cocktailapp.service.room.CocktailDBRoomDatabaseService;
import com.example.cocktailapp.viewmodel.MainActivityViewModel;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, RetrofitModule.class, CocktailDBRepositoryModule.class, RoomDBModule.class})
public interface AppComponent {
    void inject(MainActivityViewModel viewModel);

    CocktailDBRetrofitService getCocktailAPI();
    CocktailDBRoomDatabaseService getCocktailAPIDB();
}
