package com.example.foodrecipes.data.database

import androidx.room.TypeConverter
import com.example.foodrecipes.models.AnalyzedInstruction
import com.example.foodrecipes.models.ExtendedIngredient
import com.example.foodrecipes.models.Recipes
import com.example.foodrecipes.models.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class RecipesTypeConverters {

    @TypeConverter
    fun fromRecipesToString(recipes: Recipes): String {
        val gson = Gson()
        return gson.toJson(recipes)
    }

    @TypeConverter
    fun toRecipes(value: String?): Recipes? {
        val recipes: Type = object : TypeToken<Recipes>() {}.type
        return Gson().fromJson(value, recipes)
    }

    @TypeConverter
    fun fromResultToString(result: Result): String {
        val gson = Gson()
        return gson.toJson(result)
    }

    @TypeConverter
    fun toResult(value: String?): Result? {
        val result: Type = object  : TypeToken<Result>() {}.type
        return Gson().fromJson(value, result)
    }


    @TypeConverter
    fun fromExtendedIngredient(extendedIngredient: ExtendedIngredient): String {
        val gson = Gson()
        return gson.toJson(extendedIngredient)
    }

    @TypeConverter
    fun toExtendedIngredient(value: String): ExtendedIngredient? {
        val extendedIngredient: Type = object : TypeToken<ExtendedIngredient>() {}.type
        return Gson().fromJson(value, extendedIngredient)
    }

}