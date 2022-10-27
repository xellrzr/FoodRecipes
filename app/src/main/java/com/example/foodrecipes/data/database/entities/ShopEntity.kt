package com.example.foodrecipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodrecipes.models.ExtendedIngredient
import com.example.foodrecipes.utils.Constants.Companion.SHOPLIST_TABLE

@Entity(tableName = SHOPLIST_TABLE)
class ShopEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val ingredients: ExtendedIngredient
)
