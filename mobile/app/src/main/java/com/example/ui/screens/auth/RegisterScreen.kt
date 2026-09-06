package com.example.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.SahayaPrimary

@Composable
fun RegisterScreen(
    onRegisterSuccess: (username: String, email: String, password: String, firstName: String, phone: String, isClient: Boolean, isWorker: Boolean) -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    isHindi: Boolean = false
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("register_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isHindi) "खाता बनाएं" else "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = if (isHindi) "सहाय पर सेवा लेने या देने के लिए जुड़ें" else "Join Sahaya to hire or offer professional services",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Role Segmented Toggle
        Text(
            text = if (isHindi) "आपकी प्राथमिक भूमिका:" else "Primary Role:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedRole == UserRole.CLIENT) MaterialTheme.colorScheme.surface else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { selectedRole = UserRole.CLIENT }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHindi) "ग्राहक (Client)" else "Client (Hire)",
                    fontSize = 13.sp,
                    fontWeight = if (selectedRole == UserRole.CLIENT) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedRole == UserRole.CLIENT) SahayaPrimary else Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selectedRole == UserRole.WORKER) MaterialTheme.colorScheme.surface else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { selectedRole = UserRole.WORKER }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHindi) "कारीगर (Worker)" else "Worker (Earn)",
                    fontSize = 13.sp,
                    fontWeight = if (selectedRole == UserRole.WORKER) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedRole == UserRole.WORKER) SahayaPrimary else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(if (isHindi) "उपयोगकर्ता नाम" else "Username") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_username_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(if (isHindi) "ईमेल पता" else "Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_email_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(if (isHindi) "मोबाइल नंबर" else "Phone (+91)") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_phone_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (isHindi) "सुरक्षित पासवर्ड" else "Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_password_input")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val isClient = selectedRole == UserRole.CLIENT
                val isWorker = selectedRole == UserRole.WORKER
                onRegisterSuccess(
                    username.trim(),
                    email.trim(),
                    password.trim(),
                    username.trim(), // firstName
                    phone.trim(),
                    isClient,
                    isWorker
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("register_submit_button")
        ) {
            Text(
                text = if (isHindi) "खाता बनाएं एवं आगे बढ़ें" else "Register & Continue",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "पहले से खाता है?" else "Already have an account?",
                fontSize = 13.sp,
                color = Color.Gray
            )
            TextButton(onClick = onLoginClick) {
                Text(
                    text = if (isHindi) "लॉग इन करें" else "Log In",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
