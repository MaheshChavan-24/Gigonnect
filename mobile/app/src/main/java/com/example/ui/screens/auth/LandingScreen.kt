package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.SahaayLogo
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaAmberContainer
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaPrimaryContainer
import com.example.ui.theme.SahayaSuccess

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    isHindi: Boolean,
    onToggleLanguage: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SahaayLogo(size = 40.dp, shapeRadius = 10.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SAHAAY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SahayaPrimary,
                    letterSpacing = 1.2.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable(onClick = onToggleLanguage)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "English" else "हिन्दी (HI)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Tagline
        Text(
            text = if (isHindi) "कुशल कारीगर एवं घरेलू सेवाएं, सुरक्षित एस्क्रो के साथ" else "Skilled Trades & Home Repairs with Secure Escrow",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isHindi) "नलसाजी, बढ़ईगीरी, बिजली का काम और 15+ श्रेणियां।" else "Plumbing, electrical, carpentry & 15+ verified trades.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Segmented Sub-Tabs: Client / Worker
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedRole == UserRole.CLIENT) MaterialTheme.colorScheme.surface else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedRole = UserRole.CLIENT }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = if (selectedRole == UserRole.CLIENT) SahayaPrimary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "ग्राहक (Customer)" else "Customer",
                                fontSize = 13.sp,
                                fontWeight = if (selectedRole == UserRole.CLIENT) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedRole == UserRole.CLIENT) SahayaPrimary else Color.Gray
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedRole == UserRole.WORKER) MaterialTheme.colorScheme.surface else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedRole = UserRole.WORKER }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = null,
                                tint = if (selectedRole == UserRole.WORKER) SahayaAmber else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "कारीगर (Worker)" else "Service Worker",
                                fontSize = 13.sp,
                                fontWeight = if (selectedRole == UserRole.WORKER) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedRole == UserRole.WORKER) SahayaAmber else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tab Content Preview
                if (selectedRole == UserRole.CLIENT) {
                    FeatureRow(
                        title = if (isHindi) "तत्काल मरम्मत कार्य पोस्ट करें" else "Post Instant & Scheduled Jobs",
                        desc = if (isHindi) "आपातकालीन या सामान्य काम दर्ज करें, नजदीकी कारीगर तुरंत मिलेंगे।" else "Post home repairs and alert verified nearby workers instantly."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureRow(
                        title = if (isHindi) "100% सुरक्षित एस्क्रो भुगतान" else "100% Escrow Protection",
                        desc = if (isHindi) "काम पूरा होने और आपकी संतुष्टि के बाद ही भुगतान जारी होता है।" else "Funds held in safe escrow and released only after your approval."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureRow(
                        title = if (isHindi) "सत्यापित कुशल कारीगर" else "Verified Trade Professionals",
                        desc = if (isHindi) "सरकारी पहचान पत्र सत्यापित और रेटिंग वाले कारीगर खोजें।" else "Browse KYC verified plumbers, electricians, and carpenters."
                    )
                } else {
                    FeatureRow(
                        title = if (isHindi) "नजदीकी काम स्वीकारें" else "Accept Nearby Trade Jobs",
                        desc = if (isHindi) "अपने क्षेत्र में लाइव काम देखें और तुरंत स्वीकार करें।" else "View real-time job requests and accept local repair orders."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureRow(
                        title = if (isHindi) "सीधे बैंक खाते में सुरक्षित भुगतान" else "Guaranteed Escrow Payouts",
                        desc = if (isHindi) "काम समाप्त होते ही सीधे वॉलेट व बैंक में राशि प्राप्त करें।" else "Get paid on time with direct transfers to your bank account."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureRow(
                        title = if (isHindi) "व्यापार प्रोफ़ाइल बनाएं" else "Build Your Trade Reputation",
                        desc = if (isHindi) "अपने कौशल, अनुभव और ग्राहक रेटिंग का प्रदर्शन करें।" else "Showcase skills, experience, and collect 5-star client reviews."
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Escrow guarantee pill
        Row(
            modifier = Modifier
                .background(Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Escrow Guarantee",
                tint = SahayaSuccess,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isHindi) "100% सुरक्षित एस्क्रो एवं सरकारी पहचान सत्यापन" else "100% Escrow Protected & Govt ID Verified",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Login & Register CTA Buttons
        Button(
            onClick = onLoginClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("landing_login_button")
        ) {
            Text(
                text = if (isHindi) "लॉग इन करें" else "Log In",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRegisterClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("landing_register_button")
        ) {
            Text(
                text = if (isHindi) "नया खाता बनाएं (पंजीकरण)" else "Create an Account / Register",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun FeatureRow(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SahayaSuccess,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp
            )
        }
    }
}
