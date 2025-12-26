package com.par9uet.jm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import com.par9uet.jm.ui.components.CommonScaffold
import kotlinx.coroutines.flow.filter
import java.time.LocalDate

private val weekTextMap = mapOf(
    1 to "一",
    2 to "二",
    3 to "三",
    4 to "四",
    5 to "五",
    6 to "六",
    7 to "七",
)

@Composable
fun rememberFirstVisibleMonthAfterScroll(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleMonth.value = state.firstVisibleMonth }
    }
    return visibleMonth.value
}

@Composable
fun SignInScreen() {
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek() }
    val currentMonth = remember(today) { today.yearMonth }
    val startMonth = remember { currentMonth.minusMonths(500) }
    val endMonth = remember { currentMonth.plusMonths(500) }
    CommonScaffold(
        title = "每日签到"
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val state = rememberCalendarState(
                    startMonth = startMonth,
                    endMonth = endMonth,
                    firstVisibleMonth = currentMonth,
                    firstDayOfWeek = daysOfWeek.first(),
                    outDateStyle = OutDateStyle.EndOfGrid,
                )
                val visibleMonth = rememberFirstVisibleMonthAfterScroll(state)
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = visibleMonth.yearMonth.toString(),
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
            }
            HorizontalCalendar(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = rememberCalendarState(),
                calendarScrollPaged = true,
                contentHeightMode = ContentHeightMode.Fill,
                monthHeader = {
                    Row(
                        Modifier
                            .padding(bottom = 1.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(vertical = 10.dp),
                    ) {
                        for (dayOfWeek in daysOfWeek) {
                            Text(
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                text = weekTextMap[dayOfWeek.value]!!
                            )
                        }
                    }
                },
                dayContent = { day ->
                    if (day.position == DayPosition.MonthDate) {
                        Day(
                            day = day,
                            isToday = day.date == today,
                        )
                    }
                }
            )
            Button(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .height(46.dp)
                    .fillMaxWidth(),
                onClick = {

                }
            ) {
                Text("签到")
            }
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    isToday: Boolean,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = when {
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainer
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = 15.sp,
                color = when {
                    day.position == DayPosition.OutDate -> MaterialTheme.colorScheme.secondary
                    else -> Color.Unspecified
                }
            )
            Text(text = "额外奖励", fontSize = 10.sp)
        }
    }
}