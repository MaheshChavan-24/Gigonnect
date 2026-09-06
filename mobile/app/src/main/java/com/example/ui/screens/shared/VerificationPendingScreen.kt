package com.example.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VerificationStatus
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaAmberContainer
import com.example.ui.theme.SahayaSuccess

@Composable
fun VerificationPendingScreen(
    status: VerificationStatus,
    rejectionReason: String?,
    onReUploadClick: () -> Unit,
    onContinueClick: () -> Unit,
    isHindi: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    if (status == VerificationStatus.VERIFIED) Color(0xFFDCFCE7) else SahayaAmberContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (status == VerificationStatus.VERIFIED) Icons.Rounded.CheckCircle else Icons.Default.HourglassTop,
                contentDescription = null,
                tint = if (status == VerificationStatus.VERIFIED) SahayaSuccess else SahayaAmber,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        VerificationBadge(status = status, isHindi = isHindi)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (status == VerificationStatus.VERIFIED) {
                if (isHindi) "पहचान सफलतापूर्वक सत्यापित!" else "Identity Verified Successfully!"
            } else {
                if (isHindi) "सत्यापन प्रक्रियाधीन है" else "Verification Under Review"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (status == VerificationStatus.VERIFIED) {
                if (isHindi) "आपके सरकारी पहचान पत्र की पुष्टि हो चुकी है। आप सभी कार्यों को पोस्ट एवं स्वीकार कर सकते हैं।" else "Your government documents have been verified by the administrator. You have full access to marketplace jobs."
            } else {
                if (isHindi) {
                    "आपके दस्तावेज़ एडमिन समीक्षा के लिए भेज दिए गए हैं। सत्यापन प्रक्रिया आमतौर पर 1-2 घंटों में पूरी होती है।"
                } else {
                    "Your documents have been submitted to the administrator for review via Django Admin portal. You will receive an in-app notification once verified."
                }
            },
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (status == VerificationStatus.REJECTED && rejectionReason != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
            ) {
                Text(
                    text = "Reason: $rejectionReason",
                    fontSize = 12.sp,
                    color = Color(0xFF991B1B),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        if (status == VerificationStatus.REJECTED || status == VerificationStatus.UNSUBMITTED) {
            Button(
                onClick = onReUploadClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("verification_reupload_button")
            ) {
                Text(
                    text = if (isHindi) "दस्तावेज़ पुनः अपलोड करें" else "Re-upload Documents",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onContinueClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("verification_continue_button")
        ) {
            Text(
                text = if (isHindi) "डैशबोर्ड पर लौटें" else "Return to Dashboard",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
