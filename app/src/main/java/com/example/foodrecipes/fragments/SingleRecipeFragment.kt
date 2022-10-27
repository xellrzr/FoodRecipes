package com.example.foodrecipes.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodrecipes.R
import com.example.foodrecipes.adapters.IngredientsAdapter
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.data.database.entities.ShopEntity
import com.example.foodrecipes.databinding.FragmentSingleRecipeBinding
import com.example.foodrecipes.models.*
import com.example.foodrecipes.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup

@AndroidEntryPoint
class SingleRecipeFragment : Fragment(R.layout.fragment_single_recipe), IngredientsAdapter.OnItemClickListener {

    private val binding: FragmentSingleRecipeBinding by viewBinding()
    private val mainViewModel: MainViewModel by viewModels()
    private val args: SingleRecipeFragmentArgs by navArgs()
    private lateinit var result: Result
    private val ingredientsAdapter by lazy { IngredientsAdapter(this) }

    private val savedRecipeList = mutableListOf<Int?>()
    private val instructionsRecipeList = mutableListOf<String?>()
    private var recipeId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        result = args.result

        checkSaved()

        addStep()

        binding.apply {
            Glide.with(view).load(result.image).into(ivRecipeImage)
            tvRecipeTitle.text = result.title
            tvRecipeDescription.apply {
                if (result.summary != null) {
                    val summary = Jsoup.parse(result.summary!!).text()
                    text = summary
                }
                rvRecipeIngredients.apply {
                    adapter = ingredientsAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    isNestedScrollingEnabled = false
                }
                tvSteps.text = instructionsRecipeList.joinToString()
            }

            btnAddFavorites.setOnClickListener {
                insertFavoriteRecipe()
            }
            btnRemoveFavorites.setOnClickListener {
                deleteFavoriteRecipe()
                savedRecipeList.remove(result.id)
            }

        }
        result.extendedIngredients?.let { ingredientsAdapter.setNewData(it) }
    }

    private fun insertFavoriteRecipe() {
        val favoriteEntity = FavoriteEntity(0, result)
        mainViewModel.insertFavoriteRecipe(favoriteEntity)
    }

    private fun deleteFavoriteRecipe() {
        val favoriteEntity = FavoriteEntity(recipeId, result)
        mainViewModel.deleteFavoriteRecipe(favoriteEntity)
    }

    //Проверка - сохранен ли рецепт в избранных.
    private fun checkSaved() {
        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { favoriteEntities ->
            for (entity in favoriteEntities) {
                if (entity.result.id == result.id) {
                    recipeId = entity.id
                    savedRecipeList.add(entity.result.id)
                }
            }
            if (savedRecipeList.contains(result.id)) {
                binding.btnAddFavorites.visibility = View.INVISIBLE
                binding.btnRemoveFavorites.visibility = View.VISIBLE
                Log.d("SingleRecipeFragment", "Can Remove from favorites")
            } else {
                binding.btnAddFavorites.visibility = View.VISIBLE
                binding.btnRemoveFavorites.visibility = View.INVISIBLE
                Log.d("SingleRecipeFragment", "Can Add to favorites")
            }
        }
    }

    //Получение инструкций для приготовления рецепта
    private fun addStep() {
        try {
            result.analyzedInstructions?.get(0)?.steps?.forEach { step -> step.step.let { instructionsRecipeList.add(it) } }
        }
        catch (e:Exception) {
            Log.d("SingleRecipeFragment", e.message.toString())
        }
    }

    override fun onClick(extendedIngredient: ExtendedIngredient) {
        checkShop(extendedIngredient)
    }

    private fun checkShop(extendedIngredient: ExtendedIngredient) {
        val shopEntity = ShopEntity(0, extendedIngredient)
        mainViewModel.readShopList.observe(viewLifecycleOwner) { shopEntities ->
            for (entity in shopEntities) {
                if (entity.ingredients.name == extendedIngredient.name) {
                    mainViewModel.ingredientsList.add(extendedIngredient.name)
                }
            }
            if (mainViewModel.ingredientsList.contains(extendedIngredient.name)) {
                Log.d("Single", "already added to shopping list")
            } else {
                mainViewModel.insertShopList(shopEntity)
                view?.let { Snackbar.make(it, "Ingredient added to shopping list", Snackbar.LENGTH_SHORT).show() }
            }
        }
    }

}