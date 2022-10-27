package com.example.foodrecipes.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodrecipes.utils.Constants.Companion.PREFERENCES_DIET_TYPE
import com.example.foodrecipes.utils.Constants.Companion.PREFERENCES_DIET_TYPE_ID
import com.example.foodrecipes.utils.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.example.foodrecipes.utils.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.example.foodrecipes.utils.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

//Создание экземпляра dataStore (создание на верхнем уровне упрощает хранение DataStore, как Singleton)
private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

@ViewModelScoped
class DataStoreRepository  @Inject constructor(@ApplicationContext context: Context) {

    //Для обращения к экземпляру dataStore, в дальнейшем при обращении dataStore, не потребуется использовать context
    //Например при функции edit - context.dataStore.edit или context.dataStore.data
    private val dataStore = context.dataStore

    //Создание ключей для хранения значений
    private object PreferencesKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID)
    }

    //Метод для сохранения данных в DataStore на основе переданных данных
    suspend fun saveMealAndDietTypes(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.selectedMealType] = mealType
            preferences[PreferencesKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferencesKeys.selectedDietType] = dietType
            preferences[PreferencesKeys.selectedDietTypeId] = dietTypeId
        }
    }

    /**Сохраняем данные в переменную типа Flow<MealAndDietType> - data class, + такого метода,
     * что каждый раз, когда в dataStore обновляется значение, мы получаем уведомление.
     * Проверять обновленные значения не потребуется
     */

    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
            //В случае ошибки чтения, получить новый пустой preferences
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            MealAndDietType(
                //Если dataStore null - возвращаем значения по умолчанию
                preferences[PreferencesKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE,
                preferences[PreferencesKeys.selectedMealTypeId] ?: 0,
                preferences[PreferencesKeys.selectedDietType] ?: DEFAULT_DIET_TYPE,
                preferences[PreferencesKeys.selectedDietTypeId] ?: 0
            )
        }
}

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)