package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.FoodCartRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FoodCartViewModel(
    val repository: FoodCartRepository = FoodCartRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val franchises = repository.franchises
    val restaurants = repository.restaurants
    val menuItems = repository.menuItems
    val deliveryBoys = repository.deliveryBoys
    val orders = repository.orders
    val cartItems = repository.cartItems
    val appliedCoupon = repository.appliedCoupon
    val reviews = repository.reviews
    val incomingOrderAlert = repository.incomingOrderAlert
    val commissionRate = repository.commissionRate
    val baseDeliveryFee = repository.baseDeliveryFee

    // Customer Navigation & Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _filterVegOnly = MutableStateFlow(false)
    val filterVegOnly: StateFlow<Boolean> = _filterVegOnly.asStateFlow()

    private val _filterRating4Plus = MutableStateFlow(false)
    val filterRating4Plus: StateFlow<Boolean> = _filterRating4Plus.asStateFlow()

    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()

    private val _activeTrackingOrder = MutableStateFlow<Order?>(null)
    val activeTrackingOrder: StateFlow<Order?> = _activeTrackingOrder.asStateFlow()

    private val _showCartSheet = MutableStateFlow(false)
    val showCartSheet: StateFlow<Boolean> = _showCartSheet.asStateFlow()

    private val _showRolePicker = MutableStateFlow(false)
    val showRolePicker: StateFlow<Boolean> = _showRolePicker.asStateFlow()

    private val _showAuthModal = MutableStateFlow(false)
    val showAuthModal: StateFlow<Boolean> = _showAuthModal.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Automatically default tracking order to the most recent active order if available
        viewModelScope.launch {
            orders.collect { orderList ->
                val active = orderList.firstOrNull { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
                if (_activeTrackingOrder.value == null && active != null) {
                    _activeTrackingOrder.value = active
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun toggleVegFilter() {
        _filterVegOnly.update { !it }
    }

    fun toggleRatingFilter() {
        _filterRating4Plus.update { !it }
    }

    fun selectRestaurant(restaurant: Restaurant?) {
        _selectedRestaurant.value = restaurant
    }

    fun openTrackingOrder(order: Order?) {
        _activeTrackingOrder.value = order
    }

    fun setShowCartSheet(show: Boolean) {
        _showCartSheet.value = show
    }

    fun setShowRolePicker(show: Boolean) {
        _showRolePicker.value = show
    }

    fun setShowAuthModal(show: Boolean) {
        _showAuthModal.value = show
    }

    fun showToast(msg: String) {
        _statusMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (_statusMessage.value == msg) {
                _statusMessage.value = null
            }
        }
    }

    // Role Switching
    fun switchRole(role: UserRole) {
        repository.switchUserRole(role)
        _showRolePicker.value = false
        showToast("Switched to ${role.displayName} panel")
    }

    fun loginWithPhone(phone: String, otp: String) {
        if (repository.loginWithPhoneOtp(phone, otp)) {
            _showAuthModal.value = false
            showToast("Welcome to Food Cart! Logged in successfully.")
        } else {
            showToast("Invalid OTP. Try '123456'.")
        }
    }

    // Customer Actions
    fun addToCart(item: MenuItem) {
        repository.addToCart(item)
        showToast("Added ${item.name} to cart")
    }

    fun removeFromCart(item: MenuItem) {
        repository.removeFromCart(item)
    }

    fun applyCoupon(coupon: Coupon) {
        val error = repository.applyCoupon(coupon)
        if (error != null) {
            showToast(error)
        } else {
            showToast("Coupon ${coupon.code} applied!")
        }
    }

    fun removeCoupon() {
        repository.applyCoupon(null)
        showToast("Coupon removed")
    }

    fun checkoutOrder(paymentMethod: String) {
        val order = repository.placeCustomerOrder(paymentMethod)
        if (order != null) {
            _showCartSheet.value = false
            _activeTrackingOrder.value = order
            showToast("Order #${order.id} placed successfully!")
        } else {
            showToast("Your cart is empty")
        }
    }

    fun submitReview(orderId: String, restaurantId: String, rating: Float, comment: String) {
        repository.addCustomerReview(orderId, restaurantId, rating, comment)
        showToast("Thank you for your rating & review!")
    }

    // Super Admin Actions
    fun approveFranchise(id: String) {
        repository.approveFranchise(id)
        showToast("Franchise approved")
    }

    fun rejectFranchise(id: String) {
        repository.rejectFranchise(id)
        showToast("Franchise rejected")
    }

    fun approveRestaurant(id: String) {
        repository.approveRestaurant(id)
        showToast("Restaurant approved for operations")
    }

    fun rejectRestaurant(id: String) {
        repository.rejectRestaurant(id)
        showToast("Restaurant rejected")
    }

    fun updateCommissionRate(rate: Double) {
        repository.updateCommissionRate(rate)
        showToast("Platform commission updated to $rate%")
    }

    fun updateBaseDeliveryFee(fee: Double) {
        repository.updateBaseDeliveryFee(fee)
        showToast("Base delivery fee updated to ₹$fee")
    }

    // Restaurant Actions
    fun toggleRestaurantOpen(id: String, isOpen: Boolean) {
        repository.toggleRestaurantStatus(id, isOpen)
        showToast(if (isOpen) "Cart is now ONLINE & accepting orders" else "Cart is now OFFLINE")
    }

    fun acceptIncomingOrder(orderId: String) {
        repository.acceptOrder(orderId)
        showToast("Order #$orderId accepted! Sent to kitchen.")
    }

    fun rejectIncomingOrder(orderId: String) {
        repository.rejectOrder(orderId)
        showToast("Order #$orderId rejected")
    }

    fun markOrderPreparing(orderId: String) {
        repository.markOrderPreparing(orderId)
        showToast("Order #$orderId is now preparing")
    }

    fun toggleMenuItem(menuItemId: String, isAvailable: Boolean) {
        repository.toggleMenuItemAvailability(menuItemId, isAvailable)
        showToast(if (isAvailable) "Item back in stock" else "Item marked out of stock")
    }

    fun createMenuItem(item: MenuItem) {
        repository.addMenuItem(item)
        showToast("New dish '${item.name}' added to menu")
    }

    // Delivery Boy Actions
    fun toggleDeliveryDuty(riderId: String, isOnline: Boolean) {
        repository.toggleDeliveryDuty(riderId, isOnline)
        showToast(if (isOnline) "You are ONLINE for deliveries" else "You are OFFLINE")
    }

    fun acceptDeliveryTask(orderId: String, riderId: String) {
        repository.acceptDeliveryTask(orderId, riderId)
        showToast("Delivery task accepted! Navigate to Food Cart.")
    }

    fun completeDelivery(orderId: String, otp: String) {
        val success = repository.markOrderDelivered(orderId, otp)
        if (success) {
            showToast("Order delivered successfully! ₹45 credited to earnings.")
        } else {
            showToast("Invalid Delivery OTP. Please verify with customer.")
        }
    }
}
