package com.example.foodrecipes.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodrecipes.R
import com.example.foodrecipes.adapters.RecipesAdapter
import com.example.foodrecipes.databinding.FragmentSearchRecipesBinding
import com.example.foodrecipes.models.Result
import com.example.foodrecipes.utils.NetworkResult
import com.example.foodrecipes.viewmodels.MainViewModel
import com.example.foodrecipes.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchRecipesFragment : Fragment(R.layout.fragment_search_recipes),
    androidx.appcompat.widget.SearchView.OnQueryTextListener, RecipesAdapter.OnItemClickListener {

    private val mainViewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private val binding: FragmentSearchRecipesBinding by viewBinding()
    private val recipesAdapter by lazy { RecipesAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setHasOptionsMenu(true)
    }

    //Создание меню для поиска рецептов
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Добавление к макету фрагмента элемента меню
        inflater.inflate(R.menu.recipes_action_menu, menu)
        val actionSearch = menu.findItem(R.id.action_search)
        val searchView = actionSearch.actionView as? androidx.appcompat.widget.SearchView
        //Отображение кнопки отправки поискового запроса, когда запрос не пустой
        searchView?.isSubmitButtonEnabled = true
        //Задает прослушивателя действий в SearchView
        searchView?.setOnQueryTextListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun searchApiData(searchQuery: String) {
        Log.d("Recipes Fragment", "searchApiData() called")
        mainViewModel.searchRecipes(recipesViewModel.searchQueries(searchQuery))

        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner) { response ->
            when(response) {
                is NetworkResult.Success -> {
                    //hideShimmerEffect() TODO Add loading animation
                    response.data?.let { recipesAdapter.setNewData(it) }
                }
                is NetworkResult.Error -> {
                    //hideShimmerEffect() TODO Add loading animation
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading ->
                    Toast.makeText(context, "LOADING", Toast.LENGTH_SHORT).show()
            //showShimmerEffect() TODO Add loading animation
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchApiData(query)
        }
        return true
    }

    //РЕАЛИЗАЦИЯ МЕТОДОВ ИНТЕРФЕЙСА SearchView.OnQueryTextListener
    //Обработчик нажатий пользователя в SearchView
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    //Вызывается в случае изменения введеного текста пользователя
    override fun onClick(result: Result) {
        val action = SearchRecipesFragmentDirections.actionSearchRecipesFragment2ToSingleRecipeFragment(result)
        findNavController().navigate(action)
    }


    private fun setupRecyclerView() {
        binding.recyclerview.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}