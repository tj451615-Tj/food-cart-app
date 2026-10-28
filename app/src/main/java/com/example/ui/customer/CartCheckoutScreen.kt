package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.Coupon
import com.example.data.model.MenuItem
import com.example.ui.FoodCartViewModel
import com.example.ui.components.VegBadge
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.VegGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: FoodCartViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val availableCoupons = viewModel.repository.availableCoupons

    var selectedPaymentMethod by remember { mutableStateOf("UPI") } // UPI, RAZORPAY, COD

    val subtotal = viewModel.repository.calculateItemTotal()
    val deliveryFee = viewModel.repository.calculateDeliveryFee()
    val tax = viewModel.repository.calculateTax()
    val discount = viewModel.repository.calculateDiscount()
    val grandTotal = viewModel.repository.calculateGrandTotal()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("cart_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Cart Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Food Cart",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your cart is empty", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Add delicious items from nearby food carts!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Order Items
                    items(cartItems.entries.toList()) { (item, qty) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                VegBadge(isVeg = item.isVeg)
                                Column {
                                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("₹${item.price.toInt()} each", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                }
                                Text("$qty", fontWeight = FontWeight.Bold)
                                IconButton(
                                    onClick = { viewModel.addToCart(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("₹${(item.price * qty).toInt()}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Coupons Section
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Apply Coupons & Offers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    if (appliedCoupon != null) {
                                        Text(
                                            text = "Remove",
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.clickable { viewModel.removeCoupon() }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                availableCoupons.forEach { coupon ->
                                    val isApplied = appliedCoupon?.code == coupon.code
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(coupon.code, fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                                            Text(coupon.description, style = MaterialTheme.typography.labelSmall)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                if (!isApplied) viewModel.applyCoupon(coupon) else viewModel.removeCoupon()
                                            },
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(if (isApplied) "APPLIED" else "APPLY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Payment Method Options
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Payment Gateway", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                PaymentOptionRow(
                                    title = "UPI (Google Pay, PhonePe, Paytm)",
                                    subtitle = "Instant payment via UPI deep link",
                                    icon = Icons.Default.Payment,
                                    isSelected = selectedPaymentMethod == "UPI",
                                    onSelect = { selectedPaymentMethod = "UPI" }
                                )
                                PaymentOptionRow(
                                    title = "Razorpay (Test Mode Integration)",
                                    subtitle = "Credit/Debit Cards, NetBanking, Wallets",
                                    icon = Icons.Default.CreditCard,
                                    isSelected = selectedPaymentMethod == "RAZORPAY",
                                    onSelect = { selectedPaymentMethod = "RAZORPAY" }
                                )
                                PaymentOptionRow(
                                    title = "Cash on Delivery (COD)",
                                    subtitle = "Pay cash or scan QR when delivered",
                                    icon = Icons.Default.Money,
                                    isSelected = selectedPaymentMethod == "COD",
                                    onSelect = { selectedPaymentMethod = "COD" }
                                )
                            }
                        }
                    }

                    // Bill Breakdown
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Bill Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                BillRow(label = "Item Total", value = "₹${subtotal.toInt()}")
                                BillRow(label = "Delivery Partner Fee", value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}")
                                BillRow(label = "Govt Taxes & Restaurant GST (5%)", value = "₹${tax.format(1)}")
                                if (discount > 0) {
                                    BillRow(
                                        label = "Coupon Discount (${appliedCoupon?.code})",
                                        value = "-₹${discount.toInt()}",
                                        isDiscount = true
                                    )
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("To Pay", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                    Text("₹${grandTotal.format(1)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandPrimary)
                                }
                            }
                        }
                    }
                }

                // Place Order Button
                Button(
                    onClick = { viewModel.checkoutOrder(selectedPaymentMethod) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("place_order_button")
                ) {
                    Text(
                        text = "Pay ₹${grandTotal.format(1)} & Place Order",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onSelect)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(icon, contentDescription = null, tint = if (isSelected) BrandPrimary else Color.Gray)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun BillRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isDiscount) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun Double.format(digits: Int): String = String.format("%.${digits}f", this)
