package com.example.cocktailapp.module;

import com.example.cocktailapp.service.CocktailDBRepository;
import com.example.cocktailapp.service.retrofit.CocktailDBRetrofitService;
import com.example.cocktailapp.service.room.CocktailDBRoomDatabaseService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = {RoomDBModule.class, RetrofitModule.class})
public class CocktailDBRepositoryModule {
    @Provides
    @Singleton
    public CocktailDBRepository getCocktailRepository(CocktailDBRoomDatabaseService roomDatabaseService, CocktailDBRetrofitService retrofitService) {
        return new CocktailDBRepository(roomDatabaseService, retrofitService);
    }
}
