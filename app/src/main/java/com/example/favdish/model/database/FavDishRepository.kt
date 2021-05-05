package com.example.favdish.model.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.example.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val  favDishDao: FavDishDao ) {
    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetail(favDish)
    }


    val allDishesList : Flow<List<FavDish>> = favDishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetail(favDish)
    }

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish){
        favDishDao.deleteFavDish(favDish)
    }

    val favoriteDishes : Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

    fun filterListDishes(value:String) : Flow<List<FavDish>> =
        favDishDao.getFilterDishesList(value)
}