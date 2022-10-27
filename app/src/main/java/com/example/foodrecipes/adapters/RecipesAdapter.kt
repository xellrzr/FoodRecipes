package com.example.foodrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipes.databinding.RecipePreviewBinding
import com.example.foodrecipes.models.Recipes
import com.example.foodrecipes.models.Result
import com.example.foodrecipes.utils.MyDiffUtil
import org.jsoup.Jsoup

class RecipesAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private var recipes = emptyList<Result>()

    class RecipesViewHolder(private val binding: RecipePreviewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Result, listener: OnItemClickListener) {
            binding.apply {
                //recipeTitle.text = result.title
                tvCookingTimer.text = result.readyInMinutes.toString()
                tvServings.text = result.servings.toString()
                tvLikes.text = result.aggregateLikes.toString()
                tvTitle.text = result.title
                /**
                recipeDescription.apply {
                    if (result.summary != null) {
                        val desciption = Jsoup.parse(result.summary).text()
                        text = desciption.toString()
                    }
                }
                */

                Glide.with(itemView).load(result.image).into(recipeImage)
                //recipeImage.load(result.image)
                itemView.setOnClickListener {
                    listener.onClick(result)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(result: Result)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipesViewHolder {
        val binding = RecipePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesAdapter.RecipesViewHolder, position: Int) {
        val currentResult = recipes[position]
        holder.bind(currentResult, listener)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    //Метод для проверки обновления со старыми данными
    fun setNewData(newData: Recipes) {
        val diffUtil = MyDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
