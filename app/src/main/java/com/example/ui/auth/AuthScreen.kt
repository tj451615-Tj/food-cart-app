package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.UserRole
import com.example.ui.FoodCartViewModel
import com.example.ui.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolePickerSheet(
    viewModel: FoodCartViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("role_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FOOD CART Multi-Role Console",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Switch between the 5 dedicated panels instantly",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            UserRole.values().forEach { role ->
                val isSelected = currentUser.role == role
                val icon = when (role) {
                    UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
                    UserRole.FRANCHISE_OWNER -> Icons.Default.Hub
                    UserRole.RESTAURANT_OWNER -> Icons.Default.SoupKitchen
                    UserRole.DELIVERY_BOY -> Icons.Default.Moped
                    UserRole.CUSTOMER -> Icons.Default.Person
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.switchRole(role) }
                        .testTag("select_role_${role.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSelected) BrandPrimary else Color.Gray.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column {
                                Text(
                                    text = role.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = role.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        if (isSelected) {
                            Surface(
                                color = BrandPrimary,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    onDismiss()
                    viewModel.setShowAuthModal(true)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Login with Phone OTP / Super Admin Credentials")
            }
        }
    }
}

@Composable
fun AuthModalDialog(
    viewModel: FoodCartViewModel,
    onDismiss: () -> Unit
) {
    var isPhoneMode by remember { mutableStateOf(true) }
    var phoneInput by remember { mutableStateOf("+91 98765 43210") }
    var otpInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("superadmin@foodcart.com") }
    var passwordInput by remember { mutableStateOf("admin123") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isPhoneMode) "Customer Phone Sign-in" else "Super Admin / Staff Login", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = isPhoneMode,
                        onClick = { isPhoneMode = true },
                        label = { Text("Phone OTP") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = !isPhoneMode,
                        onClick = { isPhoneMode = false },
                        label = { Text("Email Login") }
                    )
                }

                if (isPhoneMode) {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("6-digit OTP (e.g. 123456)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Demo verification OTP: Use '123456'", color = BrandPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email (e.g. superadmin@foodcart.com)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isPhoneMode) {
                        viewModel.loginWithPhone(phoneInput, otpInput)
                    } else {
                        if (emailInput.contains("superadmin", ignoreCase = true)) {
                            viewModel.switchRole(UserRole.SUPER_ADMIN)
                            onDismiss()
                        } else {
                            viewModel.switchRole(UserRole.RESTAURANT_OWNER)
                            onDismiss()
                        }
                    }
                }
            ) {
                Text(if (isPhoneMode) "Verify & Login" else "Sign In")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
