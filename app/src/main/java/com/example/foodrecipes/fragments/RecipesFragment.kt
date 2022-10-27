package com.example.foodrecipes.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodrecipes.R
import com.example.foodrecipes.adapters.RecipesAdapter
import com.example.foodrecipes.data.database.entities.RecipesEntity
import com.example.foodrecipes.databinding.FragmentRecipesBinding
import com.example.foodrecipes.models.Recipes
import com.example.foodrecipes.models.Result
import com.example.foodrecipes.utils.NetworkListener
import com.example.foodrecipes.utils.NetworkResult
import com.example.foodrecipes.utils.observeOnce
import com.example.foodrecipes.viewmodels.MainViewModel
import com.example.foodrecipes.viewmodels.RecipesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes), RecipesAdapter.OnItemClickListener {

    /**
     * SearchView - класс, который предоставляет пользователю вводить поисковый запрос
     * +реазилует интерфейс с 2 методами, для обработки нажатий пользователя.
     * так же потребуется создать макет с элементом отображения для поисковой строки
     * в макете потребуется указать - "app:actionViewClass="androidx.appcompat.widget.SearchView""
     * т.е какую view использовать
     * + потребуется переписать метод onCreateOptionsMenu и "надуть" макет
     */
    private val mainViewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private val binding: FragmentRecipesBinding by viewBinding()
    private val recipesAdapter by lazy { RecipesAdapter(this) }
    private val args by navArgs<RecipesFragmentArgs>()

    private lateinit var networkListener: NetworkListener


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        //Когда фрагмент будет в состоянии START - начать выполнение кода:
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            //Проверка на доступность интернета, в зависимости от полученного ответа(true или false) выводить Snackbar с сообщением
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                when(status) {
                    true -> {
                        if (recipesViewModel.isOffline) {
                            Snackbar.make(requireView(), "Back online", Snackbar.LENGTH_SHORT)
                                .setAction("Ok") {}.show()
                            recipesViewModel.isOffline = false
                        }
                    }
                    false -> {
                        Snackbar.make(requireView(), "No Internet Connection", Snackbar.LENGTH_SHORT)
                            .setAction("Ok") {}.show()
                        recipesViewModel.isOffline = true
                    }
                }
                //Каждый раз при смене статуса, прочитать БД
                readDatabase()
            }
        }

        binding.recipesFab.setOnClickListener{
            findNavController().navigate(R.id.action_randomRecipesFragment2_to_recipesBottomSheet)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun readDatabase() {
        lifecycleScope.launch {
            /**
             * Т.к как операция чтения из БД может быть длительная и возвращает она Flow, запускаем ее в корутине
             * Используя функцию расширения, удаляем observer после получения первого результата
             * Если БД не пуста и нет аргументов от dataStore, тогда показываем данные из БД
             */
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) {database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called()")
                    recipesAdapter.setNewData(database.first().recipes)
                    hideShimmerEffect()
                } else {
                    Log.d("Recipes Fragment", args.backFromBottomSheet.toString())
                    requestApiData()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestApiData() {
        Log.d("Recipes Fragment", "requestApiData() called")
        //Отправка запроса на сервер, со списком queries(данные взяты dataStore)
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        //Наблюдаем за полученным ответом от сервера
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            //В зависимости от результата ответа
            when(response) {
                //В случае успешного ответа
                is NetworkResult.Success -> {
                    //Убираем шимер
                    hideShimmerEffect()
                    //к данным ответа применяем метод адаптера, для установки этих значений
                    response.data?.let { recipesAdapter.setNewData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache(response)
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading ->
                    showShimmerEffect()
            }
        }
    }

    //Загрузка данных из БД, в случае, когда нет успешного ответа от сервера
    private fun loadDataFromCache(response : NetworkResult<Recipes>) {
        Log.d("Recipes Fragment", "loadDataFromCache() called")
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    recipesAdapter.setNewData(database.first().recipes)
                } else {
                    //Если в БД нет данных, произвести обработку ошибки
                    handleErrorMessage(response, database)
                }
            }
        }
    }

    //Метод принимает ответ типа NetworkResult и список БД
    //В зависимости от того, какое сообщение, производятся операции с View ответственными за отображение ошибок.
    private fun handleErrorMessage(response: NetworkResult<Recipes>, database: List<RecipesEntity>?) {
        Log.d("Recipes Fragment", "handleErrorMessage() called")
        if (response is NetworkResult.Error && database.isNullOrEmpty()) {
            binding.apply {
                errorImageView.visibility = View.VISIBLE
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = response.message.toString()
            }
        } else if (response is NetworkResult.Loading) {
            binding.apply {
                errorImageView.visibility = View.VISIBLE
                errorTextView.visibility = View.VISIBLE
            }
        } else if (response is NetworkResult.Success) {
            binding.apply {
                errorImageView.visibility = View.INVISIBLE
                errorTextView.visibility = View.INVISIBLE
            }
        }
    }


    //НАСТРОЙКА RECYCLERVIEW

    private fun setupRecyclerView() {
        binding.recyclerview.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        showShimmerEffect()
    }


    //НАСТРОЙКА ШИМЕРА
    private fun showShimmerEffect() {
        binding.apply {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE //Невидима и не занимает место макета
        }
    }

    private fun hideShimmerEffect() {
        binding.apply {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        }
    }

    override fun onClick(result: Result) {
        val action = RecipesFragmentDirections.actionRandomRecipesFragment2ToSingleRecipeFragment(result)
        findNavController().navigate(action)
    }

}