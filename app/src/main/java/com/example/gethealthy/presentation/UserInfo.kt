package com.example.gethealthy.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.example.gethealthy.presentation.Database
import com.example.gethealthy.presentation.User
import com.example.gethealthy.presentation.theme.GetHealthyTheme

class UserInfo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetHealthyTheme {
                UserInfoScreen()
            }
        }
    }
}

@Composable
fun UserInfoScreen() {
    val context = LocalContext.current
    val dbHelper = remember { Database(context) }

    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf(170) }
    var weight by remember { mutableStateOf(70) }
    var stepGoal by remember { mutableStateOf(10000) }
    var age by remember { mutableStateOf(30) }

    val heightRange = (100..250).toList()
    val weightRange = (30..200).toList()
    val stepGoalRange = (1000..30000 step 1000).toList()
    val ageRange = (10..100).toList()

    LaunchedEffect(Unit) {
        val userInfo = dbHelper.getUserInfo()
        if (userInfo != null) {
            gender = userInfo.gender
            height = userInfo.height
            weight = userInfo.weight
            stepGoal = userInfo.stepGoal
            age = userInfo.age
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bilgilerim", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        GenderSelection(gender) { gender = it }

        SliderField("Boy", height, heightRange) { selectedHeight ->
            height = selectedHeight
        }
        SliderField("Kilo", weight, weightRange) { selectedWeight ->
            weight = selectedWeight
        }
        SliderField("Adım Hedefi", stepGoal, stepGoalRange) { selectedStepGoal ->
            stepGoal = selectedStepGoal
        }
        SliderField("Yaş", age, ageRange) { selectedAge ->
            age = selectedAge
        }

        Button(onClick = {
            val user = User(gender, height, weight, age, stepGoal)
            dbHelper.saveOrUpdateUserInfo(user)
            Toast.makeText(context,"Başarıyla kaydedildi",Toast.LENGTH_SHORT).show()
        }) {
            Text("Kaydet")
        }
    }
}

@Composable
fun GenderSelection(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Cinsiyet", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GenderRadioButton(selectedGender, "Kadın", onGenderSelected)
            GenderRadioButton(selectedGender, "Erkek", onGenderSelected)
        }
    }
}

@Composable
fun SliderField(label: String, currentValue: Int, range: List<Int>, onValueSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White, modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Slider(
                value = currentValue.toFloat(),
                valueRange = range.first().toFloat()..range.last().toFloat(),
                steps = range.size - 2,
                onValueChange = {
                    onValueSelected(it.toInt())
                    Log.d("SliderField", "$label selected: ${it.toInt()}")
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White
                )
            )
        }
        Text(text = currentValue.toString(), color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(top = 2.dp))
    }
}



@Composable
fun GenderRadioButton(selectedGender: String, gender: String, onGenderSelected: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(0.dp)) {
        RadioButton(
            selected = selectedGender == gender,
            onClick = { onGenderSelected(gender) },
            colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.Gray)
        )
        Text(gender, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(start = 0.dp))
    }
}
