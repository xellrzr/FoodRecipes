package com.example.foodrecipes.data

import com.example.foodrecipes.data.network.RecipesApi
import com.example.foodrecipes.models.Recipes
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val recipesApi: RecipesApi
) {
    suspend fun getRecipes(queries: Map<String, String>): Response<Recipes> {
        return recipesApi.getRecipes(queries)
    }

    suspend fun searchRecipes(queries: Map<String, String>): Response<Recipes> {
        return recipesApi.searchRecipes(queries)
    }
}