package com.example.ui.screens.client

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Constants
import com.example.data.model.Job
import com.example.data.model.TradeCategory
import com.example.data.model.User
import com.example.ui.components.JobCard
import com.example.ui.components.TradeIcons
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoDarkTile
import com.example.ui.theme.BentoDarkTileMuted
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoOutline
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoPrimaryDark
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaSuccess

@Composable
fun ClientDashboardScreen(
    user: User?,
    clientJobs: List<Job>,
    onPostJobClick: () -> Unit,
    onBrowseTradesClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onJobClick: (Job) -> Unit,
    onEmergencyPostClick: () -> Unit,
    isHindi: Boolean = false
) {
    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPostJobClick,
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("client_fab_post_job")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Post Job", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "काम पोस्ट करें" else "Post a Job",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BentoBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Bento Header: Avatar + User Info + Action Button
                BentoHeader(
                    user = user,
                    isHindi = isHindi,
                    onEmergencyClick = onEmergencyPostClick
                )
            }

            // Bento Tile 1: Hero Card (col-span-2)
            item {
                BentoHeroVaultTile(
                    clientJobs = clientJobs,
                    onPostJobClick = onPostJobClick,
                    isHindi = isHindi
                )
            }

            // Bento Row 2: Two Square Tiles (col-span-1 each)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Square Tile 1: Verified Trades
                    BentoTradesTile(
                        modifier = Modifier.weight(1f),
                        onBrowseTradesClick = onBrowseTradesClick,
                        isHindi = isHindi
                    )

                    // Square Tile 2: Quick Actions
                    BentoQuickActionsTile(
                        modifier = Modifier.weight(1f),
                        onPostJobClick = onPostJobClick,
                        onBrowseTradesClick = onBrowseTradesClick,
                        onEmergencyClick = onEmergencyPostClick,
                        isHindi = isHindi
                    )
                }
            }

            // Bento Tile 3: Dark High-Contrast Emergency Tile (col-span-2)
            item {
                BentoDarkEmergencyTile(
                    onEmergencyClick = onEmergencyPostClick,
                    isHindi = isHindi
                )
            }

            // Bento Categories Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "कुशल सेवाएं" else "Trade Services",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = if (isHindi) "15 प्रमाणित श्रेणियां" else "15 Verified Local Categories",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                    TextButton(onClick = onBrowseTradesClick) {
                        Text(
                            text = if (isHindi) "सभी देखें" else "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                    }
                }
            }

            // Bento Category Carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Constants.TRADE_CATEGORIES) { cat ->
                        BentoCategoryTile(
                            category = cat,
                            onClick = { onCategoryClick(cat.id) },
                            isHindi = isHindi
                        )
                    }
                }
            }

            // My Active & Recent Jobs Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "मेरी पोस्ट की गई नौकरियां" else "My Posted Jobs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "${clientJobs.size} ${if (isHindi) "सक्रिय अनुरोध" else "active requests"}",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }

            // Jobs list in Bento Grid style
            if (clientJobs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoOutline.copy(alpha = 0.6f)),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(BentoPrimaryContainer, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    tint = BentoPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "कोई सक्रिय कार्य नहीं" else "No Active Jobs Yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHindi) "प्लंबर, इलेक्ट्रीशियन या बढ़ई के लिए नया काम बनाएं" else "Post a task to connect with top-rated local tradespeople",
                                fontSize = 12.sp,
                                color = BentoTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(clientJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onClick = { onJobClick(job) },
                        isHindi = isHindi,
                        showDistance = false
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun BentoHeader(
    user: User?,
    isHindi: Boolean,
    onEmergencyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bento Avatar with User Initials
            val initials = (user?.username ?: "Jameson Doe").split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "JD" }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BentoPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Column {
                Text(
                    text = if (isHindi) "शुभ प्रभात • पुणे" else "GOOD MORNING • PUNE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = BentoTextSecondary
                )
                Text(
                    text = user?.username ?: "Jameson Doe",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
            }
        }

        // Circular Bento Border Action Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(BentoSurfaceVariant, CircleShape)
                .border(1.dp, BentoOutline, CircleShape)
                .clickable(onClick = onEmergencyClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Escrow Safe",
                tint = SahayaSuccess,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BentoHeroVaultTile(
    clientJobs: List<Job>,
    onPostJobClick: () -> Unit,
    isHindi: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_hero_vault_tile"),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, BentoOutline.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "एस्क्रो वॉल्ट और बुकिंग्स" else "Total Escrow Protected",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoOnPrimaryContainer
                )

                // Bento Live Badge
                Box(
                    modifier = Modifier
                        .background(BentoOnPrimaryContainer, RoundedCornerShape(100.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isHindi) "लाइव सुरक्षित" else "LIVE SECURE",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val totalBudget = clientJobs.sumOf { it.budget.toInt() }
            val displayAmount = if (totalBudget > 0) "₹$totalBudget" else "₹2,850.00"

            Text(
                text = displayAmount,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = BentoOnPrimaryContainer
            )

            Text(
                text = if (isHindi) "100% माइलस्टोन एस्क्रो सुरक्षा • काम पूरा होने पर भुगतान" else "+100% Milestone protected vs direct contractor cash",
                fontSize = 12.sp,
                color = BentoTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPostJobClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "नया काम पोस्ट करें" else "Post New Job",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BentoOnPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "100% Refundable",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoOnPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoTradesTile(
    modifier: Modifier = Modifier,
    onBrowseTradesClick: () -> Unit,
    isHindi: Boolean
) {
    Card(
        modifier = modifier
            .height(170.dp)
            .clickable(onClick = onBrowseTradesClick)
            .testTag("bento_trades_tile"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BentoOutline),
        colors = CardDefaults.cardColors(containerColor = BentoSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(BentoPrimaryDark, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Handyman,
                    contentDescription = "Trades",
                    tint = BentoOnPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = if (isHindi) "प्रमाणित कारीगर" else "Verified Trades",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isHindi) "15 श्रेणियां" else "15 Skills",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = BentoTextPrimary
                )
                Text(
                    text = if (isHindi) "प्लंबर, इलेक्ट्रीशियन..." else "Electrician, Plumber...",
                    fontSize = 10.sp,
                    color = BentoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BentoQuickActionsTile(
    modifier: Modifier = Modifier,
    onPostJobClick: () -> Unit,
    onBrowseTradesClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    isHindi: Boolean
) {
    Card(
        modifier = modifier
            .height(170.dp)
            .testTag("bento_quick_actions_tile"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BentoOutline),
        colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isHindi) "त्वरित क्रियाएं" else "QUICK ACTIONS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = BentoTextSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Action 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onPostJobClick)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = BentoPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "काम पोस्ट" else "Post Task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextPrimary
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BentoOutline.copy(alpha = 0.4f)))

                // Action 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onBrowseTradesClick)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = BentoPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "कारीगर बुक" else "Book Pro",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextPrimary
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BentoOutline.copy(alpha = 0.4f)))

                // Action 3
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onEmergencyClick)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp), tint = SahayaEmergency)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "आपातकाल SOS" else "Urgent SOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SahayaEmergency
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoDarkEmergencyTile(
    onEmergencyClick: () -> Unit,
    isHindi: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEmergencyClick)
            .testTag("client_emergency_banner"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = BentoDarkTile),
        border = BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SahayaEmergency, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "तत्काल प्रेषण ≤ 5KM" else "RAPID DISPATCH ≤ 5KM",
                        color = Color(0xFFFFDAD6),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isHindi) "आपातकालीन मरम्मत SOS" else "Urgent Repair SOS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isHindi) "बिजली शॉर्ट सर्किट, पाइप लीकेज पर 15 मिनट में कारीगर" else "Fast response for power outages, pipe bursts & gas leaks.",
                    color = BentoDarkTileMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onEmergencyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahayaEmergency,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("emergency_button_action")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "SOS" else "SOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoCategoryTile(
    category: TradeCategory,
    onClick: () -> Unit,
    isHindi: Boolean
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(130.dp)
            .clickable(onClick = onClick)
            .testTag("category_chip_${category.id}"),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, BentoOutline.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = BentoSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(BentoPrimaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TradeIcons.getIconForCategory(category.title),
                    contentDescription = category.title,
                    tint = BentoPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isHindi) category.hindiTitle else category.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "₹${category.baseHourlyRate.toInt()}/hr",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SahayaAmber
                )
            }
        }
    }
}
