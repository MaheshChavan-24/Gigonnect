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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.SahaayLogo
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaSecondary

@Composable
fun LandingScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: (UserRole) -> Unit,
    isHindi: Boolean,
    onToggleLanguage: () -> Unit
) {
    val scrollState = rememberScrollState()

    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar Language Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.clickable(onClick = onToggleLanguage)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = SahayaSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "English" else "हिन्दी (HI)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SahayaSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center App Icon
        SahaayLogo(size = 72.dp, shapeRadius = 18.dp)

        Spacer(modifier = Modifier.height(20.dp))

        // Heading
        Text(
            text = if (isHindi) "सहाय में आपका स्वागत है" else "Welcome to Sahaay",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = SahayaSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle
        Text(
            text = if (isHindi) "विश्वसनीय सेवाएँ खोजें या अपनी सेवाएँ प्रदान करें" else "Find trusted help or offer your services",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email / Username Input
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isHindi) "उपयोगकर्ता नाम / ईमेल पता" else "Email Address / Username",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SahayaSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = { usernameOrEmail = it },
                placeholder = { Text(if (isHindi) "you@email.com या उपयोगकर्ता नाम" else "you@email.com or username", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = SahayaPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("landing_username_input")
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Password Input
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isHindi) "पासवर्ड" else "Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SahayaSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(if (isHindi) "अपना पासवर्ड दर्ज करें" else "Enter your password", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = SahayaPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (isHindi) "पासवर्ड भूल गए?" else "Forgot password?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SahayaPrimary,
                    modifier = Modifier.clickable { /* future forgot pwd workflow */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Get Started / Log In Button
        Button(
            onClick = { onLoginClick(usernameOrEmail.trim(), password.trim()) },
            enabled = usernameOrEmail.isNotBlank() && password.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SahayaPrimary,
                disabledContainerColor = Color(0xFFFED7AA)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("landing_get_started_button")
        ) {
            Text(
                text = if (isHindi) "शुरू करें (लॉग इन)" else "Get Started",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Divider: or register as
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
            Text(
                text = if (isHindi) "  या पंजीकरण करें  " else "  or register as  ",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dual Register Buttons: Client | Service Provider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Client Pill
            OutlinedButton(
                onClick = { onRegisterClick(UserRole.CLIENT) },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SahayaSecondary),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("register_as_client_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = SahayaSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "ग्राहक" else "Client",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Service Provider Pill
            OutlinedButton(
                onClick = { onRegisterClick(UserRole.WORKER) },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SahayaSecondary),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("register_as_worker_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = null,
                        tint = SahayaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "सहायक" else "Service Provider",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer: Don't have an account? Sign up
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "खाता नहीं है? " else "Don't have an account? ",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = if (isHindi) "साइन अप करें" else "Sign up",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SahayaPrimary,
                modifier = Modifier
                    .clickable { onRegisterClick(UserRole.CLIENT) }
                    .testTag("landing_signup_link")
            )
        }
    }
}

