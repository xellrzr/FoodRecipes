package com.example.foodrecipes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AnalyzedInstruction(
    val name: String? = null,
    val steps: @RawValue List<Step>? = null
) : Parcelable