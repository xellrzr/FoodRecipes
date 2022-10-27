package com.example.foodrecipes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.dialogfragment.viewBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.foodrecipes.databinding.RecipesBottomSheetBinding
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodrecipes.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodrecipes.viewmodels.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RecipesBottomSheet : BottomSheetDialogFragment() {

    private val binding: RecipesBottomSheetBinding by viewBinding()
    private lateinit var recipesViewModel: RecipesViewModel

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Чтение данных из DataStore и обновление выбора UI
        //Сохраняется выбранный вариант меню и диеты при каждом открытии нижнего меню
        recipesViewModel.readMealAndTypeDiet.asLiveData().observe(viewLifecycleOwner) { value ->
            //Значение mealTypeChip = значению из выбранного чипса по ключу
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId, binding.dietTypeChipGroup)
        }

        //Присваивание значений переменным в зависимости от выбранного чипса
        binding.apply {
            mealTypeChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                val currentChip = group.findViewById<Chip>(checkedIds[id])
                val selectedMealType = currentChip.text.toString().lowercase()
                mealTypeChip = selectedMealType
                mealTypeChipId = checkedIds[id]
            }

            dietTypeChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                val currentChip = group.findViewById<Chip>(checkedIds[id])
                val selectedDietType = currentChip.text.toString().lowercase()
                dietTypeChip = selectedDietType
                dietTypeChipId = checkedIds[id]
            }

            //При нажатии на кнопку, вызывается метод для сохранения значения выбранных чипсов и переход на фрагмент Recipes
            applyBtn.setOnClickListener {
                recipesViewModel.saveMealAndDietType(mealTypeChip, mealTypeChipId, dietTypeChip, dietTypeChipId)

                val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRandomRecipesFragment2(true)
                findNavController().navigate(action)
            }
        }
    }


    private fun updateChip(selectedChipId: Int, selectedChipGroup: ChipGroup) {
        if (selectedChipId != 0) {
            try {
                selectedChipGroup.findViewById<Chip>(selectedChipId).isChecked = true
            } catch (e: Exception) {
                Log.e("RecipesFragment", e.message.toString())
            }
        }
    }

}