package com.example.ui.screens.client

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
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Switch
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
import com.example.data.model.UrgencyLevel
import com.example.ui.components.TradeIcons
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaEmergencyContainer
import com.example.ui.theme.SahayaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    onPostJob: (title: String, desc: String, category: String, address: String, budget: Double, negotiable: Boolean, urgency: UrgencyLevel) -> Unit,
    onBackClick: () -> Unit,
    initialUrgency: UrgencyLevel = UrgencyLevel.STANDARD,
    isHindi: Boolean = false
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String>(Constants.TRADE_CATEGORIES.first().nameEn) }
    var address by remember { mutableStateOf("Baner, Pune, MH") }
    var budgetStr by remember { mutableStateOf("750") }
    var isNegotiable by remember { mutableStateOf(true) }
    var urgency by remember { mutableStateOf(initialUrgency) }

    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

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
                modifier = Modifier.testTag("post_job_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "नया काम पोस्ट करें" else "Post a New Job",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Urgency Toggle Selector (Standard ≤30km vs Emergency ≤5km)
        Text(
            text = if (isHindi) "प्राथमिकता / आपातकालीन स्थिति:" else "Urgency & Distance Broadcast:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Standard Option
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (urgency == UrgencyLevel.STANDARD) 2.dp else 1.dp,
                        color = if (urgency == UrgencyLevel.STANDARD) SahayaPrimary else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { urgency = UrgencyLevel.STANDARD }
                    .testTag("post_job_urgency_standard"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (urgency == UrgencyLevel.STANDARD) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isHindi) "सामान्य (Standard)" else "Standard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (urgency == UrgencyLevel.STANDARD) SahayaPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "दायरा: ≤ 30 किमी" else "Radius: ≤ 30 km",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // Emergency Option
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (urgency == UrgencyLevel.EMERGENCY) 2.dp else 1.dp,
                        color = if (urgency == UrgencyLevel.EMERGENCY) SahayaEmergency else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { urgency = UrgencyLevel.EMERGENCY }
                    .testTag("post_job_urgency_emergency"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (urgency == UrgencyLevel.EMERGENCY) SahayaEmergencyContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isHindi) "तत्काल (SOS)" else "Emergency",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (urgency == UrgencyLevel.EMERGENCY) SahayaEmergency else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(6.dp).background(SahayaEmergency, CircleShape))
                    }
                    Text(
                        text = if (isHindi) "दायरा: ≤ 5 किमी (त्वरित)" else "Radius: ≤ 5 km (Fast)",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Service Category
        Text(
            text = if (isHindi) "सेवा श्रेणी चुनें (15 में से):" else "Select Trade Service (1 of 15):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = isCategoryDropdownOpen,
            onExpandedChange = { isCategoryDropdownOpen = !isCategoryDropdownOpen }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = TradeIcons.getIconForCategory(selectedCategory),
                        contentDescription = null,
                        tint = SahayaPrimary
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownOpen) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .testTag("post_job_category_dropdown")
            )
            ExposedDropdownMenu(
                expanded = isCategoryDropdownOpen,
                onDismissRequest = { isCategoryDropdownOpen = false }
            ) {
                Constants.TRADE_CATEGORIES.forEach { cat ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = TradeIcons.getIconForCategory(cat.title),
                                    contentDescription = null,
                                    tint = SahayaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(if (isHindi) cat.hindiTitle else cat.title)
                            }
                        },
                        onClick = {
                            selectedCategory = cat.title
                            isCategoryDropdownOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(if (isHindi) "कार्य का शीर्षक (उदा. वॉशबेसिन रिसाव मरम्मत)" else "Job Title (e.g. Washbasin Pipe Leak Repair)") },
            leadingIcon = { Icon(Icons.Default.Title, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_job_title_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(if (isHindi) "कार्य का विस्तृत विवरण" else "Detailed Task Description") },
            leadingIcon = { Icon(Icons.Default.Description, null) },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_job_description_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(if (isHindi) "स्थान / पता (पुणे, महाराष्ट्र)" else "Address / Location (Pune, MH)") },
            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_job_address_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Budget & Negotiable
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = budgetStr,
                onValueChange = { budgetStr = it },
                label = { Text(if (isHindi) "बजट (₹)" else "Budget (₹)") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("post_job_budget_input")
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column {
                    Text(
                        text = if (isHindi) "मोलभाव" else "Negotiable",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isNegotiable) "Yes" else "Fixed",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = isNegotiable,
                    onCheckedChange = { isNegotiable = it },
                    modifier = Modifier.testTag("post_job_negotiable_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                val budget = budgetStr.toDoubleOrNull() ?: 500.0
                onPostJob(
                    if (title.isBlank()) "Service for $selectedCategory" else title,
                    if (description.isBlank()) "Standard maintenance requested" else description,
                    selectedCategory,
                    address,
                    budget,
                    isNegotiable,
                    urgency
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("post_job_submit_button")
        ) {
            Text(
                text = if (urgency == UrgencyLevel.EMERGENCY) {
                    if (isHindi) "🚨 5 किमी में तत्काल कारीगरों को अलर्ट भेजें" else "🚨 Post SOS & Alert Workers Within 5km"
                } else {
                    if (isHindi) "काम पोस्ट करें एवं कारीगरों को अलर्ट भेजें" else "Post Job & Broadcast to Tradespeople"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
