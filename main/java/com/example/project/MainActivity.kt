package com.example.project

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row


import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.project.data.Meal
import com.example.project.ui.theme.ProjectTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = viewModel()

            ProjectTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "startOrder",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("startOrder") {
                            StartOrderScreen(
                                onNextButtonClicked = { navController.navigate("mealLocation") }, // "mealLocation" 화면으로 이동
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                navController = navController
                            )
                        }

                        composable("mealLocation") {
                            MealLocationScreen(
                                navController = navController,
                                onLocationSelected = { navController.navigate("mealDetail/$it") }, // 선택한 위치에 따라 "mealDetail"로 이동
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                        }

                        composable("mealDetail/{mealLocation}") { backStackEntry ->
                            val mealLocation = backStackEntry.arguments?.getString("mealLocation") ?: ""
                            MealDetailScreen(
                                navController = navController,
                                mealLocation = mealLocation,
                            )
                        }

                        composable(
                            route = "mealRating/{mealLocation}/{imageUri}/{foodList}",
                            arguments = listOf(
                                navArgument("mealLocation") { type = NavType.StringType },
                                navArgument("imageUri") { type = NavType.StringType },
                                navArgument("foodList") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val mealLocation = backStackEntry.arguments?.getString("mealLocation") ?: ""
                            val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
                            val foodList = backStackEntry.arguments?.getString("foodList")?.split(",") ?: listOf()

                            MealRatingScreen(
                                viewModel = mainViewModel,
                                mealLocation = mealLocation,
                                imageUri = imageUri,
                                foodList = foodList,
                                navController = navController,
                                modifier = Modifier,
                            )
                        }

                        composable(route = "viewMeals") {
                            ViewMealsScreen(
                                viewModel = mainViewModel,
                                navController = navController
                            )
                        }

                        composable(
                            route = "mealDetailView/{date}/{location}/{mealType}/{name}/{price}/{calorie}/{evaluation}/{imageUri}",
                            arguments = listOf(
                                navArgument("date") { type = NavType.StringType },
                                navArgument("location") { type = NavType.StringType },
                                navArgument("mealType") { type = NavType.StringType },
                                navArgument("name") { type = NavType.StringType },
                                navArgument("price") { type = NavType.StringType },
                                navArgument("calorie") { type = NavType.StringType },
                                navArgument("evaluation") { type = NavType.StringType },
                                navArgument("imageUri") { type = NavType.StringType; nullable = true }
                            )
                        ) { backStackEntry ->
                            val date = backStackEntry.arguments?.getString("date") ?: ""
                            val location = backStackEntry.arguments?.getString("location") ?: ""
                            val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
                            val name = backStackEntry.arguments?.getString("name")?.split(",") ?: listOf()
                            val price = backStackEntry.arguments?.getString("price") ?: ""
                            val calorie = backStackEntry.arguments?.getString("calorie") ?: ""
                            val evaluation = backStackEntry.arguments?.getString("evaluation") ?: ""
                            val imageUri = backStackEntry.arguments?.getString("imageUri")?.let { Uri.parse(it) }

                            MealDetailView(
                                meal = Meal(
                                    date = date,
                                    location = location,
                                    mealType = mealType,
                                    name = name,
                                    price = price,
                                    calorie = calorie,
                                    evaluation = evaluation,
                                    imageUri = imageUri
                                ),
                                navController = navController
                            )
                        }

                        composable(route = "analysis") {
                            MealAnalysisScreen(
                                viewModel = mainViewModel,
                                navController = navController,
                                modifier = Modifier,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartOrderScreen(
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "학생 식단 관리 앱",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            val images = listOf(
                R.drawable.image_2, R.drawable.image_3, R.drawable.image_4,
                R.drawable.image_5, R.drawable.image_6
            )

            images.forEach { imageRes ->
                val icon = painterResource(id = imageRes)
                Image(
                    painter = icon,
                    contentDescription = "아이콘",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(horizontal = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFC0CEDB))
                .padding(16.dp)
                .padding(top = 2.dp)

        )

        {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = 14.dp)

            )
            // 식사 입력하기 버튼
            Button(
                onClick = { onNextButtonClicked() },
                modifier = Modifier
                    .width(260.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                    .align(Alignment.TopCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            )

            {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "식사 입력하기",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                }
            }

            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = 170.dp)

            )
            // 입력한 식사보기 버튼
            Button(
                onClick = { navController.navigate("viewMeals") }, //이동
                modifier = Modifier
                    .width(260.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                    .align(Alignment.Center),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "입력한 식사보기",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = 327.dp)

            )
            // 식사 분석하기 버튼
            Button(
                onClick = { navController.navigate("analysis") }, //이동
                modifier = Modifier
                    .width(260.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "식사 분석하기",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }
}



//두번째페이지
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealLocationScreen(
    navController: NavHostController,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "학생 식단 관리 앱", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "식사 장소를 선택하세요!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 식당
                Text(
                    text = "식당",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFC0CEDB))
                        .padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_9),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopStart)
                            .padding(end = 8.dp)

                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 상록원 2층
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onLocationSelected("상록원 2층") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                                    .padding(horizontal = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                            ) {
                                Text(text = "상록원 2층",color = Color.Black)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        // 상록원 3층
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onLocationSelected("상록원 3층") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                                    .padding(horizontal = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                            ) {
                                Text(text = "상록원 3층",color = Color.Black)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        // 남산학사
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { navController.navigate("mealDetail/남산학사") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                                    .padding(horizontal = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                            ) {
                                Text(text = "남산학사",color = Color.Black)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }

                // 카페
                Text(
                    text = "카페",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFC0CEDB))
                        .padding(22.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_10),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopStart)
                            .padding(end = 8.dp)

                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 카페 ING
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onLocationSelected("카페 ING") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                                    .padding(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                            ) {
                                Text(text = "카페 ING",color = Color.Black)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        // 블루팟
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onLocationSelected("블루팟") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                                    .padding(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                            ) {
                                Text(text = "블루팟",color = Color.Black)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    )
}





//드신 메뉴 알려주세요 페이지
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    navController: NavHostController,
    mealLocation: String,
    modifier: Modifier = Modifier
) {
    var foodName by remember { mutableStateOf("") }
    var foodList by remember { mutableStateOf(listOf<String>()) }
    var mealImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> mealImageUri = uri }
    )

    val isNextButtonEnabled = mealImageUri != null && foodList.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "식당 선택: $mealLocation",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val encodedMealLocation = URLEncoder.encode(mealLocation, StandardCharsets.UTF_8.toString())
                    val encodedImageUri = URLEncoder.encode(mealImageUri?.toString() ?: "", StandardCharsets.UTF_8.toString())
                    val encodedFoodList = URLEncoder.encode(foodList.joinToString(","), StandardCharsets.UTF_8.toString())
                    navController.navigate("mealRating/$encodedMealLocation/$encodedImageUri/$encodedFoodList")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                ,colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ,enabled = isNextButtonEnabled
            ) {
                Text("다음",color = Color.Black)
            }
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "드신 메뉴를 알려주세요!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFC0CEDB))
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_7),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopStart)
                    )
                    ImagePickerBox(mealImageUri, imagePickerLauncher)
                }

                // 사진 불러오기 버튼
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 16.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                        .clip(RoundedCornerShape(16.dp))
                    ,colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray) // 버튼 색상 설정
                ) {
                    Text(text = "사진 불러오기",color = Color.Black)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFC0CEDB))
                        .padding(12.dp)
                ) {
                    AddFoodItemRow(
                        foodName = foodName,
                        onFoodNameChange = { foodName = it },
                        onAddFoodClick = {
                            if (foodName.isNotBlank()) {
                                foodList = foodList + foodName
                                foodName = ""
                            }
                        }
                    )
                }

                FoodList(
                    foodList = foodList,
                    onRemoveFoodClick = { food -> foodList = foodList - food }
                )
            }
        }
    )
}

