package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayStudyBarData
import com.example.data.model.QuizAttemptEntity
import com.example.data.model.QuizTrendPoint
import com.example.data.model.SubjectTopicProgress
import com.example.data.model.TopicProgressEntity
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary

// Vibrant Palette for Recharts Themes
val ChartTeal = Color(0xFF2EA391)
val ChartBlue = Color(0xFF3B82F6)
val ChartAmber = Color(0xFFF59E0B)
val ChartRose = Color(0xFFF43F5E)
val ChartPurple = Color(0xFF8B5CF6)
val GridLineColor = Color(0xFFE5E0D8)

/**
 * Recharts Card Container with clean title, badge and responsive styling
 */
@Composable
fun RechartsContainer(
    title: String,
    subtitle: String? = null,
    badgeText: String = "Recharts™",
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1F2C4638))
            .border(1.dp, Color(0xFFE5DFD4), RoundedCornerShape(22.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 16.sp
                        )
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B7770),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SageLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = SageDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

/**
 * Recharts Legend Item
 */
@Composable
fun RechartsLegendItem(
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF5E6A64),
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        )
    }
}

/**
 * Recharts Interactive Area & Line Chart
 * Visualizes quiz performance accuracy percentage over time with smooth bezier interpolation,
 * glowing data points, Cartesian grid lines, and interactive touch tooltip.
 */
