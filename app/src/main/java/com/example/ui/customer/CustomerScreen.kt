package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Coupon
import com.example.data.model.OrderStatus
import com.example.data.model.Restaurant
import com.example.ui.FoodCartViewModel
import com.example.ui.components.RatingBadge
import com.example.ui.components.VegBadge
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: FoodCartViewModel,
    modifier: Modifier = Modifier
) {
    val restaurants by viewModel.restaurants.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filterVegOnly by viewModel.filterVegOnly.collectAsState()
    val filterRating4Plus by viewModel.filterRating4Plus.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val orders by viewModel.orders.collectAsState()

    val totalCartCount = cartItems.values.sum()
    val activeOrder = orders.firstOrNull { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }

    val categories = listOf("All", "Biryani", "Rolls", "Burgers", "Bowls", "Starters", "Beverages", "Desserts")

    val filteredRestaurants = restaurants.filter { r ->
        val matchesSearch = searchQuery.isBlank() ||
                r.name.contains(searchQuery, ignoreCase = true) ||
                r.cuisine.contains(searchQuery, ignoreCase = true)
        val matchesRating = !filterRating4Plus || r.rating >= 4.5
        val matchesCategory = selectedCategory == "All" || r.cuisine.contains(selectedCategory, ignoreCase = true)
        matchesSearch && matchesRating && matchesCategory
    }

    Box(modifier = modifier.fillMaxSize().testTag("customer_home_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Address & User Switcher
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandPrimary)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Salt Lake Sector V", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                                Text("Tech Park & City Centre, Kolkata", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }

                        // Role Switch button
                        FilledTonalButton(
                            onClick = { viewModel.setShowRolePicker(true) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("switch_role_top_button")
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Roles", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search 'Biryani', 'Rolls', 'Burgers' or Food Carts...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandPrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_food_input")
                    )
                }
            }

            // Active Order Floating Alert
            if (activeOrder != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { viewModel.openTrackingOrder(activeOrder) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(BrandPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Moped, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("Order #${activeOrder.id} is ${activeOrder.status.title}!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Tap to track live route on map ➔", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            // Promo Banner Carousel
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            PromoCard(
                                title = "FLAT 50% OFF",
                                subtitle = "Code: WELCOME50 | Up to ₹100",
                                tag = "FOOD CART SPECIAL",
                                brush = Brush.horizontalGradient(listOf(Color(0xFFE23744), Color(0xFFFF6F00)))
                            )
                        }
                        item {
                            PromoCard(
                                title = "FREE DELIVERY",
                                subtitle = "Code: FREEDEL on all orders > ₹149",
                                tag = "POPULAR CARTS",
                                brush = Brush.horizontalGradient(listOf(Color(0xFF1E88E5), Color(0xFF00ACC1)))
                            )
                        }
                        item {
                            PromoCard(
                                title = "FLAT ₹100 OFF",
                                subtitle = "Code: FOODCART100 on orders > ₹299",
                                tag = "WEEKEND BINGE",
                                brush = Brush.horizontalGradient(listOf(Color(0xFF7B1FA2), Color(0xFFE91E63)))
                            )
                        }
                    }
                }
            }

            // Categories
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Explore Street Delicacies", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedCategory(category) },
                                label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }

            // Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterVegOnly,
                        onClick = { viewModel.toggleVegFilter() },
                        label = { Text("Pure Veg") },
                        leadingIcon = { VegBadge(isVeg = true) }
                    )
                    FilterChip(
                        selected = filterRating4Plus,
                        onClick = { viewModel.toggleRatingFilter() },
                        label = { Text("Ratings 4.5+") },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // Restaurant Count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Food Carts Nearby (${filteredRestaurants.size})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Restaurants List
            items(filteredRestaurants) { rest ->
                RestaurantCard(
                    restaurant = rest,
                    onClick = { viewModel.selectRestaurant(rest) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Floating Cart Summary Bar
        if (totalCartCount > 0) {
            Surface(
                color = BrandPrimary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clickable { viewModel.setShowCartSheet(true) }
                    .testTag("floating_cart_bottom_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$totalCartCount", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("₹${viewModel.repository.calculateGrandTotal().toInt()} Total", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Tap to review cart & pay", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("View Cart", color = Color.White, fontWeight = FontWeight.ExtraBold)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PromoCard(
    title: String,
    subtitle: String,
    tag: String,
    brush: Brush
) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .height(115.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = tag,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Column {
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("restaurant_card_${restaurant.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Delivery Time Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Text(
                            text = "${restaurant.deliveryTimeMinutes} mins • ${restaurant.distanceKm} km",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!restaurant.isOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CURRENTLY CLOSED",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    RatingBadge(rating = restaurant.rating, reviewCount = restaurant.reviewCount)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "50% OFF up to ₹100 | Use WELCOME50",
                        color = BrandPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
