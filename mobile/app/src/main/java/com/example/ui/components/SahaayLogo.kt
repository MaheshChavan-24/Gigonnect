package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun SahaayLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shapeRadius: Dp = 12.dp
) {
    Image(
        painter = painterResource(id = R.drawable.sahaay_logo),
        contentDescription = "Sahaay Logo",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(shapeRadius))
    )
}

