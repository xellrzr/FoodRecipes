package com.example.foodrecipes.data.database

import androidx.room.*
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.data.database.entities.RecipesEntity
import com.example.foodrecipes.data.database.entities.ShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    //RECIPES ENTITY DAO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    //FAVORITE RECIPES DAO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoriteEntity: FavoriteEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoriteEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity)

    //SHOPLIST DAO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopList(shopEntity: ShopEntity)

    @Query("SELECT * FROM shoplist_table ORDER BY id ASC")
    fun readShopList(): Flow<List<ShopEntity>>

    @Delete
    suspend fun deleteShopList(shopEntity: ShopEntity)
}