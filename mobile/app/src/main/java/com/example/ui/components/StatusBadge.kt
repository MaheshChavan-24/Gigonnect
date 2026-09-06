package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.VerificationStatus
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaAmberContainer
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaEmergencyContainer
import com.example.ui.theme.SahayaOnAmberContainer
import com.example.ui.theme.SahayaOnEmergencyContainer
import com.example.ui.theme.SahayaOnSuccessContainer
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaPrimaryContainer
import com.example.ui.theme.SahayaSuccess
import com.example.ui.theme.SahayaSuccessContainer

@Composable
fun UrgencyBadge(urgency: UrgencyLevel, isHindi: Boolean = false) {
    val isEmergency = urgency == UrgencyLevel.EMERGENCY
    val bgColor = if (isEmergency) SahayaEmergencyContainer else SahayaAmberContainer
    val textColor = if (isEmergency) SahayaOnEmergencyContainer else SahayaOnAmberContainer
    val label = if (isEmergency) {
        if (isHindi) "तत्काल (≤5km)" else "Emergency (≤5km)"
    } else {
        if (isHindi) "सामान्य (≤30km)" else "Standard (≤30km)"
    }

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(if (isEmergency) SahayaEmergency else SahayaAmber, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun JobStatusBadge(status: JobStatus, isHindi: Boolean = false) {
    val (bgColor, textColor, label) = when (status) {
        JobStatus.PENDING -> Triple(
            SahayaAmberContainer,
            SahayaOnAmberContainer,
            if (isHindi) "प्रतीक्षारत" else "Pending"
        )
        JobStatus.ACCEPTED -> Triple(
            SahayaPrimaryContainer,
            SahayaPrimary,
            if (isHindi) "स्वीकृत" else "Accepted"
        )
        JobStatus.WORKER_COMPLETED -> Triple(
            Color(0xFFE0E7FF),
            Color(0xFF3730A3),
            if (isHindi) "कार्य पूर्ण (सत्यापन बाकी)" else "Awaiting Approval"
        )
        JobStatus.COMPLETED -> Triple(
            SahayaSuccessContainer,
            SahayaOnSuccessContainer,
            if (isHindi) "पूर्ण" else "Completed"
        )
        JobStatus.DISPUTED -> Triple(
            SahayaEmergencyContainer,
            SahayaOnEmergencyContainer,
            if (isHindi) "विवादित" else "Disputed"
        )
    }

    Text(
        text = label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun EscrowBadge(escrowStatus: EscrowStatus, isHindi: Boolean = false) {
    val (bgColor, textColor, text, icon) = when (escrowStatus) {
        EscrowStatus.NONE -> Quad(
            Color(0xFFF1F5F9),
            Color(0xFF64748B),
            if (isHindi) "एस्क्रो नहीं" else "Escrow: None",
            false
        )
        EscrowStatus.PENDING -> Quad(
            SahayaAmberContainer,
            SahayaOnAmberContainer,
            if (isHindi) "एस्क्रो भुगतान बाकी" else "Escrow Pending",
            true
        )
        EscrowStatus.HELD -> Quad(
            SahayaSuccessContainer,
            SahayaOnSuccessContainer,
            if (isHindi) "एस्क्रो सुरक्षित (Held)" else "Escrow Held",
            true
        )
        EscrowStatus.RELEASED -> Quad(
            Color(0xFFDCFCE7),
            Color(0xFF15803D),
            if (isHindi) "एस्क्रो जारी (Released)" else "Escrow Released",
            true
        )
        EscrowStatus.REFUNDED -> Quad(
            SahayaEmergencyContainer,
            SahayaEmergency,
            if (isHindi) "वापस किया गया" else "Escrow Refunded",
            false
        )
    }

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Escrow Secured",
                tint = textColor,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun VerificationBadge(status: VerificationStatus, isHindi: Boolean = false) {
    val (bgColor, textColor, label) = when (status) {
        VerificationStatus.UNSUBMITTED -> Triple(
            Color(0xFFF1F5F9),
            Color(0xFF475569),
            if (isHindi) "असत्यापित" else "Unverified"
        )
        VerificationStatus.PENDING -> Triple(
            SahayaAmberContainer,
            SahayaOnAmberContainer,
            if (isHindi) "जांच जारी (Pending)" else "Verification Pending"
        )
        VerificationStatus.VERIFIED -> Triple(
            SahayaSuccessContainer,
            SahayaOnSuccessContainer,
            if (isHindi) "सत्यापित पहचान" else "Govt ID Verified"
        )
        VerificationStatus.REJECTED -> Triple(
            SahayaEmergencyContainer,
            SahayaEmergency,
            if (isHindi) "अस्वीकृत" else "Rejected"
        )
    }

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (status == VerificationStatus.VERIFIED) Icons.Rounded.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
