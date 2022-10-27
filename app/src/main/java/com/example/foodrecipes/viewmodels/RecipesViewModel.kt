package com.example.foodrecipes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodrecipes.data.DataStoreRepository
import com.example.foodrecipes.utils.Constants.Companion.API_KEY
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_RECIPES_COUNT
import com.example.foodrecipes.utils.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.foodrecipes.utils.Constants.Companion.QUERY_API_KEY
import com.example.foodrecipes.utils.Constants.Companion.QUERY_DIET
import com.example.foodrecipes.utils.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.foodrecipes.utils.Constants.Companion.QUERY_MEAL
import com.example.foodrecipes.utils.Constants.Companion.QUERY_NUMBER
import com.example.foodrecipes.utils.Constants.Companion.QUERY_SEARCH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    //Переменные для хранения типа меню и типа диеты
    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    //Переменная хранит данные dataStore
    val readMealAndTypeDiet = dataStoreRepository.readMealAndDietType

    //Метод для записи данных в dataStore
    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietTypes(mealType, mealTypeId, dietType, dietTypeId)
        }

    fun applyQueries(): HashMap<String, String> {
        val queries = HashMap<String, String>()
        //Чтение сохраненных данных в dataStore
        viewModelScope.launch {
            readMealAndTypeDiet.collect { values ->
                mealType = values.selectedMealType
                dietType = values.selectedDietType
            }
        }
        //Заполнение HasMap для формирования списка запроса на сервер
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_COUNT
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_MEAL] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun searchQueries(searchQuery: String): HashMap<String, String> {
        val queries = HashMap<String, String>()

        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    //Для уведомления пользователя, когда интернет появится снова
    var isOffline = false
}