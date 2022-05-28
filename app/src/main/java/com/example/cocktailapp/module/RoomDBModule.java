package com.example.cocktailapp.module;

import androidx.room.Room;

import com.example.cocktailapp.app.CocktailApp;
import com.example.cocktailapp.model.DAO.CocktailDAO;
import com.example.cocktailapp.model.DAO.IngredientDAO;
import com.example.cocktailapp.service.room.CocktailDBRoomDatabaseService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = AppModule.class)
public class RoomDBModule {
    private CocktailDBRoomDatabaseService cocktailDBService;

    public RoomDBModule(CocktailApp mApplication) {
        cocktailDBService = Room.databaseBuilder(mApplication, CocktailDBRoomDatabaseService.class, "cocktail_database")
                .build();
    }

    @Singleton
    @Provides
    CocktailDBRoomDatabaseService providesRoomDatabase() {
        return cocktailDBService;
    }

    @Singleton
    @Provides
    CocktailDAO providesCocktailDAO(CocktailDBRoomDatabaseService cocktailDBService) {
        return cocktailDBService.cocktailDAO();
    }

    @Singleton
    @Provides
    IngredientDAO providesIngredientDAO(CocktailDBRoomDatabaseService cocktailDBService) {
        return cocktailDBService.ingredientDAO();
    }
}
