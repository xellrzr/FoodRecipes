package com.example.foodrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipes.databinding.IngredientPreviewBinding
import com.example.foodrecipes.models.ExtendedIngredient
import com.example.foodrecipes.utils.Constants.Companion.BASE_IMAGE_URL
import com.example.foodrecipes.utils.MyDiffUtil

class IngredientsAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    private var ingredients = emptyList<ExtendedIngredient>()

    inner class MyViewHolder(private val binding: IngredientPreviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(extendedIngredient: ExtendedIngredient, listener: OnItemClickListener) {
            binding.apply {
                Glide.with(itemView).load(BASE_IMAGE_URL + extendedIngredient.image).into(ivIngredientImage)
                tvIngredientTitle.text = extendedIngredient.name?.replaceFirstChar { it.uppercase() }
                tvIngredientUnit.text = extendedIngredient.unit
                tvIngredientAmount.text = extendedIngredient.amount.toString()
                tvIngredientOriginal.text = extendedIngredient.original
                btnAddShoplist.setOnClickListener {
                    listener.onClick(extendedIngredient)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(extendedIngredient: ExtendedIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsAdapter.MyViewHolder {
        val binding = IngredientPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsAdapter.MyViewHolder, position: Int) {
        val currentIngredient = ingredients[position]
        holder.bind(currentIngredient, listener)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun setNewData(newIngredientsList : List<ExtendedIngredient>) {
        val diffUtil = MyDiffUtil(ingredients, newIngredientsList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        ingredients = newIngredientsList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}