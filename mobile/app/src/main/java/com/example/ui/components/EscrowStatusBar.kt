package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaSuccess

@Composable
fun EscrowStatusBar(
    jobStatus: JobStatus,
    escrowStatus: EscrowStatus,
    isHindi: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "सुरक्षित एस्क्रो प्रगति" else "Escrow Protection Status",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                EscrowBadge(escrowStatus = escrowStatus, isHindi = isHindi)
            }

            Spacer(modifier = Modifier.height(14.dp))

            val currentStep = when {
                jobStatus == JobStatus.COMPLETED && escrowStatus == EscrowStatus.RELEASED -> 4
                jobStatus == JobStatus.WORKER_COMPLETED -> 3
                escrowStatus == EscrowStatus.HELD -> 2
                jobStatus == JobStatus.ACCEPTED -> 1
                else -> 0
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StepNode(
                    icon = Icons.Default.Person,
                    label = if (isHindi) "स्वीकृत" else "Claimed",
                    isCompleted = currentStep >= 1,
                    isCurrent = currentStep == 0 || currentStep == 1
                )
                StepConnector(isCompleted = currentStep >= 2)
                StepNode(
                    icon = Icons.Default.Lock,
                    label = if (isHindi) "एस्क्रो जमा" else "Funded",
                    isCompleted = currentStep >= 2,
                    isCurrent = currentStep == 2
                )
                StepConnector(isCompleted = currentStep >= 3)
                StepNode(
                    icon = Icons.Default.Check,
                    label = if (isHindi) "कार्य पूर्ण" else "Done",
                    isCompleted = currentStep >= 3,
                    isCurrent = currentStep == 3
                )
                StepConnector(isCompleted = currentStep >= 4)
                StepNode(
                    icon = Icons.Default.Star,
                    label = if (isHindi) "जारी" else "Released",
                    isCompleted = currentStep >= 4,
                    isCurrent = currentStep == 4
                )
            }
        }
    }
}

@Composable
private fun StepNode(
    icon: ImageVector,
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isCompleted -> SahayaSuccess
            isCurrent -> SahayaPrimary
            else -> Color(0xFFCBD5E1)
        },
        label = "stepBg"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.size(width = 54.dp, height = 54.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
            color = if (isCompleted || isCurrent) MaterialTheme.colorScheme.onSurface else Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun StepConnector(isCompleted: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isCompleted) SahayaSuccess else Color(0xFFE2E8F0),
        label = "connColor"
    )
    Box(
        modifier = Modifier
            .height(3.dp)
            .width(22.dp)
            .background(color, RoundedCornerShape(2.dp))
    )
}
