package com.example.data.model

enum class UserRole(val displayName: String, val subtitle: String) {
    SUPER_ADMIN("Super Admin", "App Owner (Tanmoy Jana)"),
    FRANCHISE_OWNER("Franchise Owner", "Territory & Sub-restaurants"),
    RESTAURANT_OWNER("Restaurant Owner", "Menu & Incoming Orders"),
    DELIVERY_BOY("Delivery Partner", "Tasks, Live GPS & Earnings"),
    CUSTOMER("Customer", "Browse, Cart, Pay & Live Tracking")
}
