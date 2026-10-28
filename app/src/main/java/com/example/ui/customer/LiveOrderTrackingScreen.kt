package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.FoodCartViewModel
import com.example.ui.components.LiveMapCanvasView
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveOrderTrackingScreen(
    order: Order,
    viewModel: FoodCartViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReviewDialog by remember { mutableStateOf(false) }
    var ratingVal by remember { mutableFloatStateOf(5f) }
    var reviewComment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Order Tracking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    OrderStatusBadge(status = order.status, modifier = Modifier.padding(end = 12.dp))
                }
            )
        },
        modifier = modifier.testTag("live_tracking_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (order.status == OrderStatus.DELIVERED) "Order Delivered!" else "Arriving in ~${order.estimatedMinutes} mins",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "From ${order.restaurantName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("DELIVERY OTP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text(order.otp, fontSize = 16.sp, fontWeight = FontWeight.Black, color = BrandPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Live Canvas Map
            item {
                Text("Live Delivery Route", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LiveMapCanvasView(
                    riderProgressFraction = when (order.status) {
                        OrderStatus.PLACED -> 0.1f
                        OrderStatus.ACCEPTED -> 0.25f
                        OrderStatus.PREPARING -> 0.45f
                        OrderStatus.OUT_FOR_DELIVERY -> 0.75f
                        OrderStatus.DELIVERED -> 1.0f
                        OrderStatus.CANCELLED -> 0.0f
                    },
                    showRider = order.status == OrderStatus.OUT_FOR_DELIVERY
                )
            }

            // Step Progress Timeline
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Progress Timeline", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val steps = listOf(
                            OrderStatus.PLACED to "Order placed & confirmed",
                            OrderStatus.ACCEPTED to "Restaurant accepted order",
                            OrderStatus.PREPARING to "Kitchen is preparing your food",
                            OrderStatus.OUT_FOR_DELIVERY to "Delivery partner is on the way",
                            OrderStatus.DELIVERED to "Delivered at your door"
                        )

                        steps.forEachIndexed { index, (stepStatus, description) ->
                            val isCompleted = order.status.stepIndex >= stepStatus.stepIndex
                            val isCurrent = order.status == stepStatus

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(
                                            if (isCompleted) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stepStatus.title,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Delivery Partner Info Card
            if (order.deliveryBoyName != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Column {
                                    Text(order.deliveryBoyName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Delivery Partner • Rating ★ 4.9", style = MaterialTheme.typography.bodySmall)
                                    Text(order.deliveryBoyPhone ?: "+91 98333 44556", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalIconButton(
                                    onClick = { viewModel.showToast("Calling delivery partner...") }
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call")
                                }
                                FilledTonalIconButton(
                                    onClick = { viewModel.showToast("Opening chat with rider...") }
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chat")
                                }
                            }
                        }
                    }
                }
            }

            // Items in this order
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order #${order.id} Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        order.items.forEach { itm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${itm.quantity}x ${itm.name}", style = MaterialTheme.typography.bodySmall)
                                Text("₹${(itm.price * itm.quantity).toInt()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Paid (${order.paymentMethod})", fontWeight = FontWeight.Bold)
                            Text("₹${order.totalAmount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Rating & Review CTA when delivered
            if (order.status == OrderStatus.DELIVERED) {
                item {
                    Button(
                        onClick = { showReviewDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rate_order_button")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rate & Review Your Food Cart Experience")
                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Rate ${order.restaurantName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("How was the taste, packaging, and delivery?")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { ratingVal = star.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= ratingVal) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Write your review (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReview(order.id, order.restaurantId, ratingVal, reviewComment)
                        showReviewDialog = false
                    }
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Skip")
                }
            }
        )
    }
}
