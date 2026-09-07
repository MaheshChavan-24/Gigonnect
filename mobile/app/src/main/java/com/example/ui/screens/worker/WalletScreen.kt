package com.example.ui.screens.worker

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.User
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoDarkTile
import com.example.ui.theme.BentoDarkTileMuted
import com.example.ui.theme.BentoOutline
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.SahayaSuccess

@Composable
fun WalletScreen(
    user: User?,
    onRequestPayout: (amount: Double, bankName: String, account: String, ifsc: String) -> Unit,
    onRefresh: () -> Unit = {},
    isHindi: Boolean = false
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    val balance = user?.walletBalance ?: 0.0
    val hasBalance = balance > 0.0

    var showPayoutDialog by remember { mutableStateOf(false) }
    var payoutAmountStr by remember(balance) {
        mutableStateOf(if (hasBalance) balance.toInt().toString() else "")
    }
    var bankName by remember(user?.bankName) {
        mutableStateOf(user?.bankName?.ifEmpty { "State Bank of India" } ?: "State Bank of India")
    }
    var accountNumber by remember(user?.accountNumber) {
        mutableStateOf(user?.accountNumber ?: "")
    }
    var ifscCode by remember(user?.ifscCode) {
        mutableStateOf(user?.ifscCode ?: "")
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .verticalScroll(scrollState)
            .padding(18.dp)
    ) {
        Text(
            text = if (isHindi) "सहाय वॉलेट एवं निकासी" else "Sahaya Wallet & Payouts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextPrimary
        )
        Text(
            text = if (isHindi) "माइलस्टोन एस्क्रो से सीधे बैंक खाते में भुगतान" else "Milestone escrow protection with automated NEFT/IMPS transfers",
            fontSize = 12.sp,
            color = BentoTextSecondary,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bento Hero Balance Dark Tile
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = BentoDarkTile),
            border = BorderStroke(1.dp, Color(0xFF333333)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wallet_balance_card")
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "उपलब्ध शेष राशि" else "Available Balance",
                        color = BentoDarkTileMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Bento Escrow Safe Pill
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SahayaSuccess,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ESCROW SECURED",
                            fontSize = 9.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "₹${balance.toInt()}",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (hasBalance) {
                            showPayoutDialog = true
                        }
                    },
                    enabled = hasBalance,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF2E2E2E),
                        disabledContentColor = Color(0xFF888888)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("wallet_request_payout_button")
                ) {
                    Icon(
                        imageVector = if (hasBalance) Icons.Default.ArrowDownward else Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasBalance) {
                            if (isHindi) "बैंक खाते में निकासी अनुरोध करें (₹${balance.toInt()})" else "Request Instant Payout (₹${balance.toInt()})"
                        } else {
                            if (isHindi) "निकासी के लिए कोई शेष राशि नहीं है" else "No Balance to Withdraw"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Linked Bank Account Bento Tile
        Text(
            text = if (isHindi) "जुड़ा हुआ बैंक खाता" else "Linked Payout Account",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            border = BorderStroke(1.dp, BentoOutline.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(BentoPrimaryContainer, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = user?.bankName?.ifEmpty { "State Bank of India" } ?: "State Bank of India",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = if (!user?.accountNumber.isNullOrBlank()) {
                            "A/C: •••• ${user?.accountNumber?.takeLast(4)} • IFSC: ${user?.ifscCode ?: "SBIN0001234"}"
                        } else {
                            "Account not linked yet • Will link on payout"
                        },
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Transaction History / Escrow Releases
        Text(
            text = if (isHindi) "लेनदेन एवं एस्क्रो भुगतान" else "Transactions & Payouts",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (hasBalance) {
            TransactionItem(
                title = if (isHindi) "एस्क्रो राशि जमा" else "Escrow Balance Credited",
                subtitle = if (isHindi) "क्लाइंट द्वारा कार्य पूर्णता पर स्वीकृत • निकासी के लिए तैयार" else "Released on job completion • Ready for instant payout",
                amount = "+₹${balance.toInt()}",
                isCredit = true,
                status = "Available"
            )
        } else {
            if (!user?.accountNumber.isNullOrBlank()) {
                TransactionItem(
                    title = if (isHindi) "बैंक खाते में निकासी पूर्ण" else "Instant Payout Transferred",
                    subtitle = if (isHindi) "बैंक खाता •••• ${user?.accountNumber?.takeLast(4)} में अंतरित" else "Transferred to Bank A/C •••• ${user?.accountNumber?.takeLast(4)}",
                    amount = "Transferred",
                    isCredit = false,
                    status = "Completed"
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = BorderStroke(1.dp, BentoOutline.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "सभी भुगतान संसाधित हो चुके हैं" else "All Payouts Settled",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "जैसे ही आप नया कार्य पूरा करेंगे, एस्क्रो से आपकी राशि यहाँ जुड़ेगी।" else "New escrow payouts will appear here upon client job completion.",
                        fontSize = 12.sp,
                        color = BentoTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    // Payout Request Dialog
    if (showPayoutDialog) {
        val enteredAmount = payoutAmountStr.toDoubleOrNull() ?: 0.0
        val isAmountValid = enteredAmount > 0.0 && enteredAmount <= balance
        val isDetailsValid = accountNumber.isNotBlank() && ifscCode.isNotBlank()
        val canSubmit = isAmountValid && isDetailsValid

        Dialog(onDismissRequest = { showPayoutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BentoSurface,
                border = BorderStroke(1.dp, BentoOutline)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = if (isHindi) "बैंक निकासी अनुरोध" else "Request Wallet Payout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi) "उपलब्ध शेष: ₹${balance.toInt()}" else "Available balance: ₹${balance.toInt()}",
                        fontSize = 13.sp,
                        color = BentoPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = payoutAmountStr,
                        onValueChange = { payoutAmountStr = it },
                        label = { Text("Payout Amount (₹)") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (enteredAmount > balance) {
                        Text(
                            text = if (isHindi) "अनुरोधित राशि उपलब्ध शेष से अधिक है" else "Amount exceeds available balance",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = { Text("Bank Account Number") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ifscCode,
                        onValueChange = { ifscCode = it },
                        label = { Text("IFSC Code") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPayoutDialog = false }) {
                            Text("Cancel", color = BentoTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (canSubmit) {
                                    onRequestPayout(enteredAmount, bankName, accountNumber, ifscCode)
                                    showPayoutDialog = false
                                }
                            },
                            enabled = canSubmit,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoPrimary,
                                disabledContainerColor = Color(0xFF3A3A3A),
                                disabledContentColor = Color(0xFF888888)
                            ),
                            modifier = Modifier.testTag("confirm_payout_button")
                        ) {
                            Text("Confirm Payout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    title: String,
    subtitle: String,
    amount: String,
    isCredit: Boolean,
    status: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = BorderStroke(1.dp, BentoOutline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (isCredit) Color(0xFFDCFCE7) else BentoSurfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isCredit) SahayaSuccess else BentoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = BentoTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCredit) SahayaSuccess else BentoTextPrimary
                )
                Text(
                    text = status,
                    fontSize = 10.sp,
                    color = BentoTextSecondary
                )
            }
        }
    }
}
