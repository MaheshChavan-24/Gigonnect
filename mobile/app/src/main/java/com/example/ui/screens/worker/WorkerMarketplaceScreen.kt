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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Job
import com.example.data.model.UrgencyLevel
import com.example.data.model.User
import com.example.ui.components.InteractiveJobMap
import com.example.ui.components.JobCard
import com.example.ui.theme.SahayaAmber
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaPrimary

@Composable
fun WorkerMarketplaceScreen(
    user: User?,
    jobs: List<Job>,
    onJobClick: (Job) -> Unit,
    onAcceptJobClick: (Job) -> Unit,
    isHindi: Boolean = false
) {
    var isMapView by remember { mutableStateOf(true) }
    var emergencyOnlyFilter by remember { mutableStateOf(false) }
    var selectedMapJob by remember { mutableStateOf<Job?>(null) }

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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHindi) "उपलब्ध काम (मार्केटप्लेस)" else "Job Marketplace",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isHindi) "पुणे दायरा: ≤5km तत्काल | ≤30km मानक" else "Pune area: ≤5km SOS | ≤30km Standard",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Map vs List Toggle
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isMapView) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { isMapView = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("worker_toggle_map_view")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map View",
                        tint = if (isMapView) SahayaPrimary else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (!isMapView) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { isMapView = false }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("worker_toggle_list_view")
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "List View",
                        tint = if (!isMapView) SahayaPrimary else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Bar (Emergency vs All)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (!emergencyOnlyFilter) SahayaPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { emergencyOnlyFilter = false }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "सभी काम (${jobs.size})" else "All Jobs (${jobs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!emergencyOnlyFilter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        if (emergencyOnlyFilter) SahayaEmergency else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { emergencyOnlyFilter = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("worker_filter_emergency")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (emergencyOnlyFilter) Color.White else SahayaEmergency, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "तत्काल (≤ 5km)" else "Emergency (≤ 5km)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (emergencyOnlyFilter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // View Content
        if (isMapView) {
            // Interactive Map Canvas
            InteractiveJobMap(
                jobs = filteredJobs,
                selectedJob = selectedMapJob,
                onJobSelected = { job -> selectedMapJob = job },
                isHindi = isHindi
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Job Preview Card below Map
            if (selectedMapJob != null) {
                Text(
                    text = if (isHindi) "चयनित कार्य:" else "Selected Job (Map Pin):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                JobCard(
                    job = selectedMapJob!!,
                    onClick = { onJobClick(selectedMapJob!!) },
                    isHindi = isHindi
                )
            } else {
                Text(
                    text = "${filteredJobs.size} jobs displayed on GPS radar. Tap a pin to inspect.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            // List View
            if (filteredJobs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (isHindi) "कोई काम उपलब्ध नहीं है" else "No jobs available currently")
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
                            isHindi = isHindi
                        )
                    }
                }
            }
        }
    }
}
