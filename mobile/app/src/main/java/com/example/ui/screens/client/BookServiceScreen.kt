package com.example.ui.screens.client

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.TradeProfile
import com.example.ui.theme.SahayaPrimary

@Composable
fun BookServiceScreen(
    worker: TradeProfile?,
    onBookService: (worker: TradeProfile, date: String, timeSlot: String, description: String, budget: Double) -> Unit,
    onBackClick: () -> Unit,
    isHindi: Boolean = false
) {
    if (worker == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (isHindi) "कारीगर नहीं मिला" else "Worker not found")
        }
        return
    }

    var selectedDate by remember { mutableStateOf("Tomorrow, 10:00 AM") }
    var selectedSlot by remember { mutableStateOf(Constants.TIME_SLOTS[1]) }
    var description by remember { mutableStateOf("") }
    var budgetStr by remember { mutableStateOf(worker.hourlyRate.toInt().toString()) }

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
                modifier = Modifier.testTag("book_service_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "सीधी सेवा बुक करें" else "Book Direct Service",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Worker Summary
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = worker.workerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${worker.tradeCategory} • ₹${worker.hourlyRate.toInt()}/hr base rate",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Preferred Date
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text(if (isHindi) "तारीख / दिन" else "Preferred Date") },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("book_service_date_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time Slot Selection (9 slots)
        Text(
            text = if (isHindi) "समय स्लॉट चुनें (9 विकल्प):" else "Select 1-Hour Time Slot (9 options):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display slots in 2-column list
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Constants.TIME_SLOTS.chunked(2).forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSlots.forEach { slot ->
                        val isSelected = selectedSlot == slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) SahayaPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSlot = slot }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = slot,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(if (isHindi) "कार्य का विवरण" else "Description of Work Needed") },
            leadingIcon = { Icon(Icons.Default.Description, null) },
            minLines = 3,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("book_service_desc_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Budget
        OutlinedTextField(
            value = budgetStr,
            onValueChange = { budgetStr = it },
            label = { Text(if (isHindi) "प्रस्तावित राशि (₹)" else "Proposed Total Budget (₹)") },
            leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("book_service_budget_input")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val budget = budgetStr.toDoubleOrNull() ?: worker.hourlyRate
                onBookService(
                    worker,
                    selectedDate,
                    selectedSlot,
                    if (description.isBlank()) "Service request for ${worker.tradeCategory}" else description,
                    budget
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("book_service_submit_button")
        ) {
            Text(
                text = if (isHindi) "बुकिंग अनुरोध भेजें" else "Send Service Booking Request",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
