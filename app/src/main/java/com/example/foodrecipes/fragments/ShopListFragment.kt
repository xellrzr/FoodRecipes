package com.example.foodrecipes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipes.R
import com.example.foodrecipes.adapters.ShopListAdapter
import com.example.foodrecipes.data.database.entities.ShopEntity
import com.example.foodrecipes.databinding.FragmentShopListBinding
import com.example.foodrecipes.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListFragment : Fragment(R.layout.fragment_shop_list) {

    private val mainViewModel: MainViewModel by viewModels()
    private val binding: FragmentShopListBinding by viewBinding()
    private val shopListAdapter by lazy { ShopListAdapter() }

    private lateinit var shopEntity: List<ShopEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                mainViewModel.readShopList.observe(viewLifecycleOwner) { shopEntities ->
                    shopEntity = shopEntities
                }
                val result = shopEntity[position]
                mainViewModel.deleteShopList(result)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerview)
        }

        mainViewModel.readShopList.observe(viewLifecycleOwner) { shopEntity ->
            if (shopEntity.isNotEmpty()) {
                shopListAdapter.setNewData(shopEntity)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerview.apply {
            adapter = shopListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}


