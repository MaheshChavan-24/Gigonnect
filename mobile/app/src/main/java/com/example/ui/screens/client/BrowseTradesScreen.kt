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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.TradeIcons
import com.example.ui.components.WorkerCard
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaPrimaryContainer

@Composable
fun BrowseTradesScreen(
    profiles: List<TradeProfile>,
    selectedCategoryKey: String?,
    onCategorySelected: (String?) -> Unit,
    onWorkerClick: (TradeProfile) -> Unit,
    onBookWorkerClick: (TradeProfile) -> Unit,
    isHindi: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredProfiles = remember(profiles, selectedCategoryKey, searchQuery) {
        profiles.filter { profile ->
            val matchCategory = selectedCategoryKey == null ||
                profile.tradeCategory.contains(selectedCategoryKey, ignoreCase = true) ||
                selectedCategoryKey.contains(profile.tradeCategory, ignoreCase = true)

            val matchSearch = searchQuery.isBlank() ||
                profile.workerName.contains(searchQuery, ignoreCase = true) ||
                profile.displayName.contains(searchQuery, ignoreCase = true) ||
                profile.skills.contains(searchQuery, ignoreCase = true)

            matchCategory && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        // Header
        Text(
            text = if (isHindi) "कुशल कारीगर खोजें" else "Browse Skilled Trades",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isHindi) "कारीगर, कौशल या सेवा खोजें..." else "Search by name, skill, or service...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("browse_trades_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Horizontal Row (with "All" + 15 categories)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                CategoryPill(
                    title = if (isHindi) "सभी (All)" else "All Trades",
                    isSelected = selectedCategoryKey == null,
                    onClick = { onCategorySelected(null) }
                )
            }

            items(Constants.TRADE_CATEGORIES) { cat ->
                CategoryPill(
                    title = if (isHindi) cat.hindiTitle else cat.title,
                    isSelected = selectedCategoryKey == cat.id || selectedCategoryKey == cat.title,
                    onClick = {
                        if (selectedCategoryKey == cat.id || selectedCategoryKey == cat.title) {
                            onCategorySelected(null)
                        } else {
                            onCategorySelected(cat.title)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Results count
        Text(
            text = "${filteredProfiles.size} ${if (isHindi) "कारीगर उपलब्ध" else "verified tradespeople found"}",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Worker Profiles List
        if (filteredProfiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isHindi) "कोई कारीगर नहीं मिला" else "No tradespeople found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "कृपया अन्य श्रेणी या खोज शब्द चुनें" else "Try clearing your filters or changing search keywords",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProfiles, key = { it.id }) { profile ->
                    WorkerCard(
                        profile = profile,
                        onViewDetail = { onWorkerClick(profile) },
                        onBookClick = { onBookWorkerClick(profile) },
                        isHindi = isHindi
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) SahayaPrimary else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
