package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FoodCartRepository {

    // Pre-configured role accounts
    val superAdminUser = User(
        id = "usr_superadmin",
        name = "Tanmoy Jana",
        email = "superadmin@foodcart.com",
        phone = "+91 98765 43210",
        role = UserRole.SUPER_ADMIN
    )

    val franchiseUser = User(
        id = "usr_franchise_1",
        name = "Rajesh Sharma",
        email = "rajesh.franchise@foodcart.com",
        phone = "+91 98111 22334",
        role = UserRole.FRANCHISE_OWNER,
        franchiseId = "fr_kolkata_central"
    )

    val restaurantUser = User(
        id = "usr_rest_1",
        name = "Chef Amitav Roy",
        email = "amitav.rollcart@foodcart.com",
        phone = "+91 98222 33445",
        role = UserRole.RESTAURANT_OWNER,
        franchiseId = "fr_kolkata_central",
        restaurantId = "rest_street_rolls"
    )

    val deliveryUser = User(
        id = "usr_del_1",
        name = "Bikram Das",
        email = "bikram.rider@foodcart.com",
        phone = "+91 98333 44556",
        role = UserRole.DELIVERY_BOY,
        deliveryBoyId = "db_bikram"
    )

    val customerUser = User(
        id = "usr_cust_1",
        name = "Sourav Ganguly",
        email = "sourav.customer@foodcart.com",
        phone = "+91 98444 55667",
        role = UserRole.CUSTOMER
    )

    // Current logged in user
    private val _currentUser = MutableStateFlow<User>(customerUser)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Global System Settings (Managed by Super Admin)
    private val _commissionRate = MutableStateFlow(15.0) // 15%
    val commissionRate: StateFlow<Double> = _commissionRate.asStateFlow()

    private val _baseDeliveryFee = MutableStateFlow(35.0)
    val baseDeliveryFee: StateFlow<Double> = _baseDeliveryFee.asStateFlow()

    // Franchises
    private val _franchises = MutableStateFlow<List<Franchise>>(
        listOf(
            Franchise(
                id = "fr_kolkata_central",
                name = "Kolkata Central Food Cart Hub",
                city = "Kolkata, Salt Lake & Park Circus",
                ownerName = "Rajesh Sharma",
                email = "rajesh.franchise@foodcart.com",
                phone = "+91 98111 22334",
                status = "APPROVED",
                commissionRate = 12.0,
                totalSales = 684000.0,
                subRestaurantCount = 14,
                activeDeliveryPartners = 18
            ),
            Franchise(
                id = "fr_howrah_express",
                name = "Howrah & Station Food Franchise",
                city = "Howrah Station Hub",
                ownerName = "Priyanka Sen",
                email = "priyanka.howrah@foodcart.com",
                phone = "+91 98321 99887",
                status = "APPROVED",
                commissionRate = 12.0,
                totalSales = 412000.0,
                subRestaurantCount = 9,
                activeDeliveryPartners = 12
            ),
            Franchise(
                id = "fr_newtown_tech",
                name = "New Town Silicon Food Zone",
                city = "New Town Action Area I & II",
                ownerName = "Subhash Bose",
                email = "subhash.newtown@foodcart.com",
                phone = "+91 98555 12345",
                status = "PENDING",
                commissionRate = 10.0,
                totalSales = 0.0,
                subRestaurantCount = 6,
                activeDeliveryPartners = 8
            )
        )
    )
    val franchises: StateFlow<List<Franchise>> = _franchises.asStateFlow()

    // Restaurants
    private val _restaurants = MutableStateFlow<List<Restaurant>>(
        listOf(
            Restaurant(
                id = "rest_street_rolls",
                franchiseId = "fr_kolkata_central",
                name = "Kolkata Roll & Kebab Cart",
                cuisine = "Kolkata Kathi Rolls, Mughlai, Kebabs",
                rating = 4.6,
                reviewCount = 840,
                deliveryTimeMinutes = 20,
                minOrder = 99.0,
                address = "Near City Centre 1, Salt Lake, Kolkata",
                imageUrl = "https://images.unsplash.com/photo-1541544741938-0af808871cc0?w=600",
                isOpen = true,
                isApproved = true,
                totalSales = 124500.0,
                todayOrdersCount = 42,
                lat = 22.5855,
                lng = 88.4095,
                distanceKm = 1.2
            ),
            Restaurant(
                id = "rest_biryani_express",
                franchiseId = "fr_kolkata_central",
                name = "Royal Awadh Dum Biryani Cart",
                cuisine = "Dum Biryani, Chaap, Phirni",
                rating = 4.8,
                reviewCount = 1420,
                deliveryTimeMinutes = 25,
                minOrder = 149.0,
                address = "Sector V, Tech Park Gate, Kolkata",
                imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600",
                isOpen = true,
                isApproved = true,
                totalSales = 248000.0,
                todayOrdersCount = 68,
                lat = 22.5726,
                lng = 88.4312,
                distanceKm = 2.4
            ),
            Restaurant(
                id = "rest_desi_burger",
                franchiseId = "fr_kolkata_central",
                name = "The Crunch Street Burger Cart",
                cuisine = "Artisan Burgers, Peri-Peri Fries, Shakes",
                rating = 4.4,
                reviewCount = 520,
                deliveryTimeMinutes = 18,
                minOrder = 120.0,
                address = "CF Block, Salt Lake Sector 1, Kolkata",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600",
                isOpen = true,
                isApproved = true,
                totalSales = 94000.0,
                todayOrdersCount = 31,
                lat = 22.5890,
                lng = 88.4120,
                distanceKm = 1.5
            ),
            Restaurant(
                id = "rest_momo_wok",
                franchiseId = "fr_howrah_express",
                name = "Tibet Express Dim Sum & Wok",
                cuisine = "Steam Momos, Pan Fried Momos, Thukpa",
                rating = 4.5,
                reviewCount = 610,
                deliveryTimeMinutes = 22,
                minOrder = 99.0,
                address = "Golabari, Howrah Station Approach",
                imageUrl = "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600",
                isOpen = true,
                isApproved = true,
                totalSales = 81200.0,
                todayOrdersCount = 28,
                lat = 22.5930,
                lng = 88.3450,
                distanceKm = 3.6
            ),
            Restaurant(
                id = "rest_chaat_bazaar",
                franchiseId = "fr_newtown_tech",
                name = "Chaat & Puchka Corner (Pure Veg)",
                cuisine = "Dahi Puchka, Papdi Chaat, Raj Kachori",
                rating = 4.7,
                reviewCount = 390,
                deliveryTimeMinutes = 15,
                minOrder = 79.0,
                address = "Major Arterial Road, New Town, Kolkata",
                imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600",
                isOpen = false, // Currently paused
                isApproved = false, // Pending Super Admin approval
                totalSales = 12000.0,
                todayOrdersCount = 0,
                lat = 22.5700,
                lng = 88.4600,
                distanceKm = 4.2
            )
        )
    )
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    // Menu Items
    private val _menuItems = MutableStateFlow<List<MenuItem>>(
        listOf(
            // Kolkata Roll & Kebab Cart
            MenuItem(
                id = "m_roll_1",
                restaurantId = "rest_street_rolls",
                name = "Double Egg Chicken Kathi Roll",
                description = "Crispy lachha paratha stuffed with spicy tandoori chicken, fried egg, crunchy onions & signature tangy sauce.",
                price = 120.0,
                imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=600",
                isVeg = false,
                category = "Rolls",
                rating = 4.8,
                isBestseller = true
            ),
            MenuItem(
                id = "m_roll_2",
                restaurantId = "rest_street_rolls",
                name = "Paneer Tikka Roll",
                description = "Charcoal roasted spiced paneer cubes rolled with mint chutney and fresh capsicum.",
                price = 105.0,
                imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=600",
                isVeg = true,
                category = "Rolls",
                rating = 4.6,
                isBestseller = true
            ),
            MenuItem(
                id = "m_roll_3",
                restaurantId = "rest_street_rolls",
                name = "Reshmi Chicken Kebab (6 Pcs)",
                description = "Melt-in-mouth chicken chunks marinated in cashew cream and mild spices, grilled to perfection.",
                price = 180.0,
                imageUrl = "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=600",
                isVeg = false,
                category = "Starters",
                rating = 4.9
            ),
            MenuItem(
                id = "m_roll_4",
                restaurantId = "rest_street_rolls",
                name = "Masala Thums Up (300ml)",
                description = "Chilled soda spiked with roasted jeera, black salt, and freshly squeezed lemon.",
                price = 45.0,
                imageUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=600",
                isVeg = true,
                category = "Beverages",
                rating = 4.5
            ),

            // Biryani Express
            MenuItem(
                id = "m_biryani_1",
                restaurantId = "rest_biryani_express",
                name = "Kolkata Special Chicken Dum Biryani",
                description = "Fragrant basmati rice dum cooked with saffron, tender chicken piece, signature potato & boiled egg.",
                price = 240.0,
                imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600",
                isVeg = false,
                category = "Bowls",
                rating = 4.9,
                isBestseller = true
            ),
            MenuItem(
                id = "m_biryani_2",
                restaurantId = "rest_biryani_express",
                name = "Shahi Mutton Biryani (2 Pcs)",
                description = "Slow-cooked succulent mutton in aromatic long-grain rice with royal spices and rich aroma.",
                price = 340.0,
                imageUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=600",
                isVeg = false,
                category = "Bowls",
                rating = 4.9,
                isBestseller = true
            ),
            MenuItem(
                id = "m_biryani_3",
                restaurantId = "rest_biryani_express",
                name = "Chicken Chaap Gravy",
                description = "Thick, slow-braised poppy seed, cashew and brown onion gravy with whole tender chicken leg.",
                price = 190.0,
                imageUrl = "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=600",
                isVeg = false,
                category = "Starters",
                rating = 4.7
            ),
            MenuItem(
                id = "m_biryani_4",
                restaurantId = "rest_biryani_express",
                name = "Kolkata Zafrani Phirni",
                description = "Traditional slow cooked ground rice pudding infused with saffron and pistachios in earthen matka.",
                price = 70.0,
                imageUrl = "https://images.unsplash.com/photo-1541832676-9b763b0239ab?w=600",
                isVeg = true,
                category = "Desserts",
                rating = 4.8
            ),

            // The Crunch Street Burger Cart
            MenuItem(
                id = "m_burger_1",
                restaurantId = "rest_desi_burger",
                name = "Crispy Fried Chicken Loaded Burger",
                description = "Golden double-dredged chicken patty, spicy secret mayo, cheddar melt & jalapeños on brioche bun.",
                price = 169.0,
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600",
                isVeg = false,
                category = "Burgers",
                rating = 4.7,
                isBestseller = true
            ),
            MenuItem(
                id = "m_burger_2",
                restaurantId = "rest_desi_burger",
                name = "Aloo Tikki Double Cheese Burger",
                description = "Herb spiced crunchy potato patty, tomato slice, iceberg lettuce, caramelized onions & double cheese.",
                price = 119.0,
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=600",
                isVeg = true,
                category = "Burgers",
                rating = 4.5
            ),
            MenuItem(
                id = "m_burger_3",
                restaurantId = "rest_desi_burger",
                name = "Spicy Peri-Peri Crinkle Fries",
                description = "Crispy crinkle cut fries tossed with fiery peri-peri seasoning and served with cheesy dip.",
                price = 99.0,
                imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=600",
                isVeg = true,
                category = "Starters",
                rating = 4.6
            )
        )
    )
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    // Delivery Boys
    private val _deliveryBoys = MutableStateFlow<List<DeliveryBoy>>(
        listOf(
            DeliveryBoy(
                id = "db_bikram",
                franchiseId = "fr_kolkata_central",
                name = "Bikram Das",
                phone = "+91 98333 44556",
                vehicleNumber = "WB 02 AJ 4412 (Electric Bike)",
                isOnline = true,
                currentOrderId = "ord_101",
                totalDeliveries = 412,
                todayEarnings = 1420.0,
                rating = 4.9,
                lat = 22.5800,
                lng = 88.4150
            ),
            DeliveryBoy(
                id = "db_rohit",
                franchiseId = "fr_kolkata_central",
                name = "Rohit Verma",
                phone = "+91 98777 66554",
                vehicleNumber = "WB 06 B 8890",
                isOnline = true,
                currentOrderId = null,
                totalDeliveries = 289,
                todayEarnings = 980.0,
                rating = 4.7,
                lat = 22.5730,
                lng = 88.4320
            ),
            DeliveryBoy(
                id = "db_mantu",
                franchiseId = "fr_howrah_express",
                name = "Mantu Mondal",
                phone = "+91 98123 77665",
                vehicleNumber = "WB 12 K 1109",
                isOnline = false,
                currentOrderId = null,
                totalDeliveries = 175,
                todayEarnings = 450.0,
                rating = 4.6,
                lat = 22.5920,
                lng = 88.3440
            )
        )
    )
    val deliveryBoys: StateFlow<List<DeliveryBoy>> = _deliveryBoys.asStateFlow()

    // Orders
    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = "ord_101",
                customerId = "usr_cust_1",
                customerName = "Sourav Ganguly",
                customerPhone = "+91 98444 55667",
                customerAddress = "Flat 4B, Silver Heights, Salt Lake Sector 2, Kolkata",
                restaurantId = "rest_street_rolls",
                restaurantName = "Kolkata Roll & Kebab Cart",
                items = listOf(
                    OrderItem("m_roll_1", "Double Egg Chicken Kathi Roll", 120.0, 2, false),
                    OrderItem("m_roll_4", "Masala Thums Up (300ml)", 45.0, 2, true)
                ),
                itemTotal = 330.0,
                deliveryFee = 35.0,
                taxAmount = 16.5,
                discountAmount = 50.0,
                totalAmount = 331.5,
                couponCode = "WELCOME50",
                status = OrderStatus.OUT_FOR_DELIVERY,
                paymentMethod = "UPI",
                paymentStatus = "PAID",
                deliveryBoyId = "db_bikram",
                deliveryBoyName = "Bikram Das",
                deliveryBoyPhone = "+91 98333 44556",
                estimatedMinutes = 12,
                otp = "4829",
                createdAt = System.currentTimeMillis() - 15 * 60 * 1000
            ),
            Order(
                id = "ord_100",
                customerId = "usr_cust_1",
                customerName = "Sourav Ganguly",
                customerPhone = "+91 98444 55667",
                customerAddress = "Flat 4B, Silver Heights, Salt Lake Sector 2, Kolkata",
                restaurantId = "rest_biryani_express",
                restaurantName = "Royal Awadh Dum Biryani Cart",
                items = listOf(
                    OrderItem("m_biryani_1", "Kolkata Special Chicken Dum Biryani", 240.0, 1, false),
                    OrderItem("m_biryani_4", "Kolkata Zafrani Phirni", 70.0, 1, true)
                ),
                itemTotal = 310.0,
                deliveryFee = 35.0,
                taxAmount = 15.5,
                discountAmount = 0.0,
                totalAmount = 360.5,
                status = OrderStatus.DELIVERED,
                paymentMethod = "COD",
                paymentStatus = "PAID",
                deliveryBoyId = "db_bikram",
                deliveryBoyName = "Bikram Das",
                deliveryBoyPhone = "+91 98333 44556",
                estimatedMinutes = 0,
                otp = "1298",
                createdAt = System.currentTimeMillis() - 120 * 60 * 1000
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Active Customer Cart
    private val _cartItems = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    val cartItems: StateFlow<Map<MenuItem, Int>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    val availableCoupons = listOf(
        Coupon("WELCOME50", 50.0, 100.0, 199.0, "50% off up to ₹100 on first order"),
        Coupon("FOODCART100", 25.0, 100.0, 299.0, "₹100 flat off on orders above ₹299"),
        Coupon("FREEDEL", 100.0, 35.0, 149.0, "Zero delivery fee on popular carts")
    )

    // Customer reviews
    private val _reviews = MutableStateFlow<List<Review>>(
        listOf(
            Review("rev_1", "ord_100", "rest_biryani_express", "Sourav Ganguly", 5f, "Aroma was incredible! The Kolkata potato in biryani was soft and melt in mouth.", "Yesterday"),
            Review("rev_2", "ord_99", "rest_street_rolls", "Debolina K.", 4.5f, "Hot crispy rolls delivered in under 15 minutes! Will order again.", "2 days ago")
        )
    )
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Incoming restaurant order audio/banner alert flag
    private val _incomingOrderAlert = MutableStateFlow<Order?>(null)
    val incomingOrderAlert: StateFlow<Order?> = _incomingOrderAlert.asStateFlow()

    // Authentication & Role Switcher
    fun switchUserRole(role: UserRole) {
        val targetUser = when (role) {
            UserRole.SUPER_ADMIN -> superAdminUser
            UserRole.FRANCHISE_OWNER -> franchiseUser
            UserRole.RESTAURANT_OWNER -> restaurantUser
            UserRole.DELIVERY_BOY -> deliveryUser
            UserRole.CUSTOMER -> customerUser
        }
        _currentUser.value = targetUser
    }

    fun loginWithPhoneOtp(phone: String, otp: String): Boolean {
        if (otp.length == 6 || otp == "123456" || otp == "4829") {
            _currentUser.value = customerUser.copy(phone = phone)
            return true
        }
        return false
    }

    // Super Admin Actions
    fun approveFranchise(franchiseId: String) {
        _franchises.update { list ->
            list.map { if (it.id == franchiseId) it.copy(status = "APPROVED") else it }
        }
    }

    fun rejectFranchise(franchiseId: String) {
        _franchises.update { list ->
            list.map { if (it.id == franchiseId) it.copy(status = "REJECTED") else it }
        }
    }

    fun approveRestaurant(restaurantId: String) {
        _restaurants.update { list ->
            list.map { if (it.id == restaurantId) it.copy(isApproved = true, isOpen = true) else it }
        }
    }

    fun rejectRestaurant(restaurantId: String) {
        _restaurants.update { list ->
            list.map { if (it.id == restaurantId) it.copy(isApproved = false, isOpen = false) else it }
        }
    }

    fun updateCommissionRate(newRate: Double) {
        _commissionRate.value = newRate
    }

    fun updateBaseDeliveryFee(newFee: Double) {
        _baseDeliveryFee.value = newFee
    }

    // Restaurant Actions
    fun toggleRestaurantStatus(restaurantId: String, isOpen: Boolean) {
        _restaurants.update { list ->
            list.map { if (it.id == restaurantId) it.copy(isOpen = isOpen) else it }
        }
    }

    fun acceptOrder(orderId: String) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(
                    status = OrderStatus.ACCEPTED,
                    estimatedMinutes = 20
                ) else it
            }
        }
        _incomingOrderAlert.value = null
    }

    fun rejectOrder(orderId: String) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(
                    status = OrderStatus.CANCELLED
                ) else it
            }
        }
        _incomingOrderAlert.value = null
    }

    fun markOrderPreparing(orderId: String) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(status = OrderStatus.PREPARING) else it
            }
        }
    }

    fun addMenuItem(item: MenuItem) {
        _menuItems.update { it + item }
    }

    fun toggleMenuItemAvailability(menuItemId: String, isAvailable: Boolean) {
        _menuItems.update { list ->
            list.map { if (it.id == menuItemId) it.copy(isAvailable = isAvailable) else it }
        }
    }

    // Delivery Partner Actions
    fun toggleDeliveryDuty(riderId: String, isOnline: Boolean) {
        _deliveryBoys.update { list ->
            list.map { if (it.id == riderId) it.copy(isOnline = isOnline) else it }
        }
    }

    fun acceptDeliveryTask(orderId: String, riderId: String) {
        val rider = _deliveryBoys.value.find { it.id == riderId } ?: return
        _deliveryBoys.update { list ->
            list.map { if (it.id == riderId) it.copy(currentOrderId = orderId) else it }
        }
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(
                    status = OrderStatus.OUT_FOR_DELIVERY,
                    deliveryBoyId = rider.id,
                    deliveryBoyName = rider.name,
                    deliveryBoyPhone = rider.phone
                ) else it
            }
        }
    }

    fun markOrderDelivered(orderId: String, enteredOtp: String): Boolean {
        val order = _orders.value.find { it.id == orderId } ?: return false
        if (enteredOtp == order.otp || enteredOtp == "1234") {
            _orders.update { list ->
                list.map {
                    if (it.id == orderId) it.copy(
                        status = OrderStatus.DELIVERED,
                        paymentStatus = "PAID",
                        estimatedMinutes = 0
                    ) else it
                }
            }
            // Update rider earnings
            order.deliveryBoyId?.let { riderId ->
                _deliveryBoys.update { list ->
                    list.map {
                        if (it.id == riderId) it.copy(
                            currentOrderId = null,
                            totalDeliveries = it.totalDeliveries + 1,
                            todayEarnings = it.todayEarnings + 45.0
                        ) else it
                    }
                }
            }
            return true
        }
        return false
    }

    // Customer Cart & Checkout Actions
    fun addToCart(item: MenuItem) {
        _cartItems.update { current ->
            val updated = current.toMutableMap()
            updated[item] = (updated[item] ?: 0) + 1
            updated
        }
    }

    fun removeFromCart(item: MenuItem) {
        _cartItems.update { current ->
            val updated = current.toMutableMap()
            val count = updated[item] ?: 0
            if (count > 1) {
                updated[item] = count - 1
            } else {
                updated.remove(item)
            }
            updated
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        _appliedCoupon.value = null
    }

    fun applyCoupon(coupon: Coupon?): String? {
        if (coupon == null) {
            _appliedCoupon.value = null
            return null
        }
        val subtotal = calculateItemTotal()
        if (subtotal < coupon.minOrder) {
            return "Minimum order of ₹${coupon.minOrder.toInt()} required for this coupon"
        }
        _appliedCoupon.value = coupon
        return null
    }

    fun calculateItemTotal(): Double {
        return _cartItems.value.entries.sumOf { it.key.price * it.value }
    }

    fun calculateDeliveryFee(): Double {
        val coupon = _appliedCoupon.value
        return if (coupon?.code == "FREEDEL") 0.0 else _baseDeliveryFee.value
    }

    fun calculateDiscount(): Double {
        val coupon = _appliedCoupon.value ?: return 0.0
        if (coupon.code == "FREEDEL") return 0.0
        val subtotal = calculateItemTotal()
        val calculated = (subtotal * (coupon.discountPercent / 100.0))
        return minOf(calculated, coupon.maxDiscount)
    }

    fun calculateTax(): Double {
        return calculateItemTotal() * 0.05 // 5% GST
    }

    fun calculateGrandTotal(): Double {
        val total = calculateItemTotal() + calculateDeliveryFee() + calculateTax() - calculateDiscount()
        return maxOf(0.0, total)
    }

    fun placeCustomerOrder(
        paymentMethod: String,
        address: String = "Flat 4B, Silver Heights, Salt Lake Sector 2, Kolkata"
    ): Order? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val firstItem = items.keys.first()
        val restaurant = _restaurants.value.find { it.id == firstItem.restaurantId }
            ?: _restaurants.value.first()

        val orderItems = items.map { (menuItem, qty) ->
            OrderItem(
                menuItemId = menuItem.id,
                name = menuItem.name,
                price = menuItem.price,
                quantity = qty,
                isVeg = menuItem.isVeg
            )
        }

        val orderId = "ord_${System.currentTimeMillis() % 100000}"
        val newOrder = Order(
            id = orderId,
            customerId = _currentUser.value.id,
            customerName = _currentUser.value.name,
            customerPhone = _currentUser.value.phone,
            customerAddress = address,
            restaurantId = restaurant.id,
            restaurantName = restaurant.name,
            items = orderItems,
            itemTotal = calculateItemTotal(),
            deliveryFee = calculateDeliveryFee(),
            taxAmount = calculateTax(),
            discountAmount = calculateDiscount(),
            totalAmount = calculateGrandTotal(),
            couponCode = _appliedCoupon.value?.code,
            status = OrderStatus.PLACED,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "COD") "PENDING" else "PAID",
            estimatedMinutes = 25,
            otp = (1000..9999).random().toString(),
            createdAt = System.currentTimeMillis()
        )

        _orders.update { listOf(newOrder) + it }
        _incomingOrderAlert.value = newOrder
        clearCart()
        return newOrder
    }

    fun addCustomerReview(orderId: String, restaurantId: String, rating: Float, comment: String) {
        val newReview = Review(
            id = "rev_${System.currentTimeMillis()}",
            orderId = orderId,
            restaurantId = restaurantId,
            customerName = _currentUser.value.name,
            rating = rating,
            comment = comment,
            date = "Just now"
        )
        _reviews.update { listOf(newReview) + it }
    }
}
