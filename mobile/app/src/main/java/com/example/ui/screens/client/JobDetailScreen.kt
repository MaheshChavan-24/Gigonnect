package com.example.ui.screens.client

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.EscrowStatus
import com.example.data.model.Job
import com.example.data.model.JobStatus
import com.example.data.model.UserRole
import com.example.ui.components.EscrowStatusBar
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.StarRatingBar
import com.example.ui.components.TradeIcons
import com.example.ui.components.UrgencyBadge
import com.example.ui.theme.SahayaEmergency
import com.example.ui.theme.SahayaPrimary
import com.example.ui.theme.SahayaSuccess

@Composable
fun JobDetailScreen(
    job: Job?,
    activeRole: UserRole,
    onBackClick: () -> Unit,
    onPayEscrowClick: (Job) -> Unit,
    onWorkerAcceptClick: (Job) -> Unit,
    onWorkerMarkCompleteClick: (Job) -> Unit,
    onClientApproveReleaseClick: (Job) -> Unit,
    onClientDisputeClick: (Job, String) -> Unit,
    onSubmitReview: (workerId: Long, workerName: String, rating: Int, comment: String, serviceType: String) -> Unit,
    isHindi: Boolean = false
) {
    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (isHindi) "कार्य विवरण नहीं मिला" else "Job not found")
        }
        return
    }

    var showDisputeDialog by remember { mutableStateOf(false) }
    var disputeReason by remember { mutableStateOf("") }

    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Back Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("job_detail_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            UrgencyBadge(urgency = job.urgencyLevel, isHindi = isHindi)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title and Category
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TradeIcons.getIconForCategory(job.serviceType),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = job.serviceType,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = job.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Escrow Timeline Status Bar
        EscrowStatusBar(
            jobStatus = job.status,
            escrowStatus = job.escrowStatus,
            isHindi = isHindi
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Job Information Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isHindi) "कार्य का विवरण" else "Task Description",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "स्थान / पता" else "Service Location",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = job.address,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isHindi) "दूरी" else "Distance",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${job.distanceKm} km away",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Budget Highlight
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "अनुमानित लागत / बजट:" else "Contract Budget:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹${job.budget.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Worker Assignment Card (if accepted)
        if (job.workerName != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SahayaPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "नियुक्त कुशल कामगार:" else "Assigned Tradesperson:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = job.workerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    JobStatusBadge(status = job.status, isHindi = isHindi)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Action Buttons based on Role & State ---

        // A. Worker Actions
        if (activeRole == UserRole.WORKER) {
            if (job.status == JobStatus.PENDING) {
                Button(
                    onClick = { onWorkerAcceptClick(job) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("worker_accept_job_button")
                ) {
                    Text(
                        text = if (isHindi) "काम स्वीकारें (Accept Job)" else "Accept Job & Connect with Client",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else if (job.status == JobStatus.ACCEPTED && job.escrowStatus == EscrowStatus.HELD) {
                Button(
                    onClick = { onWorkerMarkCompleteClick(job) },
                    colors = ButtonDefaults.buttonColors(containerColor = SahayaSuccess),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("worker_mark_done_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "कार्य पूर्ण चिह्नित करें (Mark Done)" else "Mark Work Completed & Request Release",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else if (job.status == JobStatus.ACCEPTED && job.escrowStatus == EscrowStatus.PENDING) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Text(
                        text = if (isHindi) "ग्राहक द्वारा एस्क्रो में भुगतान जमा करने की प्रतीक्षा है।" else "Awaiting client to fund Escrow before you begin work.",
                        fontSize = 12.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        // B. Client Actions
        if (activeRole == UserRole.CLIENT) {
            // 1. Pay Escrow via Razorpay
            if (job.status == JobStatus.ACCEPTED && job.escrowStatus == EscrowStatus.PENDING) {
                Button(
                    onClick = { onPayEscrowClick(job) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C72EC)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("client_pay_escrow_button")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "₹${job.budget.toInt()} एस्क्रो भुगतान करें (Razorpay)" else "Pay Escrow Deposit (₹${job.budget.toInt()})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // 2. Worker Marked Complete -> Approve & Release OR Dispute
            if (job.status == JobStatus.WORKER_COMPLETED) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onClientApproveReleaseClick(job) },
                        colors = ButtonDefaults.buttonColors(containerColor = SahayaSuccess),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("client_approve_release_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "काम स्वीकृत करें एवं ₹${job.budget.toInt()} जारी करें" else "Approve Work & Release Escrow Funds",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { showDisputeDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SahayaEmergency),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("client_dispute_button")
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "विवाद दर्ज करें (Dispute)" else "Open a Dispute",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Completed -> Leave Review
            if (job.status == JobStatus.COMPLETED) {
                Button(
                    onClick = { showReviewDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("client_write_review_button")
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "कारीगर को समीक्षा व रेटिंग दें" else "Write a Review & Rate Worker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Dispute Dialog
    if (showDisputeDialog) {
        Dialog(onDismissRequest = { showDisputeDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isHindi) "कार्य विवाद दर्ज करें" else "Open Job Dispute",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isHindi) "सहाय टीम मध्यस्थता करेगी और जांच तक राशि एस्क्रो में सुरक्षित रहेगी।" else "Funds will remain held securely in escrow while Sahaya customer support mediates.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        label = { Text("Reason for dispute") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDisputeDialog = false }) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                onClientDisputeClick(job, disputeReason)
                                showDisputeDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SahayaEmergency)
                        ) {
                            Text("Submit Dispute")
                        }
                    }
                }
            }
        }
    }

    // Review Dialog
    if (showReviewDialog) {
        Dialog(onDismissRequest = { showReviewDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isHindi) "कारीगर की समीक्षा करें" else "Rate & Review Worker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    StarRatingBar(
                        rating = reviewRating,
                        onRatingChanged = { reviewRating = it }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Your feedback (e.g. prompt, clean work)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showReviewDialog = false }) {
                            Text("Skip")
                        }
                        Button(
                            onClick = {
                                onSubmitReview(
                                    job.workerId ?: 10L,
                                    job.workerName ?: "Worker",
                                    reviewRating,
                                    reviewComment,
                                    job.serviceType
                                )
                                showReviewDialog = false
                            }
                        ) {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    }
}
