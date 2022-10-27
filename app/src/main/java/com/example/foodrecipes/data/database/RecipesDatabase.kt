package com.example.foodrecipes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.data.database.entities.RecipesEntity
import com.example.foodrecipes.data.database.entities.ShopEntity


@Database(
    entities = [RecipesEntity::class, FavoriteEntity::class, ShopEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverters::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipesDao() : RecipesDao
}