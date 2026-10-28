package com.example.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val franchiseId: String? = null,
    val restaurantId: String? = null,
    val deliveryBoyId: String? = null,
    val avatarUrl: String = "",
)

data class Franchise(
    val id: String,
    val name: String,
    val city: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val status: String = "APPROVED", // PENDING, APPROVED, REJECTED
    val commissionRate: Double = 12.0, // Percentage
    val totalSales: Double = 428000.0,
    val subRestaurantCount: Int = 18,
    val activeDeliveryPartners: Int = 24
)

data class Restaurant(
    val id: String,
    val franchiseId: String,
    val name: String,
    val cuisine: String,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeMinutes: Int,
    val minOrder: Double,
    val address: String,
    val imageUrl: String,
    val isOpen: Boolean = true,
    val isApproved: Boolean = true,
    val totalSales: Double = 84500.0,
    val todayOrdersCount: Int = 34,
    val lat: Double = 22.5726,
    val lng: Double = 88.3639,
    val distanceKm: Double = 1.8
)

data class MenuItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isVeg: Boolean,
    val isAvailable: Boolean = true,
    val category: String, // Starters, Rolls, Bowls, Burgers, Beverages, Desserts
    val rating: Double = 4.5,
    val isBestseller: Boolean = false
)

enum class OrderStatus(val title: String, val stepIndex: Int) {
    PLACED("Order Placed", 0),
    ACCEPTED("Order Accepted", 1),
    PREPARING("Kitchen Preparing", 2),
    OUT_FOR_DELIVERY("Out for Delivery", 3),
    DELIVERED("Delivered", 4),
    CANCELLED("Cancelled", -1)
}

data class OrderItem(
    val menuItemId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val isVeg: Boolean
)

data class Order(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<OrderItem>,
    val itemTotal: Double,
    val deliveryFee: Double,
    val taxAmount: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val couponCode: String? = null,
    val status: OrderStatus = OrderStatus.PLACED,
    val paymentMethod: String = "UPI", // UPI, COD, RAZORPAY
    val paymentStatus: String = "PAID", // PAID, PENDING
    val deliveryBoyId: String? = null,
    val deliveryBoyName: String? = null,
    val deliveryBoyPhone: String? = null,
    val estimatedMinutes: Int = 25,
    val otp: String = "4829",
    val createdAt: Long = System.currentTimeMillis()
)

data class DeliveryBoy(
    val id: String,
    val franchiseId: String,
    val name: String,
    val phone: String,
    val vehicleNumber: String,
    val isOnline: Boolean = true,
    val currentOrderId: String? = null,
    val totalDeliveries: Int = 342,
    val todayEarnings: Double = 1250.0,
    val rating: Double = 4.8,
    val lat: Double = 22.5750,
    val lng: Double = 88.3680
)

data class Coupon(
    val code: String,
    val discountPercent: Double,
    val maxDiscount: Double,
    val minOrder: Double,
    val description: String
)

data class Review(
    val id: String,
    val orderId: String,
    val restaurantId: String,
    val customerName: String,
    val rating: Float,
    val comment: String,
    val date: String
)
