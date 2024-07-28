package com.example.gethealthy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.gethealthy.presentation.theme.GetHealthyTheme

class StepDatas : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        val db = Database(this)
        val stepDataList = db.getLast7DaysStepData()
        val userInfo = db.getUserInfo()

        setContent {
            WearApp(stepDataList = stepDataList, stepGoal = userInfo?.stepGoal ?: 10000)
        }
    }
}

@Composable
fun WearApp(stepDataList: List<StepData>, stepGoal: Int) {
    GetHealthyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            StepProgressList(stepDataList = stepDataList, stepGoal = stepGoal)
        }
    }
}

@Composable
fun StepProgressList(stepDataList: List<StepData>, stepGoal: Int) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(stepDataList) { stepData ->
            StepProgress(stepData = stepData, stepGoal = stepGoal)
        }
    }
}

@Composable
fun StepProgress(stepData: StepData, stepGoal: Int) {
    val progress = stepData.steps / stepGoal.toFloat()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${stepData.date}: \n ${stepData.steps} / $stepGoal",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(bottom = 5.dp, top = 2.dp),
            fontSize = 12.sp,
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp),
            color= Color.Green
        )
    }
}