@Composable
fun ImagePickerBox(mealImageUri: Uri?, imagePickerLauncher: ActivityResultLauncher<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (mealImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(mealImageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp))
            )
        } else {
            Text(text = "사진을 추가하세요", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun AddFoodItemRow(
    foodName: String,
    onFoodNameChange: (String) -> Unit,
    onAddFoodClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFC0CEDB))
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_8),
                contentDescription = "Image",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopStart)
            )

            OutlinedTextField(
                value = foodName,
                onValueChange = onFoodNameChange,
                label = { Text("음식 이름") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddFoodClick() })
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAddFoodClick,
            modifier = Modifier
                .clip(RoundedCornerShape(27.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                .background(Color.LightGray),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("추가",color = Color.Black)
        }
    }
}


@Composable
fun FoodList(
    foodList: List<String>,
    onRemoveFoodClick: (String) -> Unit
) {
    foodList.forEach { food ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = food,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { onRemoveFoodClick(food) },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                ,colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("삭제",color = Color.Black)
            }
        }
    }
}




//평점 및 소감 입력페이지
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealRatingScreen(
    viewModel: MainViewModel,
    foodList: List<String>,
    mealLocation: String,
    imageUri: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableIntStateOf(0) }
    var mealDate by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }
    var mealCost by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0')
            val formattedDay = selectedDay.toString().padStart(2, '0')
            mealDate = "$selectedYear-$formattedMonth-$formattedDay"
        }, year, month, day
    )

    val isSaveButtonEnabled = mealDate.isNotEmpty() &&
            mealType.isNotEmpty() &&
            mealCost.isNotEmpty() &&
            rating > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "음식평 및 소감",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 14.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "음식평 및 소감!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "평점 $i",
                            tint = if (rating >= i) Color.Yellow else Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = i }
                        )
                    }
                }


                Text(
                    text = "점수: $rating 점을 선택하셨습니다.",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = feedback,
                        onValueChange = { feedback = it },
                        label = { Text("소감을 입력하세요") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .height(200.dp),
                        maxLines = 5
                    )

                    Image(
                        painter = painterResource(id = R.drawable.image_8),
                        contentDescription = "소감 이미지",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(40.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { datePickerDialog.show() }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_13),
                        contentDescription = "Image",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(26.dp)
                    )

                    Text(
                        text = mealDate.ifEmpty { "식사 날짜" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (mealDate.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { expanded = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_14),
                        contentDescription = "Image",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(26.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = mealType.ifEmpty { "식사 종류" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (mealType.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "드롭다운 열기",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("조식") },
                            onClick = {
                                mealType = "조식"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("중식") },
                            onClick = {
                                mealType = "중식"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("석식") },
                            onClick = {
                                mealType = "석식"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("간식/음료") },
                            onClick = {
                                mealType = "간식/음료"
                                expanded = false
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = mealCost,
                        onValueChange = { mealCost = it },
                        label = { Text("식사 비용을 입력하세요") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(120.dp),
                    )

                    Image(
                        painter = painterResource(id = R.drawable.image_15),
                        contentDescription = "비용 이미지",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(40.dp)
                    )
                }

                Button(
                    onClick = {
                        val meal = Meal(
                            name = foodList,
                            mealType = mealType,
                            date = mealDate,
                            location = mealLocation.replace("+", " "),
                            imageUri = Uri.parse(imageUri),
                            price = mealCost,
                            calorie = kotlin.random.Random.nextInt(300, 1001).toString(),
                            evaluation = feedback
                        )

                        viewModel.addMeal(meal)

                        navController.navigate("startOrder") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = isSaveButtonEnabled,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("저장하기",color = Color.Black)
                }
            }
        }
    )
}


//입력한 식사보기 페이지
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewMealsScreen(
    viewModel: MainViewModel,
    navController: NavHostController, // 네비게이션 컨트롤러로 화면 전환
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {},
        content = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "입력한 식사 목록",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate("startOrder") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true // startOrder로 돌아갈 때 이전 화면들을 모두 스택에서 제거
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(16.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    val imageIds = listOf(
                        R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5, R.drawable.image_6, R.drawable.image_12, R.drawable.image_10
                    )

                    imageIds.forEach { imageId ->
                        Image(
                            painter = painterResource(id = imageId),
                            contentDescription = "식사 이미지",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // viewModel에서 meals 데이터를 불러와 각 식사 항목을 표시
                    items(viewModel.meals) { meal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // 카드 클릭 시 해당 식사의 상세 페이지로 이동
                                    navController.navigate(
                                        "mealDetailView/${URLEncoder.encode(meal.date, "UTF-8")}/${URLEncoder.encode(meal.location, "UTF-8")}/${URLEncoder.encode(meal.mealType, "UTF-8")}/${URLEncoder.encode(meal.name.joinToString(","), "UTF-8")}/${meal.price}/${meal.calorie}/${URLEncoder.encode(meal.evaluation, "UTF-8")}/${URLEncoder.encode(meal.imageUri?.toString() ?: "", "UTF-8")}"
                                    )
                                }
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFC0CEDB))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "날짜: ${meal.date}",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "위치: ${meal.location}",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "종류: ${meal.mealType}",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "이름: ${meal.name.joinToString(", ")}",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "비용: ${meal.price}원",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "칼로리: ${meal.calorie}kcal",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "평가: ${meal.evaluation}",
                                    fontWeight = FontWeight.Bold
                                )
                                // 식사 이미지 표시
                                meal.imageUri?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AsyncImage(
                                        model = it,
                                        contentDescription = "식사 이미지",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colorScheme.surface)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}



// 식사 상세 정보를 보여주는 화면
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailView(
    meal: Meal,
    navController: NavHostController, // 네비게이션 컨트롤러
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "식사 상세 정보",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = meal.date,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 식사 이미지
                    meal.imageUri?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "식사 이미지",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(bottom = 16.dp)
                        )
                    }

                    // 각 항목에 대한 레이아웃
                    InfoRow(label = "위치", value = meal.location)
                    InfoRow(label = "종류", value = meal.mealType)
                    InfoRow(label = "이름", value = meal.name.joinToString(", "))
                    InfoRow(label = "비용", value = "${meal.price}원")
                    InfoRow(label = "칼로리", value = "${meal.calorie}kcal")
                    InfoRow(label = "평가", value = meal.evaluation)
                }
            }
        }
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



