package com.example.foodrecipes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ExtendedIngredient(
    val aisle: String? = null,
    val amount: Double? = null,
    val consistency: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val measures: @RawValue Measures? = null,
    val meta: List<String>? = null,
    val name: String? = null,
    val nameClean: String? = null,
    val original: String? = null,
    val originalName: String? = null,
    val unit: String? = null
) : Parcelable