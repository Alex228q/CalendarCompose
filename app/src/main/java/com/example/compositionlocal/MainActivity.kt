package com.example.compositionlocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compositionlocal.ui.theme.CompositionLocalTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Calendar()
                }
            }
        }
    }
}


@Composable
fun DaysOfWeek() {
    val days = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        days.forEach { day ->
            val isWeekend = day == "СБ" || day == "ВС"
            Text(
                text = day,
                color = if (isWeekend) Color.Red else Color.Black,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


@Composable
fun Calendar() {

    var selectedButton by remember {
        mutableIntStateOf(0)
    }

    val handleChangeButton = { numButton: Int -> selectedButton = numButton }
    var isVisibleCurrentDate by remember { mutableStateOf(false) }
    var currentYearMonth by remember {
        mutableStateOf(YearMonth.now(ZoneId.systemDefault()))
    }


    val firstDayOfMonth = currentYearMonth.atDay(1)

    val dates = mutableListOf<LocalDate>()
    var date = firstDayOfMonth
    while (date.month == currentYearMonth.month) {
        dates.add(date)
        date = date.plusDays(1)
    }
    val currentDate = LocalDate.now()
    val emptyCells = currentYearMonth.atDay(1).dayOfWeek.value - 1

    var offsetX by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 22.dp, horizontal = 12.dp),
    ) {

        MonthAndYear(currentYearMonth)
        HorizontalDivider(
            thickness = 2.dp
        )
        Spacer(Modifier.height(16.dp))

        DaysOfWeek()

        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            label = "DayA",
            targetState = currentYearMonth,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally(
                        animationSpec = tween(easing = LinearOutSlowInEasing),
                        initialOffsetX = { height -> height }
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { height -> -height }
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = tween(easing = LinearOutSlowInEasing),
                        initialOffsetX = { height -> -height }
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { height -> height }
                    )
                }.using(
                    SizeTransform(clip = false)
                )
            }
        )
        { currentTarget ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(offsetX) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            when {
                                dragAmount > 0 -> {
                                    currentYearMonth = currentTarget.minusMonths(1)
                                    offsetX += dragAmount
                                    isVisibleCurrentDate =
                                        YearMonth.now(ZoneId.systemDefault()) != currentYearMonth

                                }

                                dragAmount < 0 -> {
                                    currentYearMonth = currentTarget.plusMonths(1)
                                    offsetX -= dragAmount
                                    isVisibleCurrentDate =
                                        YearMonth.now(ZoneId.systemDefault()) != currentYearMonth
                                }
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(emptyCells) {
                    Box(
                        modifier = Modifier

                            .size(50.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
                itemsIndexed(dates) { index, dayInMonth ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                    ) {
                        val indexPlusOne = index + 1
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            text = indexPlusOne.toString(),
                            color = if (currentDate == dayInMonth) Color.Red else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            label = "DateA",
            visible = isVisibleCurrentDate,
            enter = fadeIn(animationSpec = tween(800)),
            exit = fadeOut(animationSpec = tween(800))
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                Box(
                    contentAlignment = Alignment.Center,
                ) {

                    Text(
                        text = formatter.format(currentDate),
                        modifier = Modifier
                            .clickable {
                                currentYearMonth = YearMonth.now(ZoneId.systemDefault())
                                isVisibleCurrentDate = false
                            },
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonsBlock(selectedButton, handleChangeButton)
    }
}

@Composable
fun MonthAndYear(currentYearMonth: YearMonth) {
    val month = when (currentYearMonth.monthValue) {
        1 -> "ЯНВАРЬ"
        2 -> "ФЕВРАЛЬ"
        3 -> "МАРТ"
        4 -> "АПРЕЛЬ"
        5 -> "МАЙ"
        6 -> "ИЮНЬ"
        7 -> "ИЮЛЬ"
        8 -> "АВГУСТ"
        9 -> "СЕНТЯБРЬ"
        10 -> "ОКТЯБРЬ"
        11 -> "НОЯБРЬ"
        else -> "ДЕКАБРЬ"
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {

        Text(
            text = month,
            fontSize = 24.sp,
            fontWeight = FontWeight.W500
        )
        Text(
            text = currentYearMonth.year.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
fun ButtonsBlock(selectedButton: Int, onChange: (Int) -> Unit) {
    val buttons = listOf(1, 2, 3, 4)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 18.dp)
    ) {
        buttons.forEachIndexed { index, text ->
            OutlinedButton(
                onClick = { onChange(index) },
                border = BorderStroke(
                    2.dp,
                    if (selectedButton == index) MaterialTheme.colorScheme.primary else Color.Transparent
                ),
            ) {
                Text(
                    text = text.toString(),
                    fontSize = if (selectedButton == index) 22.sp else 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    CompositionLocalTheme {
        Calendar()
    }
}

