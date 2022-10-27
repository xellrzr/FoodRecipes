package com.example.foodrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipes.data.database.entities.FavoriteEntity
import com.example.foodrecipes.databinding.RecipePreviewBinding
import com.example.foodrecipes.utils.MyDiffUtil
import org.jsoup.Jsoup

class FavoriteRecipesAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FavoriteRecipesAdapter.FavoriteViewHolder>() {

    private var favoriteRecipes = emptyList<FavoriteEntity>()

    inner class FavoriteViewHolder(private val binding: RecipePreviewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteEntity: FavoriteEntity, listener: OnItemClickListener) {
            binding.apply {
                tvCookingTimer.text = favoriteEntity.result.readyInMinutes.toString()
                tvServings.text = favoriteEntity.result.servings.toString()
                tvLikes.text = favoriteEntity.result.aggregateLikes.toString()
                tvTitle.text = favoriteEntity.result.title

                /**
                recipeDescription.apply {
                    if (favoriteEntity.result.summary != null) {
                        val description = Jsoup.parse(favoriteEntity.result.summary!!).text()
                        text = description.toString()
                    }
                }
                */

                Glide.with(itemView).load(favoriteEntity.result.image).into(recipeImage)
                itemView.setOnClickListener {
                    listener.onClick(favoriteEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteRecipesAdapter.FavoriteViewHolder {
        val binding = RecipePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteRecipesAdapter.FavoriteViewHolder, position: Int) {
        val currentFavoriteRecipe = favoriteRecipes[position]
        holder.bind(currentFavoriteRecipe, listener)
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    interface OnItemClickListener {
        fun onClick(favoriteEntity: FavoriteEntity)
    }

    fun setNewData(newData: List<FavoriteEntity>) {
        val diffUtil = MyDiffUtil(favoriteRecipes, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        favoriteRecipes = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

}