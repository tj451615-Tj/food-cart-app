package com.example.ui.franchise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.FoodCartViewModel
import com.example.ui.components.RatingBadge
import com.example.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranchiseScreen(
    viewModel: FoodCartViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val franchises by viewModel.franchises.collectAsState()
    val restaurants by viewModel.restaurants.collectAsState()
    val deliveryBoys by viewModel.deliveryBoys.collectAsState()

    val myFranchise = franchises.find { it.id == (user.franchiseId ?: "fr_kolkata_central") }
        ?: franchises.first()
    val subRestaurants = restaurants.filter { it.franchiseId == myFranchise.id }
    val myRiders = deliveryBoys.filter { it.franchiseId == myFranchise.id }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Outlets (${subRestaurants.size})", "Delivery Team (${myRiders.size})", "Financials")

    var showAddRiderDialog by remember { mutableStateOf(false) }
    var newRiderName by remember { mutableStateOf("") }
    var newRiderPhone by remember { mutableStateOf("") }
    var newRiderVehicle by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("franchise_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Franchise Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "FRANCHISE HUB PANEL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = myFranchise.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Territory: ${myFranchise.city} • Manager: ${myFranchise.ownerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
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
                    title = "Franchise GMV",
                    value = "₹${(myFranchise.totalSales / 100000.0).format(2)} Lakhs",
                    subtitle = "This Month",
                    icon = Icons.Default.CurrencyRupee,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Franchise Profit",
                    value = "₹${((myFranchise.totalSales * 0.12) / 1000.0).format(1)}k",
                    subtitle = "@ 12% territory margin",
                    icon = Icons.Default.MonetizationOn,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tabs
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
                // Outlets Management
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Managed Food Carts & Outlets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${subRestaurants.count { it.isOpen }} Online",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(subRestaurants) { rest ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(rest.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(rest.cuisine, style = MaterialTheme.typography.bodySmall)
                                    Text(rest.address, style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Switch(
                                        checked = rest.isOpen,
                                        onCheckedChange = { viewModel.toggleRestaurantOpen(rest.id, it) }
                                    )
                                    Text(
                                        text = if (rest.isOpen) "ACCEPTING" else "CLOSED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rest.isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Rating: ★ ${rest.rating}", style = MaterialTheme.typography.labelSmall)
                                Text("Today's Orders: ${rest.todayOrdersCount}", style = MaterialTheme.typography.labelSmall)
                                Text("Sales: ₹${rest.totalSales.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Delivery Team
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dedicated Delivery Riders",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        FilledTonalButton(
                            onClick = { showAddRiderDialog = true }
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Rider")
                        }
                    }
                }

                items(myRiders) { rider ->
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
                            Column {
                                Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(rider.phone, style = MaterialTheme.typography.bodySmall)
                                Text("Vehicle: ${rider.vehicleNumber}", style = MaterialTheme.typography.labelSmall)
                                Text("Total Deliveries: ${rider.totalDeliveries} | Rating: ★ ${rider.rating}", style = MaterialTheme.typography.labelSmall)
                            }
                            Surface(
                                color = if (rider.isOnline) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (rider.isOnline) "ON DUTY" else "OFFLINE",
                                    color = if (rider.isOnline) Color(0xFF2E7D32) else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Financials
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Monthly Settlement Summary", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Gross Cart Sales:")
                                Text("₹${myFranchise.totalSales.toInt()}", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Franchise Share (12%):")
                                Text("₹${(myFranchise.totalSales * 0.12).toInt()}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("App Owner Commission (15%):")
                                Text("₹${(myFranchise.totalSales * 0.15).toInt()}", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddRiderDialog) {
        AlertDialog(
            onDismissRequest = { showAddRiderDialog = false },
            title = { Text("Add Franchise Delivery Partner") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newRiderName,
                        onValueChange = { newRiderName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRiderPhone,
                        onValueChange = { newRiderPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRiderVehicle,
                        onValueChange = { newRiderVehicle = it },
                        label = { Text("Vehicle Registration & Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRiderName.isNotBlank() && newRiderPhone.isNotBlank()) {
                            viewModel.showToast("Rider $newRiderName registered under ${myFranchise.name}!")
                            showAddRiderDialog = false
                            newRiderName = ""
                            newRiderPhone = ""
                            newRiderVehicle = ""
                        }
                    }
                ) {
                    Text("Register Rider")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRiderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun Double.format(digits: Int): String = String.format("%.${digits}f", this)
