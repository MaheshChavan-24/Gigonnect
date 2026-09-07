package com.example.ui.screens.worker

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.EscrowStatus
import com.example.data.model.Job
import com.example.data.model.JobStatus
import com.example.ui.components.EscrowStatusBar
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.StarRatingBar
import com.example.ui.components.TradeIcons
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaPrimaryContainer
import com.example.ui.theme.SahayaSuccess

@Composable
fun ActiveJobScreen(
    activeJob: Job?,
    onMarkDoneClick: (Job) -> Unit,
    onFindJobsClick: () -> Unit,
    onSubmitWorkerReview: (clientId: Long, clientName: String, rating: Int, comment: String, serviceType: String) -> Unit,
    isHindi: Boolean = false
) {
    if (activeJob == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(SahayaPrimaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = null,
                        tint = SahayaPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isHindi) "कोई सक्रिय कार्य नहीं है" else "No Active Job Right Now",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isHindi) "मार्केटप्लेस से तुरंत काम स्वीकारें" else "Browse the marketplace radar to accept nearby repair jobs",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onFindJobsClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("active_job_find_jobs_button")
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "काम खोजें" else "Find Jobs",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    val scrollState = rememberScrollState()

    // Two-way review: worker rates client after escrow is released
    var showWorkerReviewDialog by remember { mutableStateOf(false) }
    var workerReviewRating by remember { mutableIntStateOf(5) }
    var workerReviewComment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text(
            text = if (isHindi) "सक्रिय कार्य (Active Work)" else "Active Ongoing Job",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Escrow Timeline
        EscrowStatusBar(
            jobStatus = activeJob.status,
            escrowStatus = activeJob.escrowStatus,
            isHindi = isHindi
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Job Details Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SahayaPrimaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = TradeIcons.getIconForCategory(activeJob.serviceType),
                                contentDescription = null,
                                tint = SahayaPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = activeJob.serviceType,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    JobStatusBadge(status = activeJob.status, isHindi = isHindi)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = activeJob.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activeJob.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activeJob.address,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payout guarantee box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "एस्क्रो राशि (पूर्ण होने पर):" else "Escrow Payout (Upon Completion):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹${activeJob.budget.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Client Info
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE2E8F0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.DarkGray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isHindi) "ग्राहक विवरण" else "Client",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = activeJob.clientName ?: "Verified Client",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Completion Action / Warnings
        if (activeJob.escrowStatus == EscrowStatus.HELD && activeJob.status == JobStatus.ACCEPTED) {
            Button(
                onClick = { onMarkDoneClick(activeJob) },
                colors = ButtonDefaults.buttonColors(containerColor = SahayaSuccess),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("worker_active_job_mark_done")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "कार्य पूर्ण चिह्नित करें (Mark Complete)" else "Mark Completed & Request Release",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (activeJob.status == JobStatus.WORKER_COMPLETED) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = SahayaSuccess)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isHindi) "कार्य पूर्ण चिह्नित हो चुका है। ग्राहक द्वारा एस्क्रो राशि जारी करने की प्रतीक्षा है।" else "Work submitted! Awaiting client to approve and release escrow payout.",
                        fontSize = 12.sp,
                        color = Color(0xFF14532D)
                    )
                }
            }
        } else if (activeJob.escrowStatus == EscrowStatus.PENDING) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFB45309))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isHindi) "ग्राहक द्वारा एस्क्रो भुगतान जमा होने की प्रतीक्षा है। भुगतान होते ही कार्य प्रारंभ करें।" else "Awaiting client to fund escrow deposit. You will receive notification as soon as money is secured.",
                        fontSize = 12.sp,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }
        // COMPLETED → Worker rates the client (two-way review)
        if (activeJob.status == JobStatus.COMPLETED) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHindi) "कार्य सफलतापूर्वक पूर्ण हुआ!" else "Job Successfully Completed!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SahayaSuccess
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showWorkerReviewDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SahayaPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("worker_rate_client_button")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "ग्राहक को रेटिंग दें" else "Rate the Client",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Worker-to-client review dialog
    if (showWorkerReviewDialog && activeJob != null) {
        Dialog(onDismissRequest = { showWorkerReviewDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "ग्राहक की समीक्षा करें" else "Rate & Review Client",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = activeJob.clientName ?: "Client",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    StarRatingBar(
                        rating = workerReviewRating,
                        onRatingChanged = { workerReviewRating = it }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = workerReviewComment,
                        onValueChange = { workerReviewComment = it },
                        label = { Text(if (isHindi) "समीक्षा लिखें" else "Your feedback (e.g. punctual, cooperative)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showWorkerReviewDialog = false }) {
                            Text(if (isHindi) "छोड़ें" else "Skip")
                        }
                        Button(
                            onClick = {
                                onSubmitWorkerReview(
                                    activeJob.clientId ?: 0L,
                                    activeJob.clientName ?: "Client",
                                    workerReviewRating,
                                    workerReviewComment,
                                    activeJob.serviceType
                                )
                                showWorkerReviewDialog = false
                            }
                        ) {
                            Text(if (isHindi) "समीक्षा सबमिट करें" else "Submit Review")
                        }
                    }
                }
            }
        }
    }
}
