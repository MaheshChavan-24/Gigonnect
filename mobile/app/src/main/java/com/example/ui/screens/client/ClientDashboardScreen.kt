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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaPrimaryContainer
import com.example.ui.theme.SahayaSecondary
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
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header: User Greeting & Escrow Trust Status
            item {
                Spacer(modifier = Modifier.height(4.dp))
                DashboardHeader(
                    user = user,
                    isHindi = isHindi
                )
            }

            // Single Hero Action Card
            item {
                HeroActionCard(
                    onPostJobClick = onPostJobClick,
                    onBrowseTradesClick = onBrowseTradesClick,
                    isHindi = isHindi
                )
            }

            // 100% Escrow Guarantee Trust Banner
            item {
                EscrowTrustBanner(isHindi = isHindi)
            }

            // Categories Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "कुशल सेवाएं एवं कारीगर" else "Verified Trade Services",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = SahayaSecondary
                    )
                    TextButton(onClick = onBrowseTradesClick) {
                        Text(
                            text = if (isHindi) "सभी देखें" else "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SahayaPrimary
                        )
                    }
                }
            }

            // 4x2 Category Grid
            item {
                val displayCategories = Constants.TRADE_CATEGORIES.take(8)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    displayCategories.chunked(4).forEach { rowCats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowCats.forEach { cat ->
                                CategoryGridItem(
                                    category = cat,
                                    onClick = { onCategoryClick(cat.id) },
                                    isHindi = isHindi,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining space if less than 4
                            for (i in 0 until (4 - rowCats.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // My Active & Recent Jobs Header
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "मेरे कार्य एवं अनुरोध" else "My Service Requests",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SahayaSecondary
                        )
                        Text(
                            text = "${clientJobs.size} ${if (isHindi) "सक्रिय अनुरोध" else "active bookings"}",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Jobs list
            if (clientJobs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isHindi) "कोई सक्रिय कार्य नहीं" else "No Active Requests",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SahayaSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isHindi) "नया काम पोस्ट करें और नजदीकी सत्यापित कारीगरों से जुड़ें" else "Post a task to connect with top-rated nearby pros",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
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
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    user: User?,
    isHindi: Boolean
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
            val initials = (user?.username ?: "Client").split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "C" }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SahayaSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Column {
                Text(
                    text = if (isHindi) "नमस्ते 👋" else "Welcome Back 👋",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = user?.username ?: "Client",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = SahayaSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Escrow Protected",
                    tint = SahayaSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isHindi) "एस्क्रो सुरक्षित" else "100% Escrow",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SahayaSuccess
                )
            }
        }
    }
}

@Composable
private fun HeroActionCard(
    onPostJobClick: () -> Unit,
    onBrowseTradesClick: () -> Unit,
    isHindi: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("client_hero_action_card"),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFFED7AA)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = if (isHindi) "आज किसी कुशल कारीगर की आवश्यकता है?" else "Need a Skilled Trade Pro Today?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF7C2D12),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isHindi) "तुरंत काम पोस्ट करें या 15+ प्रमाणित कारीगरों की सूची देखें।" else "Post your repair requirements or browse KYC-verified professionals.",
                fontSize = 12.sp,
                color = Color(0xFF9A3412),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPostJobClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SahayaPrimary),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp)
                        .testTag("hero_post_job_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "काम पोस्ट करें" else "Post a Job",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onBrowseTradesClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SahayaSecondary),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("hero_browse_trades_button")
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "कारीगर खोजें" else "Browse",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EscrowTrustBanner(isHindi: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0FDF4), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = SahayaSuccess,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = if (isHindi) {
                "100% सुरक्षित एस्क्रो: काम पूरा होने और आपकी संतुष्टि पर ही भुगतान।"
            } else {
                "100% Escrow Protection: Funds released only after your approval."
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF166534),
            lineHeight = 15.sp
        )
    }
}

@Composable
private fun CategoryGridItem(
    category: TradeCategory,
    onClick: () -> Unit,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("category_grid_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFFFFF7ED), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TradeIcons.getIconForCategory(category.title),
                    contentDescription = category.title,
                    tint = SahayaPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isHindi) category.hindiTitle else category.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SahayaSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

