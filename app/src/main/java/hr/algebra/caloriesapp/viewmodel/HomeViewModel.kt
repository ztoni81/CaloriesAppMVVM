package hr.algebra.caloriesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.algebra.caloriesapp.model.Food
import hr.algebra.caloriesapp.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val foodRepository: FoodRepository) : ViewModel() {

    private val _foodList = MutableLiveData<List<Food>>()
    val foodList: LiveData<List<Food>> get() = _foodList

    init {
        loadFoodList()
    }

    private fun loadFoodList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val foods = foodRepository.getAll()
                withContext(Dispatchers.Main) {
                    _foodList.value = foods
                }
            }
        }
    }

    fun addFoodItemToFoodList(name: String, calorieCount: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                foodRepository.insert(Food(name = name, calorieCount = calorieCount))
                loadFoodList()
            }
        }
    }

    fun deleteFoodItem(food: Food) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                foodRepository.delete(food)
                loadFoodList()
            }
        }
    }
}