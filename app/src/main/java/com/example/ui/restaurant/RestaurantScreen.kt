package com.example.ui.restaurant

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.model.MenuItem
import com.example.data.model.OrderStatus
import com.example.ui.FoodCartViewModel
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.StatCard
import com.example.ui.components.VegBadge
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreen(
    viewModel: FoodCartViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val restaurants by viewModel.restaurants.collectAsState()
    val allMenuItems by viewModel.menuItems.collectAsState()
    val allOrders by viewModel.orders.collectAsState()
    val incomingAlert by viewModel.incomingOrderAlert.collectAsState()

    val myRestaurant = restaurants.find { it.id == (user.restaurantId ?: "rest_street_rolls") }
        ?: restaurants.first()
    val myMenu = allMenuItems.filter { it.restaurantId == myRestaurant.id }
    val myOrders = allOrders.filter { it.restaurantId == myRestaurant.id }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Kitchen & Orders", "Menu Catalog (${myMenu.size})", "Daily Report")

    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Rolls") }
    var newItemIsVeg by remember { mutableStateOf(true) }
    var newItemDesc by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cart Status Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (myRestaurant.isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
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
                                        if (myRestaurant.isOpen) Color(0xFF00C853) else Color(0xFFD32F2F),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = if (myRestaurant.isOpen) "CART IS LIVE & ACCEPTING" else "CART IS CURRENTLY OFFLINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (myRestaurant.isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = myRestaurant.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${myRestaurant.address} • Rating ★ ${myRestaurant.rating}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF424242)
                        )
                    }

                    Switch(
                        checked = myRestaurant.isOpen,
                        onCheckedChange = { viewModel.toggleRestaurantOpen(myRestaurant.id, it) },
                        modifier = Modifier.testTag("cart_online_switch")
                    )
                }
            }
        }

        // Live Incoming Order Alert Box
        incomingAlert?.let { alertOrder ->
            if (alertOrder.restaurantId == myRestaurant.id && alertOrder.status == OrderStatus.PLACED) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "NEW INCOMING ORDER!",
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                                Text("Order #${alertOrder.id}", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Customer: ${alertOrder.customerName} (${alertOrder.customerPhone})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Address: ${alertOrder.customerAddress}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            alertOrder.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodySmall)
                                    Text("₹${(item.price * item.quantity).toInt()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bill Total: ₹${alertOrder.totalAmount} (${alertOrder.paymentMethod})",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.rejectIncomingOrder(alertOrder.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reject")
                                }
                                Button(
                                    onClick = { viewModel.acceptIncomingOrder(alertOrder.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Accept & Cook")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Navigation Tabs
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
                // Kitchen Orders List
                item {
                    Text(
                        text = "Active Kitchen Orders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val activeOrders = myOrders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
                if (activeOrders.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No pending orders right now", fontWeight = FontWeight.Medium)
                                Text("New orders will appear here with an alert sound.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }

                items(activeOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Order #${order.id}", fontWeight = FontWeight.Bold)
                                OrderStatusBadge(status = order.status)
                            }
                            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            order.items.forEach { item ->
                                Text("• ${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (order.status == OrderStatus.PLACED) {
                                    Button(
                                        onClick = { viewModel.acceptIncomingOrder(order.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Accept")
                                    }
                                }
                                if (order.status == OrderStatus.ACCEPTED) {
                                    Button(
                                        onClick = { viewModel.markOrderPreparing(order.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mark Preparing")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Menu Catalog Management
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dishes & Street Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        FilledTonalButton(onClick = { showAddItemDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Dish")
                        }
                    }
                }

                items(myMenu) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VegBadge(isVeg = item.isVeg)
                                Column {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Category: ${item.category} • ₹${item.price.toInt()}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Switch(
                                    checked = item.isAvailable,
                                    onCheckedChange = { viewModel.toggleMenuItem(item.id, it) }
                                )
                                Text(
                                    text = if (item.isAvailable) "IN STOCK" else "OUT OF STOCK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isAvailable) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Daily Report
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Today's Orders",
                            value = "${myRestaurant.todayOrdersCount}",
                            subtitle = "Completed",
                            icon = Icons.Default.ReceiptLong,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Today's Gross",
                            value = "₹12,450",
                            subtitle = "Avg: ₹296/order",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Top Selling Dishes Today", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("1. Double Egg Chicken Kathi Roll (24 orders)")
                            Text("2. Paneer Tikka Roll (12 orders)")
                            Text("3. Masala Thums Up (18 orders)")
                        }
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Add Dish to Food Cart") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Dish Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newItemPrice,
                        onValueChange = { newItemPrice = it },
                        label = { Text("Price (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newItemCategory,
                        onValueChange = { newItemCategory = it },
                        label = { Text("Category (Rolls, Starters, Beverages)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newItemDesc,
                        onValueChange = { newItemDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dietary Type: ${if (newItemIsVeg) "Pure Veg" else "Non-Veg"}")
                        Switch(
                            checked = newItemIsVeg,
                            onCheckedChange = { newItemIsVeg = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceVal = newItemPrice.toDoubleOrNull() ?: 100.0
                        if (newItemName.isNotBlank()) {
                            viewModel.createMenuItem(
                                MenuItem(
                                    id = "m_custom_${System.currentTimeMillis()}",
                                    restaurantId = myRestaurant.id,
                                    name = newItemName,
                                    description = newItemDesc.ifBlank { "Freshly prepared signature street cart dish." },
                                    price = priceVal,
                                    imageUrl = "https://images.unsplash.com/photo-1541544741938-0af808871cc0?w=600",
                                    isVeg = newItemIsVeg,
                                    category = newItemCategory.ifBlank { "Rolls" }
                                )
                            )
                            showAddItemDialog = false
                            newItemName = ""
                            newItemPrice = ""
                            newItemDesc = ""
                        }
                    }
                ) {
                    Text("Add Dish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
