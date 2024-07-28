package com.example.gethealthy.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.gethealthy.R
import com.example.gethealthy.presentation.theme.GetHealthyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val context = LocalContext.current
    GetHealthyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                items(listOf(
                    ChipData("Adım", R.drawable.walk, Color.Cyan) { context.startActivity(Intent(context, StepCounter::class.java)) },
                    ChipData("Nabız", R.drawable.hearth, Color.Red) { context.startActivity(Intent(context, HearthRate::class.java)) },
                    ChipData("Kalori", R.drawable.calorie, Color.Red) { context.startActivity(Intent(context, Calories::class.java)) },
                    ChipData("Bilgilerim", R.drawable.info, Color.Yellow) { context.startActivity(Intent(context,UserInfo::class.java)) }
                )) { chip ->
                    Chip(
                        onClick = chip.onClick,
                        label = { Text(chip.label) },
                        icon = {
                            Icon(
                                painter = painterResource(id = chip.iconRes),
                                contentDescription = chip.label,
                                modifier = Modifier
                                    .size(ChipDefaults.LargeIconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray, iconColor = chip.iconColor)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

data class ChipData(val label: String, val iconRes: Int, val iconColor: Color, val onClick: () -> Unit)

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
