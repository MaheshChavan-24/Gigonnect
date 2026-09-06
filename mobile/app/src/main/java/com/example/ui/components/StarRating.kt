package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SahayaAmber

@Composable
fun StarRatingDisplay(
    rating: Float,
    reviewCount: Int? = null,
    starSize: Dp = 15.dp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = SahayaAmber,
            modifier = Modifier.size(starSize)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = (starSize.value * 0.9).sp,
            fontWeight = FontWeight.Bold,
            color = SahayaAmber
        )
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                fontSize = (starSize.value * 0.8).sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxStars: Int = 5,
    starSize: Dp = 32.dp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..maxStars) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "$i Stars",
                tint = if (i <= rating) SahayaAmber else Color(0xFFCBD5E1),
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRatingChanged(i) }
            )
            if (i < maxStars) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}
