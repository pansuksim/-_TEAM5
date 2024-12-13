package com.example.project

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.project.data.Meal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {
    val meals = mutableStateListOf<Meal>()

    fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    fun getTotalCaloriesThisMonth(): Int {
        val currentDate = LocalDate.now()
        val currentMonth = currentDate.monthValue
        val currentYear = currentDate.year

        return meals
            .filter { meal ->
                val mealDate = LocalDate.parse(meal.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                mealDate.year == currentYear && mealDate.monthValue == currentMonth
            }
            .sumOf { it.calorie.toInt() }
    }

    fun getTotalBreakfastCost(): Int {
        return meals.filter { it.mealType == "조식" }.sumOf { it.price.toInt() }
    }

    fun getTotalLunchCost(): Int {
        return meals.filter { it.mealType == "중식" }.sumOf { it.price.toInt() }
    }

    fun getTotalDinnerCost(): Int {
        return meals.filter { it.mealType == "석식" }.sumOf { it.price.toInt() }
    }

    fun getTotalSnackCost(): Int {
        return meals.filter { it.mealType == "간식/음료" }.sumOf { it.price.toInt() }
    }
}