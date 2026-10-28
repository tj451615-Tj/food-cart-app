package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun VegBadge(isVeg: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (isVeg) VegGreen else NonVegRed
    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(borderColor, if (isVeg) CircleShape else RoundedCornerShape(1.dp))
        )
    }
}

@Composable
fun RatingBadge(rating: Double, reviewCount: Int? = null, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF247F38),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = String.format("%.1f", rating),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            if (reviewCount != null) {
                Text(
                    text = "($reviewCount)",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PLACED -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.ACCEPTED -> Color(0xFFE3F2FD) to Color(0xFF0D47A1)
        OrderStatus.PREPARING -> Color(0xFFEDE7F6) to Color(0xFF4A148C)
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        OrderStatus.DELIVERED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status.title,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun LiveMapCanvasView(
    modifier: Modifier = Modifier,
    riderProgressFraction: Float = 0.65f,
    showRider: Boolean = true
) {
    // Pulse animation for destination marker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEBF1F5))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Simulated Roads
            val roadColor = Color(0xFFFFFFFF)
            val roadBorder = Color(0xFFD0D7DE)

            // Street Grid
            drawLine(roadBorder, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 24f)
            drawLine(roadColor, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 20f)

            drawLine(roadBorder, Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 28f)
            drawLine(roadColor, Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 24f)

            drawLine(roadBorder, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 20f)
            drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 16f)

            drawLine(roadBorder, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 22f)
            drawLine(roadColor, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 18f)

            // Delivery Route Path (Restaurant -> Corner -> Customer)
            val pStart = Offset(w * 0.25f, h * 0.35f)
            val pCorner = Offset(w * 0.75f, h * 0.35f)
            val pEnd = Offset(w * 0.75f, h * 0.75f)

            // Draw full dashed route
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
            drawLine(
                color = Color(0xFFFF5200),
                start = pStart,
                end = pCorner,
                strokeWidth = 6f,
                pathEffect = dashEffect
            )
            drawLine(
                color = Color(0xFFFF5200),
                start = pCorner,
                end = pEnd,
                strokeWidth = 6f,
                pathEffect = dashEffect
            )

            // Restaurant Marker
            drawCircle(Color(0xFFFF9800), radius = 14f, center = pStart)
            drawCircle(Color.White, radius = 6f, center = pStart)

            // Customer Destination Marker (with animated pulse)
            drawCircle(
                color = Color(0x33E23744),
                radius = 16f * pulseScale,
                center = pEnd
            )
            drawCircle(Color(0xFFE23744), radius = 14f, center = pEnd)
            drawCircle(Color.White, radius = 6f, center = pEnd)

            // Rider position along the path
            if (showRider) {
                val riderPos = if (riderProgressFraction <= 0.5f) {
                    val subFrac = riderProgressFraction / 0.5f
                    Offset(
                        pStart.x + (pCorner.x - pStart.x) * subFrac,
                        pStart.y
                    )
                } else {
                    val subFrac = (riderProgressFraction - 0.5f) / 0.5f
                    Offset(
                        pCorner.x,
                        pCorner.y + (pEnd.y - pCorner.y) * subFrac
                    )
                }

                // Rider beacon
                drawCircle(Color(0xFF007AFF), radius = 16f, center = riderPos)
                drawCircle(Color.White, radius = 8f, center = riderPos)
                drawCircle(Color(0xFF007AFF), radius = 4f, center = riderPos)
            }
        }

        // Overlay Map Badges
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF00C853), CircleShape)
                    )
                    Text(
                        text = "GPS Live Tracking",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}
