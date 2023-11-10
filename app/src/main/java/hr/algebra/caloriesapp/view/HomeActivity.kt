package hr.algebra.caloriesapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.caloriesapp.R
import hr.algebra.caloriesapp.dao.AppDatabase
import hr.algebra.caloriesapp.model.Food
import hr.algebra.caloriesapp.viewmodel.HomeViewModel
import hr.algebra.caloriesapp.repository.FoodRepository
import hr.algebra.caloriesapp.viewmodel.HomeViewModelFactory


class HomeActivity : AppCompatActivity() {
    private lateinit var rvFoodItems: RecyclerView
    private lateinit var etFoodName: EditText
    private lateinit var etCalorieCount: EditText
    private lateinit var btnSave: Button

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvFoodItems = findViewById(R.id.rvFoodItemList)
        rvFoodItems.layoutManager = LinearLayoutManager(this)
        etFoodName = findViewById(R.id.etFoodName)
        etCalorieCount = findViewById(R.id.etCalorieCount)
        btnSave = findViewById(R.id.btnSave)


        val foodDao = AppDatabase.getInstance(applicationContext).foodDao()
        val foodRepository = FoodRepository(foodDao)

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(foodRepository)
        ).get(HomeViewModel::class.java)

        homeViewModel.foodList.observe(this, Observer { foodList ->
            displayFoodList(foodList)
        })

        btnSave.setOnClickListener {
            if (isInputValid()) {
                homeViewModel.addFoodItemToFoodList(
                    etFoodName.text.toString(),
                    etCalorieCount.text.toString()
                )
                etFoodName.text.clear()
                etCalorieCount.text.clear()
            }
        }

        val divider = DividerItemDecoration(rvFoodItems.context, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let { it -> divider.setDrawable(it) }
        rvFoodItems.addItemDecoration(divider)
    }

    private fun displayFoodList(foodList: List<Food>) {
        val adapter = FoodAdapter(this, foodList, homeViewModel)
        rvFoodItems.adapter = adapter
    }

    private fun isInputValid(): Boolean {
        if (etFoodName.text.trim().isEmpty()) {
            etFoodName.error = "Please enter Food name..."
            return false
        }
        if (etCalorieCount.text.trim().isEmpty() || !etCalorieCount.text.trim()
                .isDigitsOnly() || etCalorieCount.text.trim().toString().toLong() !in 0..1000
        ) {
            etCalorieCount.error = "Please enter a number between 0 and 1000..."
            return false
        }
        return true
    }
}