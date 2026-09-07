package com.example.ui.screens.worker

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Job
import com.example.data.model.UrgencyLevel
import com.example.data.model.User
import com.example.ui.components.JobCard
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaSecondary

@Composable
fun WorkerMarketplaceScreen(
    user: User?,
    jobs: List<Job>,
    onJobClick: (Job) -> Unit,
    onAcceptJobClick: (Job) -> Unit,
    isHindi: Boolean = false
) {
    var emergencyOnlyFilter by remember { mutableStateOf(false) }

    val filteredJobs = remember(jobs, emergencyOnlyFilter) {
        if (emergencyOnlyFilter) {
            jobs.filter { it.urgencyLevel == UrgencyLevel.EMERGENCY }
        } else {
            jobs
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHindi) "उपलब्ध कार्य (मार्केटप्लेस)" else "Available Jobs",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SahayaSecondary
                )
                Text(
                    text = if (isHindi) "ग्राहकों द्वारा पोस्ट किए गए ऑन-डिमांड कार्य" else "Direct client service postings with escrow guarantee",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Urgency Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (!emergencyOnlyFilter) SahayaPrimary else Color.White,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { emergencyOnlyFilter = false }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("worker_filter_all")
            ) {
                Text(
                    text = if (isHindi) "सभी कार्य (${jobs.size})" else "All Tasks (${jobs.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!emergencyOnlyFilter) Color.White else SahayaSecondary
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        if (emergencyOnlyFilter) SahayaEmergency else Color.White,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { emergencyOnlyFilter = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("worker_filter_emergency")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = if (emergencyOnlyFilter) Color.White else SahayaEmergency,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "तत्काल SOS" else "Emergency SOS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (emergencyOnlyFilter) Color.White else SahayaSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clean Jobs List
        if (filteredJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHindi) "वर्तमान में कोई काम उपलब्ध नहीं है" else "No Available Jobs Right Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SahayaSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "जैसे ही ग्राहक नया काम पोस्ट करेंगे, वह यहाँ दिखाई देगा।" else "New client requests will appear here instantly.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onClick = { onJobClick(job) },
                        isHindi = isHindi,
                        showDistance = false
                    )
                }
            }
        }
    }
}
