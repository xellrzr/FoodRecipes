package com.example.foodrecipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodrecipes.models.Result
import com.example.foodrecipes.utils.Constants.Companion.FAVORITE_RECIPES_TABLE


@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)