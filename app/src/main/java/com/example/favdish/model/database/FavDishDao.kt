package com.example.favdish.model.database

import androidx.room.*
import com.example.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavDishDetail(favDish : FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList() : Flow<List<FavDish>>

    @Update
    suspend fun updateFavDishDetail(favDish: FavDish)

    @Query("SELECT * FROM   FAV_DISHES_TABLE WHERE favorite_dish = 1")
     fun getFavoriteDishesList() : Flow<List<FavDish>>

     @Delete
     suspend fun deleteFavDish(favDish: FavDish)

     @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
     fun getFilterDishesList(filterType:String) : Flow<List<FavDish>>
}