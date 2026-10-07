package com.example.data.model

enum class Role {
    CUSTOMER,
    ADMIN,
    DELIVERY_STAFF
}

enum class PricingType(val label: String) {
    PER_KG("Per KG"),
    PER_ITEM("Per Item"),
    FIXED("Fixed Price")
}

enum class OrderStatus(val display: String, val stepIndex: Int) {
    PLACED("Order Placed", 0),
    CONFIRMED("Pickup Scheduled", 1),
    PICKED_UP("Picked Up", 2),
    WASHING("Washing", 3),
    IRONING("Ironing", 4),
    QUALITY_CHECK("Quality Check", 5),
    READY("Ready for Delivery", 6),
    OUT_FOR_DELIVERY("Out for Delivery", 7),
    DELIVERED("Delivered", 8),
    CANCELLED("Cancelled", -1)
}

enum class DeliveryPreference(val label: String, val speedText: String) {
    STANDARD("Standard Delivery", "24 - 48 Hours"),
    EXPRESS("Express Delivery", "Within 12 Hours")
}

enum class PaymentMethod(val label: String) {
    UPI("UPI (GPay / PhonePe)"),
    CARD("Credit / Debit Card"),
    NET_BANKING("Net Banking"),
    COD("Cash on Delivery")
}

enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    PENDING("Payment Pending"),
    FAILED("Payment Failed"),
    REFUNDED("Refunded")
}

enum class SlotAvailability(val label: String) {
    AVAILABLE("Available"),
    LIMITED("Limited Slots"),
    UNAVAILABLE("Unavailable")
}

data class TimeSlot(
    val id: String,
    val timeRange: String,
    val day: String,
    val availability: SlotAvailability
)

data class CartItem(
    val serviceId: Long,
    val serviceName: String,
    val pricingType: PricingType,
    val unitPrice: Double,
    val unit: String,
    val quantity: Double
) {
    val total: Double
        get() = unitPrice * quantity
}

data class PriceCalculationResult(
    val serviceCharges: Double,
    val additionalCharges: Double,
    val expressCharges: Double,
    val deliveryCharges: Double,
    val discount: Double,
    val tax: Double,
    val finalTotal: Double
)
