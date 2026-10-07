package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DeliveryPreference
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.PricingType
import com.example.data.model.Role

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val name: String = "Sarah Jenkins",
    val mobile: String = "+1 555-019-2834",
    val email: String = "sarah.jenkins@example.com",
    val location: String = "Austin, TX - Downtown",
    val role: Role = Role.CUSTOMER
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: String,
    val pricingType: PricingType,
    val price: Double,
    val unit: String,
    val minQuantity: Double = 1.0,
    val estimatedHours: Int = 24,
    val fabricTypes: String = "Cotton, Linen, Synthetic, Wool blends",
    val isActive: Boolean = true,
    val iconName: String = "wash"
)

@Entity(tableName = "price_history")
data class PriceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceId: Long,
    val serviceName: String,
    val previousPrice: Double,
    val newPrice: Double,
    val changedBy: String = "Admin Owner",
    val changedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_charges")
data class SystemChargesEntity(
    @PrimaryKey val id: Int = 1,
    val pickupCharge: Double = 0.0,
    val deliveryCharge: Double = 3.50,
    val expressCharge: Double = 7.00,
    val minOrderCharge: Double = 10.0,
    val taxPercentage: Double = 8.25
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String, // e.g. "FF10245"
    val userId: Long = 1,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val status: OrderStatus,
    val pickupDate: String,
    val pickupTimeSlot: String,
    val deliveryDate: String,
    val deliveryPreference: DeliveryPreference,
    val subtotal: Double,
    val additionalCharges: Double,
    val expressCharges: Double,
    val deliveryCharges: Double,
    val discount: Double,
    val tax: Double,
    val finalAmount: Double,
    val adminOverrideAmount: Double? = null,
    val adminOverrideReason: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.UPI,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val deliveryStaffId: Long? = 1,
    val deliveryStaffName: String? = "David Miller",
    val deliveryStaffPhone: String? = "+1 555-384-9211",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val serviceId: Long,
    val serviceName: String,
    val pricingType: PricingType,
    val unitPriceSnapshot: Double, // Price snapshot at time of confirmation
    val quantity: Double,
    val itemTotal: Double
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val label: String, // "Home", "Work", "Other"
    val recipientName: String,
    val phone: String,
    val streetAddress: String,
    val landmark: String = "",
    val city: String = "Austin",
    val postalCode: String = "78701",
    val isDefault: Boolean = false
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val isPercent: Boolean, // true = %, false = fixed
    val discountValue: Double,
    val minOrderValue: Double = 15.0,
    val maxDiscount: Double = 20.0,
    val expiryDate: String = "31 Dec 2026",
    val isActive: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "ORDER", "PICKUP", "DELIVERY", "PAYMENT", "OFFER"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "delivery_staff")
data class DeliveryStaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val vehicleNumber: String,
    val activeOrdersCount: Int = 2,
    val rating: Double = 4.9,
    val isOnline: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val serviceRating: Int,
    val deliveryRating: Int,
    val comment: String,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String? = null,
    val subject: String,
    val description: String,
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED
    val priority: String = "NORMAL",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
