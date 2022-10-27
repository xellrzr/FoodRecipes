package com.example.foodrecipes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MissedIngredient(
    val aisle: String? = null,
    val amount: Double? = null,
    val extendedName: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val meta: List<String>? = null,
    val name: String? = null,
    val original: String? = null,
    val originalName: String? = null,
    val unit: String? = null,
    val unitLong: String? = null,
    val unitShort: String? = null
) : Parcelable