@Composable
fun RechartsQuizPerformanceChart(
    points: List<QuizTrendPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No quiz attempts recorded yet", color = Color.Gray, fontSize = 13.sp)
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(points.lastIndex) }

    // Chart animation on appear
    var animationProgress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "recharts_curve_anim"
    )

    LaunchedEffect(points) {
        animationProgress = 1f
        selectedIndex = points.lastIndex
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Legend & Target Reference Line indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RechartsLegendItem(label = "Quiz Score %", color = SagePrimary)
                RechartsLegendItem(label = "Target (80%)", color = PeachAccent)
            }

            Text(
                text = "Tap points to inspect",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF8B9690),
                    fontSize = 10.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas Chart
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .testTag("recharts_quiz_line_chart")
        ) {
            val chartWidth = constraints.maxWidth.toFloat()
            val chartHeight = constraints.maxHeight.toFloat()
            val paddingLeft = 36.dp.value * 2.5f
            val paddingRight = 16.dp.value * 2.5f
            val paddingTop = 16.dp.value * 2.5f
            val paddingBottom = 26.dp.value * 2.5f

            val plotWidth = (chartWidth - paddingLeft - paddingRight).coerceAtLeast(10f)
            val plotHeight = (chartHeight - paddingTop - paddingBottom).coerceAtLeast(10f)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            if (points.isNotEmpty()) {
                                val stepX = if (points.size > 1) plotWidth / (points.size - 1) else plotWidth
                                val relativeX = offset.x - paddingLeft
                                val closestIndex = (relativeX / stepX).toInt().coerceIn(0, points.lastIndex)
                                selectedIndex = closestIndex
                            }
                        }
                    }
                    .pointerInput(points) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            if (points.isNotEmpty()) {
                                val stepX = if (points.size > 1) plotWidth / (points.size - 1) else plotWidth
                                val relativeX = change.position.x - paddingLeft
                                val closestIndex = (relativeX / stepX).toInt().coerceIn(0, points.lastIndex)
                                selectedIndex = closestIndex
                            }
                        }
                    }
            ) {
                val stepX = if (points.size > 1) plotWidth / (points.size - 1) else plotWidth

                // 1. Draw Cartesian Grid Lines (Horizontal at 0%, 25%, 50%, 75%, 100%)
                val yLevels = listOf(0, 25, 50, 75, 100)
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                yLevels.forEach { level ->
                    val yPos = paddingTop + plotHeight - (level / 100f * plotHeight)
                    drawLine(
                        color = GridLineColor,
                        start = Offset(paddingLeft, yPos),
                        end = Offset(paddingLeft + plotWidth, yPos),
                        strokeWidth = 1f,
                        pathEffect = dashEffect
                    )
                }

                // 2. Reference Line at 80% (Passing Target)
                val targetY = paddingTop + plotHeight - (0.80f * plotHeight)
                drawLine(
                    color = PeachAccent.copy(alpha = 0.65f),
                    start = Offset(paddingLeft, targetY),
                    end = Offset(paddingLeft + plotWidth, targetY),
                    strokeWidth = 1.6f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                )

                // 3. Compute Data Coordinates
                val coordinates = points.mapIndexed { index, pt ->
                    val x = paddingLeft + (index * stepX)
                    val rawY = paddingTop + plotHeight - ((pt.percentage / 100f) * plotHeight)
                    // Animate Y position from bottom
                    val animatedY = (paddingTop + plotHeight) - ((paddingTop + plotHeight - rawY) * animatedProgress)
                    Offset(x, animatedY)
                }

                // 4. Draw Smooth Cubic Spline / Area Gradient
                if (coordinates.size >= 2) {
                    val linePath = Path().apply {
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            val p0 = coordinates[i]
                            val p1 = coordinates[i + 1]
                            val controlX1 = p0.x + (p1.x - p0.x) / 2f
                            val controlY1 = p0.y
                            val controlX2 = p0.x + (p1.x - p0.x) / 2f
                            val controlY2 = p1.y
                            cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                        }
                    }

                    // Area Fill Path
                    val areaPath = Path().apply {
                        addPath(linePath)
                        lineTo(coordinates.last().x, paddingTop + plotHeight)
                        lineTo(coordinates.first().x, paddingTop + plotHeight)
                        close()
                    }

                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SagePrimary.copy(alpha = 0.35f),
                                SagePrimary.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            startY = paddingTop,
                            endY = paddingTop + plotHeight
                        )
                    )

                    // Stroke Path
                    drawPath(
                        path = linePath,
                        color = SagePrimary,
                        style = Stroke(width = 3.2f, cap = StrokeCap.Round)
                    )
                }

                // 5. Draw Interactive Scrub Indicator Line & Point Dots
                coordinates.forEachIndexed { index, coord ->
                    val isSelected = (index == selectedIndex)

                    if (isSelected) {
                        // Vertical scrubber line
                        drawLine(
                            color = SagePrimary.copy(alpha = 0.4f),
                            start = Offset(coord.x, paddingTop),
                            end = Offset(coord.x, paddingTop + plotHeight),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )

                        // Outer pulse ring
                        drawCircle(
                            color = SagePrimary.copy(alpha = 0.25f),
                            radius = 11f,
                            center = coord
                        )
                    }

                    // Outer dot border
                    drawCircle(
                        color = if (isSelected) SageDark else SagePrimary,
                        radius = if (isSelected) 6.5f else 4.5f,
                        center = coord
                    )
                    // Inner white dot
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 3.5f else 2.5f,
                        center = coord
                    )
                }
            }
        }

        // X-Axis Date Labels Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 12.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { index, pt ->
                Text(
                    text = pt.dateLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (index == selectedIndex) SageDark else Color(0xFF7A8680),
                        fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Recharts Interactive Floating Tooltip Display Card
        selectedIndex?.let { idx ->
            if (idx in points.indices) {
                val point = points[idx]
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SageLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SagePrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(SagePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = "Quiz trend",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = point.quizTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageDark,
                                        fontSize = 13.5.sp
                                    )
                                )
                                Text(
                                    text = "${point.subject} • ${point.fullDate}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF55605A),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${point.percentage}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (point.percentage >= 80) SageDark else PeachAccent,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Score: ${point.score}/${point.totalQuestions}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF6A7770),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recharts Rounded Bar Chart
 * Visualizes study time and completed sessions across subjects and days
 */
@Composable
fun RechartsStudyTimeBarChart(
    barDataList: List<DayStudyBarData>,
    modifier: Modifier = Modifier
) {
    if (barDataList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No study sessions logged yet", color = Color.Gray, fontSize = 13.sp)
        }
        return
    }

    var selectedBarIndex by remember { mutableStateOf<Int?>(barDataList.lastIndex) }
    val maxMinutes = (barDataList.maxOfOrNull { it.totalMinutes } ?: 120).coerceAtLeast(60).toFloat()

    Column(modifier = modifier.fillMaxWidth()) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RechartsLegendItem(label = "DPA", color = LavenderPrimary)
                RechartsLegendItem(label = "OS", color = PeachAccent)
                RechartsLegendItem(label = "Math", color = SagePrimary)
                RechartsLegendItem(label = "Code", color = ChartTeal)
            }
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF7B8881),
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bar Chart Canvas
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag("recharts_study_bar_chart")
        ) {
            val chartWidth = constraints.maxWidth.toFloat()
            val chartHeight = constraints.maxHeight.toFloat()
            val paddingLeft = 32.dp.value * 2.5f
            val paddingRight = 12.dp.value * 2.5f
            val paddingTop = 10.dp.value * 2.5f
            val paddingBottom = 22.dp.value * 2.5f

            val plotWidth = (chartWidth - paddingLeft - paddingRight).coerceAtLeast(10f)
            val plotHeight = (chartHeight - paddingTop - paddingBottom).coerceAtLeast(10f)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(barDataList) {
                        detectTapGestures { offset ->
                            val slotWidth = plotWidth / barDataList.size
                            val relativeX = offset.x - paddingLeft
                            val tappedIndex = (relativeX / slotWidth).toInt().coerceIn(0, barDataList.lastIndex)
                            selectedBarIndex = tappedIndex
                        }
                    }
            ) {
                // Background grid lines
                val gridYLevels = listOf(0f, 0.5f, 1f)
                gridYLevels.forEach { ratio ->
                    val y = paddingTop + plotHeight - (ratio * plotHeight)
                    drawLine(
                        color = GridLineColor,
                        start = Offset(paddingLeft, y),
                        end = Offset(paddingLeft + plotWidth, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                val slotWidth = plotWidth / barDataList.size
                val barWidth = (slotWidth * 0.58f).coerceAtMost(32f)

                barDataList.forEachIndexed { index, item ->
                    val isSelected = (index == selectedBarIndex)
                    val centerX = paddingLeft + (index * slotWidth) + (slotWidth / 2f)
                    val left = centerX - (barWidth / 2f)

                    val barHeight = ((item.totalMinutes / maxMinutes) * plotHeight).coerceAtLeast(6f)
                    val top = paddingTop + plotHeight - barHeight

                    // Subtle background slot highlight for selected
                    if (isSelected) {
                        drawRoundRect(
                            color = SageLight.copy(alpha = 0.6f),
                            topLeft = Offset(centerX - slotWidth / 2f + 2f, paddingTop),
                            size = Size(slotWidth - 4f, plotHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }

                    // Multi-segment stacked or single rounded bar with primary color
                    val barColor = when {
                        item.dpaMinutes > 0 && item.dpaMinutes >= item.osMinutes && item.dpaMinutes >= item.mathMinutes -> LavenderPrimary
                        item.osMinutes > 0 && item.osMinutes >= item.mathMinutes -> PeachAccent
                        item.mathMinutes > 0 -> SagePrimary
                        item.programmingMinutes > 0 -> ChartTeal
                        else -> Color(0xFFB0BDB6)
                    }

                    drawRoundRect(
                        color = if (isSelected) barColor else barColor.copy(alpha = 0.85f),
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Top accent cap if selected
                    if (isSelected) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(left + 2f, top + 2f),
                            size = Size(barWidth - 4f, 4f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }

        // X-Axis Day Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 10.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            barDataList.forEachIndexed { index, item ->
                Text(
                    text = item.dayLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (index == selectedBarIndex) SageDark else Color(0xFF7A8680),
                        fontWeight = if (index == selectedBarIndex) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tooltip for Selected Bar
        selectedBarIndex?.let { idx ->
            if (idx in barDataList.indices) {
                val item = barDataList[idx]
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7F2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2DDD2), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                tint = SagePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${item.dayLabel} (${item.dateLabel})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageDark,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Text(
                            text = if (item.totalMinutes > 0) {
                                String.format(java.util.Locale.US, "%.1f hrs (%d mins)", item.totalMinutes / 60.0, item.totalMinutes)
                            } else {
                                "Rest day (0 mins)"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.totalMinutes > 0) SageDark else Color.Gray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Recharts Radial Progress & Topic Mastery Gauge
 */
@Composable
fun RechartsTopicRadialGauge(
    completedCount: Int,
    totalCount: Int,
    subjectStats: List<SubjectTopicProgress>,
    modifier: Modifier = Modifier
) {
    val overallPercentage = if (totalCount > 0) ((completedCount.toDouble() / totalCount) * 100).toInt() else 0
    val animatedProgress by animateFloatAsState(
        targetValue = overallPercentage / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "radial_gauge_anim"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radial Gauge Arc
        Box(
            modifier = Modifier
                .size(110.dp)
                .testTag("recharts_radial_gauge"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 11.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background track
                drawCircle(
                    color = Color(0xFFE9E5DC),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Animated active arc
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(SagePrimary, ChartTeal, LavenderPrimary, SagePrimary)
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$overallPercentage%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = SageDark,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Syllabus",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF6F7B74),
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Subject Breakdown Bars
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subjectStats.forEach { stat ->
                val barColor = when {
                    stat.subject.contains("DPA", ignoreCase = true) -> LavenderPrimary
                    stat.subject.contains("Operating", ignoreCase = true) -> PeachAccent
                    stat.subject.contains("Math", ignoreCase = true) -> SagePrimary
                    else -> ChartTeal
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stat.subject,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "${stat.completedCount}/${stat.totalCount} (${stat.percentage}%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF6B7570),
                                fontSize = 10.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE8E4DA))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (stat.percentage / 100f).coerceIn(0f, 1f))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Completed Topics Checklist with Subject Filtering
 */
@Composable
fun RechartsTopicTrackerSection(
    topics: List<TopicProgressEntity>,
    onToggleTopic: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects = listOf("All", "DPA", "Operating Systems", "Discrete Mathematics", "Programming")
    var selectedSubjectTab by remember { mutableIntStateOf(0) }

    val filteredTopics = remember(topics, selectedSubjectTab) {
        if (selectedSubjectTab == 0) {
            topics
        } else {
            val selectedName = subjects[selectedSubjectTab]
            topics.filter { it.subject.contains(selectedName, ignoreCase = true) || selectedName.contains(it.subject, ignoreCase = true) }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Tab selector
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subjects.forEachIndexed { index, subj ->
                val isSelected = (index == selectedSubjectTab)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SagePrimary else Color(0xFFECE7DC))
                        .clickable { selectedSubjectTab = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = subj,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF4C5852),
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Topic items list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredTopics.forEach { topic ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (topic.isCompleted) Color(0xFFF7FAF8) else Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (topic.isCompleted) SagePrimary.copy(alpha = 0.3f) else Color(0xFFE8E3D8),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onToggleTopic(topic.id, topic.isCompleted) }
                        .testTag("topic_item_${topic.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox icon
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (topic.isCompleted) SagePrimary else Color(0xFFE8E5DD)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (topic.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = topic.topicName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (topic.isCompleted) SageDark else Color(0xFF26322C),
                                    fontSize = 13.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = topic.subject,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = LavenderPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                                Text(
                                    text = " • ${if (topic.isCompleted) "Completed ✓" else "In Progress"}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (topic.isCompleted) SagePrimary else Color.Gray,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (topic.isCompleted) SageLight else Color(0xFFF3EFE7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${topic.masteryPercentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (topic.isCompleted) SageDark else Color.Gray,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
