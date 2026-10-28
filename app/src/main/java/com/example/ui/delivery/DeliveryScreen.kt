package com.example.ui.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.FoodCartViewModel
import com.example.ui.components.LiveMapCanvasView
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryScreen(
    viewModel: FoodCartViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val deliveryBoys by viewModel.deliveryBoys.collectAsState()
    val orders by viewModel.orders.collectAsState()

    val myRider = deliveryBoys.find { it.id == (user.deliveryBoyId ?: "db_bikram") }
        ?: deliveryBoys.first()

    // Find active order assigned to rider or available to be picked up
    val activeTask = orders.find { it.id == myRider.currentOrderId }
        ?: orders.find { it.deliveryBoyId == myRider.id && it.status != OrderStatus.DELIVERED }
    val unassignedReadyOrders = orders.filter {
        it.status == OrderStatus.PREPARING || it.status == OrderStatus.ACCEPTED
    }

    var showOtpDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active Route & Tasks", "Earnings & History")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("delivery_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Rider Status Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (myRider.isOnline) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (myRider.isOnline) Color(0xFF00C853) else Color.Gray,
                                        CircleShape
                                    )
                            )
                            Text(
                                text = if (myRider.isOnline) "ONLINE FOR DELIVERIES" else "DUTY OFF",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (myRider.isOnline) Color(0xFF0D47A1) else Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = myRider.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${myRider.vehicleNumber} • Rating ★ ${myRider.rating}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = myRider.isOnline,
                        onCheckedChange = { viewModel.toggleDeliveryDuty(myRider.id, it) },
                        modifier = Modifier.testTag("duty_switch")
                    )
                }
            }
        }

        // Stats summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Today's Payout",
                    value = "₹${myRider.todayEarnings.toInt()}",
                    subtitle = "14 trips + incentives",
                    icon = Icons.Default.CurrencyRupee,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lifetime Trips",
                    value = "${myRider.totalDeliveries}",
                    subtitle = "99.4% on-time",
                    icon = Icons.Default.TwoWheeler,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab Row
        item {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Active Task
                if (activeTask != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Active Delivery Task", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    OrderStatusBadge(status = activeTask.status)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Order #${activeTask.id} • Earnings: ₹45.00", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFFF5200), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pickup: ${activeTask.restaurantName}", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE23744), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Drop: ${activeTask.customerAddress}", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Customer: ${activeTask.customerName} (${activeTask.customerPhone})", style = MaterialTheme.typography.labelSmall)

                                Spacer(modifier = Modifier.height(14.dp))
                                LiveMapCanvasView(
                                    riderProgressFraction = 0.70f,
                                    showRider = true
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { showOtpDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("complete_delivery_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Complete Delivery (Verify OTP)")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Moped, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No active delivery task assigned", fontWeight = FontWeight.Bold)
                                Text("Available orders ready for pickup are listed below.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    if (unassignedReadyOrders.isNotEmpty()) {
                        item {
                            Text("Orders Ready for Delivery Partner Acceptance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        items(unassignedReadyOrders) { order ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Order #${order.id} • ${order.restaurantName}", fontWeight = FontWeight.Bold)
                                        Text("To: ${order.customerAddress}", style = MaterialTheme.typography.bodySmall)
                                        Text("Trip Fare: ₹45.00", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                    Button(onClick = { viewModel.acceptDeliveryTask(order.id, myRider.id) }) {
                                        Text("Accept")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Trip History
                item {
                    Text("Today's Completed Trips", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                val completed = orders.filter { it.status == OrderStatus.DELIVERED }
                items(completed) { ord ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Order #${ord.id}", fontWeight = FontWeight.Bold)
                                Text("${ord.restaurantName} ➔ ${ord.customerName}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("+₹45.00", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showOtpDialog && activeTask != null) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Customer Delivery OTP") },
            text = {
                Column {
                    Text("Ask customer for the 4-digit delivery verification code.")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Hint for Demo: The OTP is '${activeTask.otp}'", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("4-digit OTP") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.completeDelivery(activeTask.id, enteredOtp)
                        showOtpDialog = false
                        enteredOtp = ""
                    }
                ) {
                    Text("Verify & Deliver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
