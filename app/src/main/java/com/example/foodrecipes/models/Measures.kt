package com.example.foodrecipes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Measures(
    val metric: @RawValue Metric? = null,
    val us: @RawValue Us? = null
) : Parcelable