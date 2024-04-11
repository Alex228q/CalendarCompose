package com.example.compositionlocal

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compositionlocal.ui.theme.CompositionLocalTheme
import com.example.compositionlocal.util.WorkShifts
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        setContent {
            CompositionLocalTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(sharedPreferences)
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
            .padding(start = 8.dp, top = 6.dp, end = 8.dp)
    ) {
        days.forEach { day ->
            val isWeekend = day == "СБ" || day == "ВС"
            Text(
                text = day,
                color = if (isWeekend) Color.Red else MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


@Composable
fun MainScreen(sp: SharedPreferences) {
    var numberOfTeam by remember {
        mutableIntStateOf(sp.getInt("crew", 1))
    }

    val handleChangeTeam = { team: Int -> numberOfTeam = team }
    val workShifts = WorkShifts(numberOfTeam)
    val days = workShifts.getDays()
    val evening = workShifts.getEvening()
    val night = workShifts.getNight()
    Calendar(days, evening, night, handleChangeTeam, sp)
}


@Composable
fun ButtonsBlock(
    selectedButton: Int,
    onChange: (Int) -> Unit,
    onChangeTeam: (Int) -> Unit,
    onUpdateSh: (Int) -> Unit
) {
    val buttons = listOf(1, 2, 3, 4)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 18.dp)
    ) {
        buttons.forEachIndexed { index, text ->
            OutlinedButton(
                onClick = {
                    onChange(index)
                    onChangeTeam(index + 1)
                    onUpdateSh(index + 1)
                },
                border = BorderStroke(
                    width = 3.dp,
                    color = if (selectedButton - 1 == index) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            ) {
                Text(
                    text = text.toString(),
                    color = if (selectedButton - 1 == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun Calendar(
    days: List<String>,
    evening: List<String>,
    night: List<String>,
    onChangeTeam: (Int) -> Unit,
    sp: SharedPreferences
) {

    val editor = sp.edit()
    val updateSatedPreference = { crew: Int ->
        editor.putInt("crew", crew).apply()
    }
    var selectedButton by remember {
        mutableIntStateOf(sp.getInt("crew", 1))
    }


    val handleChangeButton = { numButton: Int -> selectedButton = numButton + 1 }
    var isVisibleCurrentDate by remember { mutableStateOf(false) }
    var currentYearMonth by remember {
        mutableStateOf(YearMonth.now(ZoneId.systemDefault()))
    }
    val handlePlusYear = {
        currentYearMonth = currentYearMonth.plusYears(1)
        if (!isVisibleCurrentDate) isVisibleCurrentDate = true
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
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 22.dp),
    ) {

        MonthAndYear(currentYearMonth, handlePlusYear)
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
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

                        val color = when (dayInMonth.toString()) {
                            in days -> Color(0Xff6FF089)
                            in evening -> Color(0XFF4FBBFF)
                            in night -> Color(0XFFFF83A4)
                            else -> Color.Transparent
                        }
                        val indexPlusOne = index + 1
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                text = indexPlusOne.toString(),
                                color = if (currentDate == dayInMonth) Color.Red else MaterialTheme.colorScheme.onPrimary
                            )
                            HorizontalDivider(
                                thickness = 4.dp,
                                modifier = Modifier
                                    .width(14.dp)
                                    .clip(RoundedCornerShape(60)),
                                color = color
                            )
                        }
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
                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append(formatter.format(currentDate))
                        }
                    }
                    Text(
                        textDecoration = TextDecoration.Underline,
                        text = annotatedString,
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
        ButtonsBlock(
            selectedButton,
            handleChangeButton,
            onChangeTeam,
            updateSatedPreference
        )
    }
}

@Composable
fun MonthAndYear(currentYearMonth: YearMonth, onYearTap: () -> Unit) {
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
            color = MaterialTheme.colorScheme.onPrimary,
            text = month,
            fontSize = 26.sp,
            fontWeight = FontWeight.W500
        )
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = currentYearMonth.year.toString(),
            fontSize = 26.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier.clickable {
                onYearTap()
            }
        )
    }
}