// 식사 분석 화면
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealAnalysisScreen(
    viewModel: MainViewModel, // ViewModel을 통한 데이터 관리
    navController: NavHostController, // 네비게이션 컨트롤러
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "식사 분석", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val imageIds = listOf(
                        R.drawable.image_2, R.drawable.image_3, R.drawable.image_4,
                        R.drawable.image_5, R.drawable.image_6, R.drawable.image_12, R.drawable.image_10
                    )

                    imageIds.forEach { imageId ->
                        Image(
                            painter = painterResource(id = imageId),
                            contentDescription = "이미지",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp)
                        )
                    }
                }

                AnalysisCard(
                    title = "총 칼로리",
                    value = "${viewModel.getTotalCaloriesThisMonth()} kcal"
                )

                AnalysisCard(
                    title = "조식 총 비용",
                    value = "${viewModel.getTotalBreakfastCost()} 원"
                )

                AnalysisCard(
                    title = "중식 총 비용",
                    value = "${viewModel.getTotalLunchCost()} 원"
                )

                AnalysisCard(
                    title = "석식 총 비용",
                    value = "${viewModel.getTotalDinnerCost()} 원"
                )

                AnalysisCard(
                    title = "간식/음료 총 비용",
                    value = "${viewModel.getTotalSnackCost()} 원"
                )
            }
        }
    )
}

// 분석 정보를 카드 형태로 표시하는 컴포저블
@Composable
fun AnalysisCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}





