package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.admin.SuperAdminScreen
import com.example.ui.auth.AuthModalDialog
import com.example.ui.auth.RolePickerSheet
import com.example.ui.customer.CartCheckoutScreen
import com.example.ui.customer.CustomerScreen
import com.example.ui.customer.LiveOrderTrackingScreen
import com.example.ui.customer.RestaurantDetailScreen
import com.example.ui.delivery.DeliveryScreen
import com.example.ui.franchise.FranchiseScreen
import com.example.ui.restaurant.RestaurantScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCartApp(
    viewModel: FoodCartViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsState()
    val activeTrackingOrder by viewModel.activeTrackingOrder.collectAsState()
    val showCartSheet by viewModel.showCartSheet.collectAsState()
    val showRolePicker by viewModel.showRolePicker.collectAsState()
    val showAuthModal by viewModel.showAuthModal.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalCartCount = cartItems.values.sum()

    // Handle back button when viewing restaurant details or order tracking in Customer mode
    if (selectedRestaurant != null) {
        BackHandler {
            viewModel.selectRestaurant(null)
        }
    } else if (activeTrackingOrder != null && currentUser.role == UserRole.CUSTOMER) {
        BackHandler {
            viewModel.openTrackingOrder(null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = BrandPrimary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "FOOD CART",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = currentUser.role.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Quick Role Switch Button
                    TextButton(
                        onClick = { viewModel.setShowRolePicker(true) },
                        modifier = Modifier.testTag("switch_role_action_button")
                    ) {
                        Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Switch Role", fontWeight = FontWeight.Bold, color = BrandPrimary)
                    }

                    if (currentUser.role == UserRole.CUSTOMER) {
                        IconButton(onClick = { viewModel.setShowCartSheet(true) }) {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge { Text("$totalCartCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = {
            statusMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White
                ) {
                    Text(msg, fontWeight = FontWeight.Medium)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentUser.role) {
                UserRole.SUPER_ADMIN -> {
                    SuperAdminScreen(viewModel = viewModel)
                }

                UserRole.FRANCHISE_OWNER -> {
                    FranchiseScreen(viewModel = viewModel)
                }

                UserRole.RESTAURANT_OWNER -> {
                    RestaurantScreen(viewModel = viewModel)
                }

                UserRole.DELIVERY_BOY -> {
                    DeliveryScreen(viewModel = viewModel)
                }

                UserRole.CUSTOMER -> {
                    when {
                        selectedRestaurant != null -> {
                            RestaurantDetailScreen(
                                restaurant = selectedRestaurant!!,
                                viewModel = viewModel,
                                onBack = { viewModel.selectRestaurant(null) }
                            )
                        }

                        activeTrackingOrder != null -> {
                            LiveOrderTrackingScreen(
                                order = activeTrackingOrder!!,
                                viewModel = viewModel,
                                onBack = { viewModel.openTrackingOrder(null) }
                            )
                        }

                        else -> {
                            CustomerScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    // Role Picker Modal
    if (showRolePicker) {
        RolePickerSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowRolePicker(false) }
        )
    }

    // Cart and Checkout Bottom Sheet
    if (showCartSheet) {
        CartCheckoutScreen(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowCartSheet(false) }
        )
    }

    // Auth Modal
    if (showAuthModal) {
        AuthModalDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowAuthModal(false) }
        )
    }
}
