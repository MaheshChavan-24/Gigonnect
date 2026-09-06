package com.example.ui.screens.shared

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Constants
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KYCUploadScreen(
    onSubmit: (String) -> Unit,
    onBackClick: () -> Unit,
    isHindi: Boolean = false
) {
    var selectedIdType by remember { mutableStateOf("Aadhaar") }
    var isExpanded by remember { mutableStateOf(false) }

    var frontUploaded by remember { mutableStateOf(true) }
    var backUploaded by remember { mutableStateOf(true) }
    var selfieUploaded by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("kyc_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "सरकारी पहचान सत्यापन (KYC)" else "KYC Identity Verification",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SahayaPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isHindi) {
                        "काम पोस्ट करने या स्वीकारने से पहले पहचान सत्यापन अनिवार्य है। सुरक्षित एस्क्रो और ग्राहकों के विश्वास हेतु।"
                    } else {
                        "Identity verification is required before accepting jobs. Admin reviews your ID to maintain platform trust & safety."
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF1E3A8A),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ID Type Selector
        Text(
            text = if (isHindi) "पहचान पत्र का प्रकार चुनें:" else "Select Government ID Type:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                value = selectedIdType,
                onValueChange = {},
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.CreditCard, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .testTag("kyc_id_type_selector")
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                Constants.ID_TYPES.forEach { idType ->
                    DropdownMenuItem(
                        text = { Text(idType) },
                        onClick = {
                            selectedIdType = idType
                            isExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Document Upload Slots
        Text(
            text = if (isHindi) "दस्तावेज़ अपलोड करें:" else "Upload Document Photos:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 1. ID Front Image
        UploadCard(
            title = "$selectedIdType (Front Side)",
            isUploaded = frontUploaded,
            onToggle = { frontUploaded = !frontUploaded },
            icon = Icons.Default.CloudUpload,
            tag = "kyc_upload_front"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. ID Back Image
        UploadCard(
            title = "$selectedIdType (Back Side)",
            isUploaded = backUploaded,
            onToggle = { backUploaded = !backUploaded },
            icon = Icons.Default.CloudUpload,
            tag = "kyc_upload_back"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Selfie Photo
        UploadCard(
            title = if (isHindi) "चेहरे की सेल्फी (Selfie)" else "Live Face Selfie with ID",
            isUploaded = selfieUploaded,
            onToggle = { selfieUploaded = !selfieUploaded },
            icon = Icons.Default.CameraAlt,
            tag = "kyc_upload_selfie"
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { onSubmit(selectedIdType) },
            enabled = frontUploaded && backUploaded && selfieUploaded,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("kyc_submit_button")
        ) {
            Text(
                text = if (isHindi) "सत्यापन हेतु भेजें" else "Submit for Verification",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun UploadCard(
    title: String,
    isUploaded: Boolean,
    onToggle: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isUploaded) SahayaSuccess else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isUploaded) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isUploaded) Color(0xFFDCFCE7) else Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUploaded) Icons.Default.Check else icon,
                    contentDescription = null,
                    tint = if (isUploaded) SahayaSuccess else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isUploaded) "Document attached (Tap to change)" else "Tap to upload file / photo",
                    fontSize = 11.sp,
                    color = if (isUploaded) SahayaSuccess else Color.Gray
                )
            }
        }
    }
}
