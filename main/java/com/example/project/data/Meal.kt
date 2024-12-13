package com.example.project.data

import android.net.Uri

data class Meal(
    val name: List<String>,
    val mealType: String,
    val date: String,
    val location: String,
    val imageUri: Uri?,
    val price: String,
    val calorie: String,
    val evaluation: String
)
