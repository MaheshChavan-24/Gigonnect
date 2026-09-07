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
    isHindi: Boolean = false
) {
    var showPayoutDialog by remember { mutableStateOf(false) }
    var payoutAmountStr by remember { mutableStateOf(if ((user?.walletBalance ?: 0.0) > 0) (user?.walletBalance?.toInt() ?: 0).toString() else "") }
    var bankName by remember { mutableStateOf(user?.bankName?.ifEmpty { "State Bank of India" } ?: "State Bank of India") }
    var accountNumber by remember { mutableStateOf(user?.accountNumber ?: "") }
    var ifscCode by remember { mutableStateOf(user?.ifscCode ?: "") }

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
                    text = "₹${(user?.walletBalance ?: 0.0).toInt()}",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { showPayoutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("wallet_request_payout_button")
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "बैंक खाते में निकासी अनुरोध करें" else "Request Instant Payout",
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
                        text = user?.bankName ?: "HDFC Bank Ltd.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "A/C: •••• ${user?.accountNumber?.takeLast(4) ?: "1992"} • IFSC: ${user?.ifscCode ?: "HDFC0001234"}",
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

        if ((user?.walletBalance ?: 0.0) > 0) {
            TransactionItem(
                title = if (isHindi) "एस्क्रो राशि जमा" else "Escrow Balance Credited",
                subtitle = if (isHindi) "क्लाइंट द्वारा कार्य पूर्णता पर स्वीकृत" else "Released on job completion by Client",
                amount = "+₹${(user?.walletBalance ?: 0.0).toInt()}",
                isCredit = true,
                status = "Available"
            )
        } else {
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
                        text = if (isHindi) "अभी कोई लेनदेन नहीं है" else "No Recent Transactions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "जैसे ही आप कार्य पूरा करेंगे, एस्क्रो से आपकी राशि यहाँ जुड़ेगी।" else "Escrow payouts will appear here upon client job approval.",
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
                        text = if (isHindi) "उपलब्ध शेष: ₹${(user?.walletBalance ?: 0.0).toInt()}" else "Available balance: ₹${(user?.walletBalance ?: 0.0).toInt()}",
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
                                val amount = payoutAmountStr.toDoubleOrNull() ?: 500.0
                                onRequestPayout(amount, bankName, accountNumber, ifscCode)
                                showPayoutDialog = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
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
