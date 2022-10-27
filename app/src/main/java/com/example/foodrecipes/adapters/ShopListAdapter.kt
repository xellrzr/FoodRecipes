package com.example.foodrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipes.data.database.entities.ShopEntity
import com.example.foodrecipes.databinding.IngredientShopPreviewBinding
import com.example.foodrecipes.utils.Constants
import com.example.foodrecipes.utils.MyDiffUtil

class ShopListAdapter() : RecyclerView.Adapter<ShopListAdapter.ShopListViewHolder>() {

    private var shopList = emptyList<ShopEntity>()

    inner class ShopListViewHolder(private val binding: IngredientShopPreviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(shopEntity: ShopEntity) {
            binding.apply {
                Glide.with(itemView).load(Constants.BASE_IMAGE_URL + shopEntity.ingredients.image).into(ivIngredientImage)
                tvIngredientTitle.text = shopEntity.ingredients.name?.replaceFirstChar { it.uppercase() }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val binding = IngredientShopPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val currentIngredient = shopList[position]
        holder.bind(currentIngredient)
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    fun setNewData(newData: List<ShopEntity>) {
        val diffUtil = MyDiffUtil(shopList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        shopList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}