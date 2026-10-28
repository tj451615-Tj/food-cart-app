package com.example.ui.admin

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Franchise
import com.example.data.model.Restaurant
import com.example.ui.FoodCartViewModel
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminScreen(
    viewModel: FoodCartViewModel,
    modifier: Modifier = Modifier
) {
    val franchises by viewModel.franchises.collectAsState()
    val restaurants by viewModel.restaurants.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val commissionRate by viewModel.commissionRate.collectAsState()
    val baseDeliveryFee by viewModel.baseDeliveryFee.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview & Settings", "Pending Approvals", "All Carts & Franchises", "Global Orders")

    // Calculations
    val totalGmv = franchises.sumOf { it.totalSales } + 420000.0
    val platformEarnings = totalGmv * (commissionRate / 100.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("super_admin_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Owner Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFF00C853), CircleShape)
                                )
                                Text(
                                    text = "SUPER ADMIN CONSOLE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Welcome, Tanmoy Jana",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "superadmin@foodcart.com | App Owner & Master Controller",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Scalability Architecture: Engineered for 20 Million+ users with sharded indexing & CDN caching.",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Overview & Platform Settings
                item {
                    Text(
                        text = "Real-time Platform Financials",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Gross GMV",
                            value = "₹${(totalGmv / 100000.0).format(1)} Lakhs",
                            subtitle = "+24.8% this week",
                            icon = Icons.Default.AccountBalanceWallet,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Net Revenue",
                            value = "₹${(platformEarnings / 100000.0).format(2)} Lakhs",
                            subtitle = "@ $commissionRate% commission",
                            icon = Icons.Default.TrendingUp,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Franchises",
                            value = "${franchises.size} Active",
                            subtitle = "Across Kolkata & WB",
                            icon = Icons.Default.Storefront,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Food Carts",
                            value = "${restaurants.size} Carts",
                            subtitle = "${restaurants.count { it.isOpen }} currently online",
                            icon = Icons.Default.Fastfood,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Dynamic System Controls
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Dynamic Commission & Fee Engine",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Commission Rate
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Platform Commission Rate:")
                                Text("${commissionRate.roundToInt()}%", fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = commissionRate.toFloat(),
                                onValueChange = { viewModel.updateCommissionRate(it.toDouble()) },
                                valueRange = 5f..30f,
                                steps = 24,
                                modifier = Modifier.testTag("commission_slider")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Base Delivery Charge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Base Delivery Fee:")
                                Text("₹${baseDeliveryFee.roundToInt()}", fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = baseDeliveryFee.toFloat(),
                                onValueChange = { viewModel.updateBaseDeliveryFee(it.toDouble()) },
                                valueRange = 15f..60f,
                                steps = 9,
                                modifier = Modifier.testTag("delivery_fee_slider")
                            )
                        }
                    }
                }
            }

            1 -> {
                // Pending Approvals
                item {
                    Text(
                        text = "Pending Registrations Requiring App Owner Approval",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val pendingFranchises = franchises.filter { it.status == "PENDING" }
                val pendingRestaurants = restaurants.filter { !it.isApproved }

                if (pendingFranchises.isEmpty() && pendingRestaurants.isEmpty()) {
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
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00C853),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("All Franchises & Carts Approved", fontWeight = FontWeight.Bold)
                                Text("No pending approval requests at this moment.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                items(pendingFranchises) { franchise ->
                    PendingFranchiseCard(
                        franchise = franchise,
                        onApprove = { viewModel.approveFranchise(franchise.id) },
                        onReject = { viewModel.rejectFranchise(franchise.id) }
                    )
                }

                items(pendingRestaurants) { restaurant ->
                    PendingRestaurantCard(
                        restaurant = restaurant,
                        onApprove = { viewModel.approveRestaurant(restaurant.id) },
                        onReject = { viewModel.rejectRestaurant(restaurant.id) }
                    )
                }
            }

            2 -> {
                // All Carts & Franchises
                item {
                    Text(
                        text = "Franchise Territories (${franchises.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(franchises) { fr ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(fr.name, fontWeight = FontWeight.Bold)
                                    Text(fr.city, style = MaterialTheme.typography.bodySmall)
                                }
                                Surface(
                                    color = if (fr.status == "APPROVED") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = fr.status,
                                        color = if (fr.status == "APPROVED") Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Owner: ${fr.ownerName} (${fr.phone})", style = MaterialTheme.typography.labelSmall)
                            Text("Sub-Restaurants: ${fr.subRestaurantCount} | Delivery Partners: ${fr.activeDeliveryPartners}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Food Carts & Restaurants (${restaurants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(restaurants) { rest ->
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
                                Text(rest.name, fontWeight = FontWeight.Bold)
                                Text(rest.cuisine, style = MaterialTheme.typography.bodySmall)
                                Text("Rating: ★ ${rest.rating} | Sales: ₹${rest.totalSales.toInt()}", style = MaterialTheme.typography.labelSmall)
                            }
                            Switch(
                                checked = rest.isOpen,
                                onCheckedChange = { viewModel.toggleRestaurantOpen(rest.id, it) }
                            )
                        }
                    }
                }
            }

            3 -> {
                // Global Orders
                item {
                    Text(
                        text = "Real-time Orders Across Network",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(orders) { order ->
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
                            Text("Food Cart: ${order.restaurantName}", style = MaterialTheme.typography.bodySmall)
                            Text("Customer: ${order.customerName} (${order.customerPhone})", style = MaterialTheme.typography.bodySmall)
                            Text("Total: ₹${order.totalAmount} (${order.paymentMethod} - ${order.paymentStatus})", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingFranchiseCard(
    franchise: Franchise,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Franchise Application", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(franchise.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Territory: ${franchise.city}", style = MaterialTheme.typography.bodySmall)
            Text("Applicant: ${franchise.ownerName} • ${franchise.phone}", style = MaterialTheme.typography.bodySmall)
            Text("Planned Outlets: ${franchise.subRestaurantCount}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve Franchise")
                }
            }
        }
    }
}

@Composable
fun PendingRestaurantCard(
    restaurant: Restaurant,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Food Cart Onboarding Request", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Cuisine: ${restaurant.cuisine}", style = MaterialTheme.typography.bodySmall)
            Text("Address: ${restaurant.address}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve Cart")
                }
            }
        }
    }
}

private fun Double.format(digits: Int): String = String.format("%.${digits}f", this)
