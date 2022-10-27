package com.example.foodrecipes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Step(
    val equipment: @RawValue List<Equipment>? = null,
    val ingredients: @RawValue List<Ingredient>? = null,
    val length: @RawValue Length? = null,
    val number: Int? = null,
    val step: String? = null
) : Parcelable