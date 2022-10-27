package com.example.foodrecipes.viewmodels

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.foodrecipes.data.Repository
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.data.database.entities.RecipesEntity
import com.example.foodrecipes.data.database.entities.ShopEntity
import com.example.foodrecipes.fragments.SingleRecipeFragment
import com.example.foodrecipes.models.ExtendedIngredient
import com.example.foodrecipes.models.Recipes
import com.example.foodrecipes.utils.NetworkResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
//Наследуем от AndroidViewModel, чтобы можно было использовать Application Context
) : AndroidViewModel(application) {

    val ingredientsList = mutableSetOf<String?>()

    //ROOM
    //Переменная хранит список рецептов, полученных из БД
    val readRecipes: LiveData<List<RecipesEntity>> =
        repository.local.readRecipes().asLiveData()
    //Метод для "вставки" рецептов в БД, выполняется в Dispatchers.IO(чтобы не нагружать основной поток)
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    //Внести в БД любимый рецепт
    fun insertFavoriteRecipe(favoriteEntity: FavoriteEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipe(favoriteEntity)
        }
    //Получить список всех любимых рецептов из БД
    val readFavoriteRecipes: LiveData<List<FavoriteEntity>> =
        repository.local.readFavoriteRecipes().asLiveData()
    //Удалить из БД любимый рецепт
    fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoriteEntity)
        }

    //Внесение ингредиента в БД
    fun insertShopList(shopEntity: ShopEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertShopList(shopEntity)
        }

    //Получить список всех добавленных в список покупок ингредиентов
    val readShopList: LiveData<List<ShopEntity>> =
        repository.local.readShopList().asLiveData()

    //Удаление ингредиента из списка покупок
    fun deleteShopList(shopEntity: ShopEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteShopList(shopEntity)
        }

    //RETROFIT
    val recipesResponse: MutableLiveData<NetworkResult<Recipes>> = MutableLiveData()
    val searchRecipesResponse: MutableLiveData<NetworkResult<Recipes>> = MutableLiveData()

    //1. Метод для получения ответа от сервера, запуск в корутине метода для "безопасного" вызова
    @RequiresApi(Build.VERSION_CODES.M)
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun searchRecipes(queries: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(queries)
    }

    /**
     * Изначально переменной recipesResponse присваивается значение Loading()
     * 1) Идет проверка на наличие интернет-соединение
     * 2) Переменной response присваиваем значение, полученное в результате запроса от сервера
     * 3) Обработка полученного ответа от сервера
     * 4) В зависимости от полученного и обработанного ответа от сервера(пункт 3) присваивается значение переменной recipesResponse
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                var response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleRecipeResponse(response)
                /**
                 * 1) Переменной recipes присваивается значение полученное от сервера, тип - data class
                 * 2) Если recipes не null - закэшировать данные в БД
                 */
                var recipes = recipesResponse.value!!.data
                if (recipes != null) {
                    offlineCacheRecipes(recipes)
                }
                //Если в результате запроса, получен Exception - присвоить переменной recipesResponse - значение Error
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found")
                Log.e("RecipesViewModel", e.message.toString())
            }
        } else {
            //Если не было интернет соединения - присвоить переменной recipesResponse - значение Error
            recipesResponse.value = NetworkResult.Error("Has no internet connection")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleRecipeResponse(response)

            } catch (e: Exception) {
                searchRecipesResponse.value = NetworkResult.Error("Recipes not found.")
                Log.e("RecipesViewModel", e.message.toString())
            }
        } else {
            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Метод для проверки интернет соединения.
     *т.к для создания системного сервиса ConnectivityManager требуется Context, а передать context из Activity нельзя
     * по той причине, что:
     * 1) Активити может быть уничтожена, тогда произойдет краш приложения
     * 2) ViewModel создается для того, чтобы рзаделить Ui и логику приложения
     * передаем Application Context т.к приложение живет на всем этапе работы приложения.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun handleRecipeResponse(response: Response<Recipes>): NetworkResult<Recipes> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("Api Key Limited")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("No Recipes Found")
            }
            response.isSuccessful -> {
                val recipeData = response.body()
                return NetworkResult.Success(recipeData!!)
            }
            else -> return NetworkResult.Error(response.message())
        }
    }

    private fun offlineCacheRecipes(recipes: Recipes) {
        val recipesEntity = RecipesEntity(recipes)
        insertRecipes(recipesEntity)
    }
}