package com.example.foodrecipes.data

import com.example.foodrecipes.data.database.RecipesDao
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.data.database.entities.RecipesEntity
import com.example.foodrecipes.data.database.entities.ShopEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    suspend fun insertFavoriteRecipe(favoriteEntity: FavoriteEntity) {
        recipesDao.insertFavoriteRecipe(favoriteEntity)
    }

    fun readFavoriteRecipes(): Flow<List<FavoriteEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    suspend fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity) {
        recipesDao.deleteFavoriteRecipe(favoriteEntity)
    }

    suspend fun insertShopList(shopEntity: ShopEntity) {
        recipesDao.insertShopList(shopEntity)
    }

    fun readShopList(): Flow<List<ShopEntity>> {
        return recipesDao.readShopList()
    }

    suspend fun deleteShopList(shopEntity: ShopEntity) {
        recipesDao.deleteShopList(shopEntity)
    }
}