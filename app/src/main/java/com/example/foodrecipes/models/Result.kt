package com.example.foodrecipes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Result(
    val aggregateLikes: Int? = null,
    val analyzedInstructions: @RawValue List<AnalyzedInstruction>? = null,
    val cheap: Boolean = false,
    val cookingMinutes: Int? = null,
    val creditsText: String? = null,
    val cuisines: List<String>? = null,
    val dairyFree: Boolean,
    val diets: List<String>? = null,
    val dishTypes: List<String>? = null,
    val extendedIngredients: @RawValue List<ExtendedIngredient>? = null,
    val gaps: String? = null,
    val glutenFree: Boolean = false,
    val healthScore: Int? = null,
    val id: Int? = null,
    val image: String? = null,
    val imageType: String? = null,
    val license: String? = null,
    val likes: Int? = null,
    val lowFodmap: Boolean? = null,
    val missedIngredientCount: Int? = null,
    val missedIngredients: @RawValue List<MissedIngredient>? = null,
    val occasions: List<String>? = null,
    val openLicense: Int? = null,
    val preparationMinutes: Int? = null,
    val pricePerServing: Double? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val sourceName: String? = null,
    val sourceUrl: String? = null,
    val spoonacularSourceUrl: String? = null,
    val summary: String? = null,
    val sustainable: Boolean = false,
    val title: String? = null,
    val unusedIngredients: @RawValue List<Any>? = null,
    val usedIngredientCount: Int? = null,
    val usedIngredients: @RawValue List<Any>? = null,
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val veryHealthy: Boolean = false,
    val veryPopular: Boolean = false,
    val weightWatcherSmartPoints: Int? = null
) : Parcelable
