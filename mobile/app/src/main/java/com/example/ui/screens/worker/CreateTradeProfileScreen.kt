package com.example.ui.screens.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Constants
import com.example.ui.components.TradeIcons
import com.example.ui.theme.SahayaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTradeProfileScreen(
    onSubmit: (
        displayName: String,
        category: String,
        skills: String,
        experienceDesc: String,
        years: Int,
        availability: String,
        tools: String,
        languages: String,
        rate: Double
    ) -> Unit,
    onBackClick: () -> Unit,
    isHindi: Boolean = false
) {
    var selectedCategory by remember { mutableStateOf<String>(Constants.TRADE_CATEGORIES.first().nameEn) }
    var displayName by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var experienceDesc by remember { mutableStateOf("") }
    var yearsStr by remember { mutableStateOf("5") }
    var availability by remember { mutableStateOf("Mon-Sat 9AM-7PM") }
    var tools by remember { mutableStateOf("Full professional toolkit") }
    var languages by remember { mutableStateOf("Hindi, Marathi, English") }
    var rateStr by remember { mutableStateOf("450") }

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
                modifier = Modifier.testTag("create_profile_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "ट्रेड प्रोफाइल प्रकाशित करें" else "Create Trade Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trade Category Dropdown (1 of 15)
        Text(
            text = if (isHindi) "ट्रेड श्रेणी (15 में से एक):" else "Trade Category (1 of 15):",
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
                    .testTag("create_profile_category_dropdown")
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
                            rateStr = cat.baseHourlyRate.toInt().toString()
                            isCategoryDropdownOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Display Title
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text(if (isHindi) "प्रदर्शन नाम / पद (उदा. मास्टर प्लंबर)" else "Display Title (e.g. Master Plumber & Pipe Specialist)") },
            leadingIcon = { Icon(Icons.Default.Title, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_profile_title_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Skills
        OutlinedTextField(
            value = skills,
            onValueChange = { skills = it },
            label = { Text(if (isHindi) "कौशल (अल्पविराम से अलग करें)" else "Skills (comma separated, e.g. Leak Detection, Geyser Repair)") },
            leadingIcon = { Icon(Icons.Default.Handyman, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_profile_skills_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Experience Description
        OutlinedTextField(
            value = experienceDesc,
            onValueChange = { experienceDesc = it },
            label = { Text(if (isHindi) "अनुभव का विवरण" else "Experience Summary & Track Record") },
            leadingIcon = { Icon(Icons.Default.Description, null) },
            minLines = 3,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_profile_desc_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Years & Rate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = yearsStr,
                onValueChange = { yearsStr = it },
                label = { Text(if (isHindi) "अनुभव (वर्ष)" else "Years Exp") },
                leadingIcon = { Icon(Icons.Default.Work, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("create_profile_years_input")
            )

            OutlinedTextField(
                value = rateStr,
                onValueChange = { rateStr = it },
                label = { Text(if (isHindi) "दर (₹/घंटा)" else "Rate (₹/hr)") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("create_profile_rate_input")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tools Owned
        OutlinedTextField(
            value = tools,
            onValueChange = { tools = it },
            label = { Text(if (isHindi) "उपलब्ध उपकरण" else "Tools Owned") },
            leadingIcon = { Icon(Icons.Default.Build, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Availability & Languages
        OutlinedTextField(
            value = availability,
            onValueChange = { availability = it },
            label = { Text(if (isHindi) "उपलब्ध समय" else "Availability Schedule") },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = languages,
            onValueChange = { languages = it },
            label = { Text(if (isHindi) "ज्ञात भाषाएं" else "Languages Spoken") },
            leadingIcon = { Icon(Icons.Default.Language, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val years = yearsStr.toIntOrNull() ?: 3
                val rate = rateStr.toDoubleOrNull() ?: 400.0
                onSubmit(
                    if (displayName.isBlank()) "Professional $selectedCategory" else displayName,
                    selectedCategory,
                    if (skills.isBlank()) "General $selectedCategory Services" else skills,
                    if (experienceDesc.isBlank()) "Experienced tradesperson in $selectedCategory with verified client reviews." else experienceDesc,
                    years,
                    availability,
                    tools,
                    languages,
                    rate
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("create_profile_submit_button")
        ) {
            Text(
                text = if (isHindi) "प्रोफाइल प्रकाशित करें" else "Publish Trade Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
