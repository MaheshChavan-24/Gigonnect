package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Job
import com.example.data.model.UrgencyLevel
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaPrimary

@Composable
fun InteractiveJobMap(
    jobs: List<Job>,
    selectedJob: Job?,
    onJobSelected: (Job) -> Unit,
    isHindi: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 48f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Pre-calculated relative canvas offsets for seed jobs around center
    val jobOffsets = remember(jobs) {
        jobs.mapIndexed { index, job ->
            val angle = (index * 68.0) * Math.PI / 180.0
            val normalizedDist = (job.distanceKm / 10.0).coerceIn(0.25, 0.95)
            Pair(job, Pair(Math.cos(angle) * normalizedDist, Math.sin(angle) * normalizedDist))
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .testTag("interactive_map_canvas")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(jobs) {
                            detectTapGestures { tapOffset ->
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val maxRadius = Math.min(centerX, centerY) * 0.88f

                                for ((job, offsetNorm) in jobOffsets) {
                                    val pinX = centerX + (offsetNorm.first.toFloat() * maxRadius)
                                    val pinY = centerY + (offsetNorm.second.toFloat() * maxRadius)
                                    val distance = Math.hypot(
                                        (tapOffset.x - pinX).toDouble(),
                                        (tapOffset.y - pinY).toDouble()
                                    )
                                    if (distance <= 36.0) {
                                        onJobSelected(job)
                                        break
                                    }
                                }
                            }
                        }
                ) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val maxRadius = Math.min(centerX, centerY) * 0.88f

                    // 1. Grid lines representing city streets
                    val gridColor = Color(0xFFE2E8F0)
                    for (x in 0 until size.width.toInt() step 50) {
                        drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1f)
                    }
                    for (y in 0 until size.height.toInt() step 50) {
                        drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1f)
                    }

                    // 2. Concentric radius zones
                    // 5km Emergency Zone (Red boundary)
                    val r5km = maxRadius * 0.45f
                    drawCircle(
                        color = SahayaEmergency.copy(alpha = 0.05f),
                        radius = r5km,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = SahayaEmergency.copy(alpha = 0.35f),
                        radius = r5km,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1.5f)
                    )

                    // 30km Standard Zone (Amber boundary)
                    drawCircle(
                        color = SahayaAmber.copy(alpha = 0.03f),
                        radius = maxRadius,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = SahayaAmber.copy(alpha = 0.25f),
                        radius = maxRadius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1.5f)
                    )

                    // 3. Current Worker GPS Location (Pulse + Solid center)
                    drawCircle(
                        color = SahayaPrimary.copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 9f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = SahayaPrimary,
                        radius = 6f,
                        center = Offset(centerX, centerY)
                    )

                    // 4. Job Pins
                    for ((job, offsetNorm) in jobOffsets) {
                        val pinX = centerX + (offsetNorm.first.toFloat() * maxRadius)
                        val pinY = centerY + (offsetNorm.second.toFloat() * maxRadius)
                        val isEmergency = job.urgencyLevel == UrgencyLevel.EMERGENCY
                        val isSelected = selectedJob?.id == job.id

                        val pinColor = if (isEmergency) SahayaEmergency else SahayaAmber

                        // Pulsing outer halo for emergency or selected jobs
                        if (isEmergency || isSelected) {
                            drawCircle(
                                color = pinColor.copy(alpha = 0.25f),
                                radius = if (isSelected) 18f else 14f,
                                center = Offset(pinX, pinY)
                            )
                        }

                        // Pin shadow
                        drawCircle(
                            color = Color(0x33000000),
                            radius = 9f,
                            center = Offset(pinX, pinY + 2f)
                        )

                        // Outer pin body
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 10f else 8f,
                            center = Offset(pinX, pinY)
                        )
                        // Inner dot
                        drawCircle(
                            color = pinColor,
                            radius = if (isSelected) 7f else 5.5f,
                            center = Offset(pinX, pinY)
                        )
                    }
                }

                // Map Legend Overlay at Top Left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(SahayaEmergency, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "तत्काल (≤ 5km)" else "Emergency (≤ 5km)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(SahayaAmber, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "मानक (≤ 30km)" else "Standard (≤ 30km)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                // GPS Center Indicator badge at Top Right
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(SahayaPrimary.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "GPS Active",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pune GPS (Live)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Bottom Hint
                Text(
                    text = if (isHindi) "मार्कर पर टैप करें विवरण देखने हेतु" else "Tap on any marker to inspect & claim",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
    }
}
