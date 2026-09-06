package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.vector.ImageVector

object TradeIcons {
    fun getIconForCategory(categoryKey: String): ImageVector {
        val lower = categoryKey.lowercase()
        return when {
            lower.contains("plumb") -> Icons.Default.Build
            lower.contains("carpenter") || lower.contains("carpent") -> Icons.Default.Handyman
            lower.contains("electr") -> Icons.Default.Bolt
            lower.contains("paint") -> Icons.Default.Brush
            lower.contains("clean") -> Icons.Default.CleaningServices
            lower.contains("appliance") -> Icons.Default.HomeRepairService
            lower.contains("garden") -> Icons.Default.Yard
            lower.contains("pest") -> Icons.Default.PestControl
            lower.contains("mason") || lower.contains("tile") -> Icons.Default.Construction
            lower.contains("hvac") || lower.contains("ac") -> Icons.Default.AcUnit
            lower.contains("mov") -> Icons.Default.LocalShipping
            lower.contains("weld") -> Icons.Default.Construction
            lower.contains("interior") -> Icons.Default.Architecture
            lower.contains("cctv") || lower.contains("secur") -> Icons.Default.Videocam
            lower.contains("computer") || lower.contains("it") -> Icons.Default.Computer
            else -> Icons.Default.Handyman
        }
    }
}
