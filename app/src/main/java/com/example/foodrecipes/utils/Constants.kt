package com.example.foodrecipes.utils

class Constants {
    companion object {

        const val API_KEY = "d0de446832d44c9d9e6671a79f5e83b9"
        const val BASE_URL = "https://api.spoonacular.com"

        const val BASE_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/"


        //ROOM
        const val DATABASE_NAME = "recipes_database"
        const val RECIPES_TABLE = "recipes_table"
        const val FAVORITE_RECIPES_TABLE = "favorite_recipes_table"
        const val SHOPLIST_TABLE = "shoplist_table"

        //API QUERIES
        const val QUERY_SEARCH = "query"

        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_MEAL = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        //DATASTORE
        const val PREFERENCES_NAME = "my_recipe_preferences"

        //DATASTORE PREFERENCES KEYS
        const val PREFERENCES_MEAL_TYPE = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID = "mealTypeId"
        const val PREFERENCES_DIET_TYPE = "dietType"
        const val PREFERENCES_DIET_TYPE_ID = "dietTypeId"

        //DATASTORE DEFAULT VALUES
        const val DEFAULT_RECIPES_COUNT = "50"
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"

        //https://www.baeldung.com/kotlin/gson-parse-arrays
        //https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
    }
}