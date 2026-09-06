package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SahayaSuccess
import com.example.ui.viewmodel.RazorpayPaymentState

@Composable
fun RazorpayPaymentDialog(
    state: RazorpayPaymentState,
    onPayClicked: () -> Unit,
    onDismiss: () -> Unit,
    isHindi: Boolean = false
) {
    if (!state.isOpen) return

    var selectedMethod by remember { mutableStateOf("UPI") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Razorpay Branded Top Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C2340))
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF0C72EC), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "R",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Razorpay Escrow",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Order: ${state.orderId}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    // Purpose & Amount
                    Text(
                        text = state.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "जमा की जाने वाली राशि" else "Escrow Deposit Amount",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "₹${state.amount.toInt()}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0C2340)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isHindi) "भुगतान का माध्यम चुनें:" else "Select Payment Method:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Payment Methods Selection
                    PaymentMethodOption(
                        title = "UPI (Google Pay, PhonePe, Paytm)",
                        subtitle = "Instant escrow deposit via UPI app",
                        icon = Icons.Default.QrCode,
                        isSelected = selectedMethod == "UPI",
                        onClick = { selectedMethod = "UPI" }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    PaymentMethodOption(
                        title = "Debit / Credit Card",
                        subtitle = "Visa, Mastercard, RuPay",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedMethod == "CARD",
                        onClick = { selectedMethod = "CARD" }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    PaymentMethodOption(
                        title = "Net Banking",
                        subtitle = "HDFC, SBI, ICICI, Axis & 50+ banks",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedMethod == "NETBANKING",
                        onClick = { selectedMethod = "NETBANKING" }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Escrow Trust Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECFDF5), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Safe Escrow",
                            tint = SahayaSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) {
                                "एस्क्रो सुरक्षा: धन तब तक सुरक्षित रहेगा जब तक आप कार्य की संतुष्टि के बाद इसे जारी नहीं करते।"
                            } else {
                                "100% Escrow Guarantee: Money is held securely and only released when you approve the completed job."
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF065F46),
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Pay Button with state
                    Button(
                        onClick = onPayClicked,
                        enabled = !state.isVerifying,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C72EC)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("razorpay_pay_button")
                    ) {
                        if (state.isVerifying) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "सत्यापन जारी..." else "Verifying Signature...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (state.isSuccess) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "एस्क्रो जमा सफल!" else "Escrow Secured!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = if (isHindi) "₹${state.amount.toInt()} सुरक्षित जमा करें" else "Pay & Fund Escrow (₹${state.amount.toInt()})",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF0C72EC) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                if (isSelected) Color(0xFFF0F7FF) else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Color(0xFF0C72EC) else Color.Gray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) Color(0xFF0C72EC) else Color.LightGray,
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C72EC), CircleShape)
                )
            }
        }
    }
}
