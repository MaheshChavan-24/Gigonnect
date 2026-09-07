package com.example.ui.screens.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaAmberContainer
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaPrimaryContainer
import com.example.ui.theme.SahayaSuccess

@Composable
fun ProfileScreen(
    user: User?,
    onSwitchRole: (UserRole) -> Unit,
    onKycClick: () -> Unit,
    onWalletClick: () -> Unit,
    isHindi: Boolean,
    onToggleLanguage: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val kycSubtitle = when (user?.verificationStatus) {
        com.example.data.model.VerificationStatus.VERIFIED -> if (isHindi) "सत्यापित (Verified)" else "Verified Badge Active"
        com.example.data.model.VerificationStatus.PENDING -> if (isHindi) "समीक्षाधीन (Under Review)" else "Under Review (Tap to View)"
        com.example.data.model.VerificationStatus.REJECTED -> if (isHindi) "अस्वीकृत (पुनः अपलोड करें)" else "Rejected - Action Required"
        else -> if (isHindi) "जमा नहीं किया (सत्यापन करें)" else "Not Submitted (Tap to Verify)"
    }

    val bankSubtitle = if (!user?.bankName.isNullOrBlank()) {
        "${user?.bankName} •••• ${user?.accountNumber?.takeLast(4) ?: "••••"}"
    } else {
        if (isHindi) "बैंक खाता जोड़ें (एस्क्रो हेतु)" else "Add Bank Details for Escrow Payouts"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = if (isHindi) "मेरी प्रोफ़ाइल" else "My Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User Overview Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(SahayaPrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SahayaPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.username ?: "User",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user?.phoneNumber?.ifBlank { "+91 98765 43210" } ?: "+91 98765 43210",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    VerificationBadge(status = user?.verificationStatus ?: com.example.data.model.VerificationStatus.UNSUBMITTED, isHindi = isHindi)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Indicator Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (user?.activeRole == UserRole.CLIENT) SahayaPrimaryContainer else SahayaAmberContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (user?.activeRole == UserRole.CLIENT) Icons.Default.Person else Icons.Default.Handyman,
                            contentDescription = null,
                            tint = if (user?.activeRole == UserRole.CLIENT) SahayaPrimary else SahayaAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (user?.activeRole == UserRole.CLIENT) {
                                if (isHindi) "सक्रिय मोड: ग्राहक" else "Active Mode: Client"
                            } else {
                                if (isHindi) "सक्रिय मोड: सहायक" else "Active Mode: Service Provider"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (user?.activeRole == UserRole.CLIENT) SahayaPrimary else SahayaAmber
                        )
                    }

                    Text(
                        text = if (isHindi) "सत्यापित" else "Verified",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Role Switching Card (Only shown if user is a registered Worker/Service Provider)
        if (user != null && user.isWorker) {
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = SahayaPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (user.activeRole == UserRole.WORKER) {
                                    if (isHindi) "ग्राहक मोड पर स्विच करें" else "Switch to Client Mode"
                                } else {
                                    if (isHindi) "सहायक मोड पर स्विच करें" else "Switch to Service Provider Mode"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isHindi) "अन्य सेवाओं हेतु ग्राहक मोड या काम हेतु सहायक मोड" else "Switch modes to hire other services or provide services",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = user.activeRole == UserRole.CLIENT,
                        onCheckedChange = { isClient ->
                            onSwitchRole(if (isClient) UserRole.CLIENT else UserRole.WORKER)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Menu Items
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Shield,
                    title = if (isHindi) "सरकारी पहचान सत्यापन (KYC)" else "KYC Identity Documents",
                    subtitle = kycSubtitle,
                    onClick = onKycClick
                )

                ProfileMenuItem(
                    icon = Icons.Default.AccountBalance,
                    title = if (isHindi) "बैंक विवरण (पayout हेतु)" else "Bank Account & Payouts",
                    subtitle = bankSubtitle,
                    onClick = onWalletClick
                )

                ProfileMenuItem(
                    icon = Icons.Default.Language,
                    title = if (isHindi) "भाषा (Language)" else "App Language",
                    subtitle = if (isHindi) "हिन्दी (Hindi)" else "English",
                    onClick = onToggleLanguage
                )

                ProfileMenuItem(
                    icon = Icons.Default.VerifiedUser,
                    title = if (isHindi) "सुरक्षा एवं एस्क्रो गारंटी" else "Escrow Safety & Trust",
                    subtitle = if (isHindi) "100% सुरक्षित भुगतान प्रणाली" else "100% Secure Payment Guarantee",
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        OutlinedButton(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SahayaEmergency),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("profile_logout_button")
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "लॉग आउट करें" else "Log Out",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(12.dp)
        )
    }
}
