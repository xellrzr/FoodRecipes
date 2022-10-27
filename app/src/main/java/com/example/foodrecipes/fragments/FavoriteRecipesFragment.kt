package com.example.foodrecipes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipes.R
import com.example.foodrecipes.adapters.FavoriteRecipesAdapter
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.databinding.FragmentFavoriteRecipesBinding
import com.example.foodrecipes.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment(R.layout.fragment_favorite_recipes), FavoriteRecipesAdapter.OnItemClickListener {

    private val mainViewModel: MainViewModel by viewModels()
    private val binding: FragmentFavoriteRecipesBinding by viewBinding()
    private val favoriteRecipesAdapter by lazy { FavoriteRecipesAdapter(this) }

    private lateinit var favoriteEntity: List<FavoriteEntity>
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        //Класс для удаления любимого рецепта из списка любимых с помощью свайпа вправо-влево
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { favoriteEntities ->
                    favoriteEntity = favoriteEntities
                }
                val result = favoriteEntity[position]
                mainViewModel.deleteFavoriteRecipe(result)
                Snackbar.make(view, "Recipe deleted", Snackbar.LENGTH_SHORT).apply {
                    //При нажатии Undo можно вернуть рецепт в список любимых
                    setAction("Undo") {
                        mainViewModel.insertFavoriteRecipe(result)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.recyclerview)
        }

        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { favoriteEntities ->
            if (favoriteEntities.isNotEmpty()) {
                favoriteRecipesAdapter.setNewData(favoriteEntities)
            }
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerview.apply {
            adapter = favoriteRecipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onClick(favoriteEntity: FavoriteEntity) {
        val result = favoriteEntity.result
        val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToSingleRecipeFragment(result)
        findNavController().navigate(action)
    }
}