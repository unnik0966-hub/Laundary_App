package com.example.data.repository

import com.example.data.local.dao.FreshFoldDao
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CouponEntity
import com.example.data.local.entity.DeliveryStaffEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderItemEntity
import com.example.data.local.entity.PriceHistoryEntity
import com.example.data.local.entity.ReviewEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.SystemChargesEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.CartItem
import com.example.data.model.DeliveryPreference
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.PriceCalculationResult
import com.example.data.model.Role
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class FreshFoldRepository(private val dao: FreshFoldDao) {

    // --- Services & Dynamic Pricing ---
    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    val activeServices: Flow<List<ServiceEntity>> = dao.getActiveServices()
    val priceHistory: Flow<List<PriceHistoryEntity>> = dao.getAllPriceHistory()
    val systemCharges: Flow<SystemChargesEntity?> = dao.getSystemCharges()

    suspend fun getSystemChargesDirect(): SystemChargesEntity {
        return dao.getSystemChargesDirect() ?: SystemChargesEntity()
    }

    suspend fun updateServicePrice(
        serviceId: Long,
        newPrice: Double,
        adminName: String = "Admin Owner"
    ) {
        val service = dao.getServiceById(serviceId) ?: return
        val previousPrice = service.price
        if (previousPrice != newPrice) {
            val updated = service.copy(price = newPrice)
            dao.updateService(updated)
            dao.insertPriceHistory(
                PriceHistoryEntity(
                    serviceId = service.id,
                    serviceName = service.name,
                    previousPrice = previousPrice,
                    newPrice = newPrice,
                    changedBy = adminName,
                    changedAtTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun saveService(service: ServiceEntity, adminName: String = "Admin Owner") {
        if (service.id == 0L) {
            val newId = dao.insertService(service)
            dao.insertPriceHistory(
                PriceHistoryEntity(
                    serviceId = newId,
                    serviceName = service.name,
                    previousPrice = 0.0,
                    newPrice = service.price,
                    changedBy = adminName
                )
            )
        } else {
            val existing = dao.getServiceById(service.id)
            if (existing != null && existing.price != service.price) {
                dao.insertPriceHistory(
                    PriceHistoryEntity(
                        serviceId = service.id,
                        serviceName = service.name,
                        previousPrice = existing.price,
                        newPrice = service.price,
                        changedBy = adminName
                    )
                )
            }
            dao.updateService(service)
        }
    }

    suspend fun deleteService(service: ServiceEntity) {
        dao.deleteService(service)
    }

    suspend fun updateSystemCharges(charges: SystemChargesEntity) {
        dao.updateSystemCharges(charges)
    }

    // --- Dynamic Price Calculator ---
    fun calculatePrice(
        items: List<CartItem>,
        charges: SystemChargesEntity,
        deliveryPreference: DeliveryPreference,
        selectedCoupon: CouponEntity?
    ): PriceCalculationResult {
        val serviceCharges = items.sumOf { it.total }
        val additionalCharges = charges.pickupCharge
        val expressCharges = if (deliveryPreference == DeliveryPreference.EXPRESS) charges.expressCharge else 0.0
        val deliveryCharges = if (serviceCharges >= 30.0) 0.0 else charges.deliveryCharge

        var discount = 0.0
        selectedCoupon?.let { coupon ->
            if (serviceCharges >= coupon.minOrderValue) {
                discount = if (coupon.isPercent) {
                    (serviceCharges * (coupon.discountValue / 100.0)).coerceAtMost(coupon.maxDiscount)
                } else {
                    coupon.discountValue.coerceAtMost(serviceCharges)
                }
            }
        }

        val taxableAmount = (serviceCharges + additionalCharges + expressCharges + deliveryCharges - discount).coerceAtLeast(0.0)
        val tax = taxableAmount * (charges.taxPercentage / 100.0)
        val finalTotal = taxableAmount + tax

        return PriceCalculationResult(
            serviceCharges = serviceCharges,
            additionalCharges = additionalCharges,
            expressCharges = expressCharges,
            deliveryCharges = deliveryCharges,
            discount = discount,
            tax = tax,
            finalTotal = finalTotal
        )
    }

    // --- Orders & Pricing Snapshot ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    fun getOrderById(orderId: String): Flow<OrderEntity?> = dao.getOrderById(orderId)

    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        pickupDate: String,
        pickupTimeSlot: String,
        deliveryDate: String,
        deliveryPreference: DeliveryPreference,
        items: List<CartItem>,
        calculation: PriceCalculationResult,
        paymentMethod: PaymentMethod
    ): OrderEntity {
        val orderNumber = "FF${Random.nextInt(10000, 99999)}"
        val order = OrderEntity(
            id = orderNumber,
            userId = 1,
            customerName = customerName,
            customerPhone = customerPhone,
            customerAddress = customerAddress,
            status = OrderStatus.CONFIRMED,
            pickupDate = pickupDate,
            pickupTimeSlot = pickupTimeSlot,
            deliveryDate = deliveryDate,
            deliveryPreference = deliveryPreference,
            subtotal = calculation.serviceCharges,
            additionalCharges = calculation.additionalCharges,
            expressCharges = calculation.expressCharges,
            deliveryCharges = calculation.deliveryCharges,
            discount = calculation.discount,
            tax = calculation.tax,
            finalAmount = calculation.finalTotal,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == PaymentMethod.COD) PaymentStatus.PENDING else PaymentStatus.PAID,
            deliveryStaffId = 1,
            deliveryStaffName = "David Miller",
            deliveryStaffPhone = "+1 555-384-9211",
            createdAtTimestamp = System.currentTimeMillis()
        )
        dao.insertOrder(order)

        // Store price snapshots for each item so future admin price updates never alter this order
        val orderItems = items.map { item ->
            OrderItemEntity(
                orderId = orderNumber,
                serviceId = item.serviceId,
                serviceName = item.serviceName,
                pricingType = item.pricingType,
                unitPriceSnapshot = item.unitPrice,
                quantity = item.quantity,
                itemTotal = item.total
            )
        }
        dao.insertOrderItems(orderItems)

        // Add a notification
        dao.insertNotification(
            NotificationEntity(
                title = "Order $orderNumber Confirmed",
                message = "Pickup scheduled for $pickupDate between $pickupTimeSlot. Our driver David Miller will arrive on time.",
                type = "ORDER"
            )
        )

        return order
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        dao.updateOrderStatus(orderId, status)
        val notifMessage = when (status) {
            OrderStatus.PICKED_UP -> "Your clothes have been collected by our delivery valet."
            OrderStatus.WASHING -> "Clothes are in washing cycle with hypoallergenic detergent."
            OrderStatus.IRONING -> "High-pressure crease-free steam press in progress."
            OrderStatus.QUALITY_CHECK -> "Passed 4-point quality inspection: zero stains, crisp fold."
            OrderStatus.OUT_FOR_DELIVERY -> "Out for delivery! Your driver is approaching your doorstep."
            OrderStatus.DELIVERED -> "Delivered! Fresh clothes delivered right to your door."
            OrderStatus.CANCELLED -> "Your order $orderId has been cancelled."
            else -> "Order status updated to ${status.display}."
        }
        dao.insertNotification(
            NotificationEntity(
                title = "Order $orderId: ${status.display}",
                message = notifMessage,
                type = "ORDER"
            )
        )
    }

    suspend fun overrideOrderPrice(orderId: String, newPrice: Double, reason: String) {
        dao.overrideOrderPrice(orderId, newPrice, reason)
        dao.insertNotification(
            NotificationEntity(
                title = "Price Adjusted on Order $orderId",
                message = "Amount revised to \$${String.format(Locale.US, "%.2f", newPrice)}. Reason: $reason",
                type = "PAYMENT"
            )
        )
    }

    suspend fun assignDeliveryStaff(orderId: String, staff: DeliveryStaffEntity) {
        dao.assignDeliveryStaff(orderId, staff.id, staff.name, staff.phone)
    }

    // --- Delivery Staff ---
    val allDeliveryStaff: Flow<List<DeliveryStaffEntity>> = dao.getAllDeliveryStaff()

    // --- Addresses ---
    val allAddresses: Flow<List<AddressEntity>> = dao.getAllAddresses()

    suspend fun addAddress(address: AddressEntity) {
        if (address.isDefault) {
            dao.clearDefaultAddress()
        }
        dao.insertAddress(address)
    }

    suspend fun updateAddress(address: AddressEntity) {
        if (address.isDefault) {
            dao.clearDefaultAddress()
        }
        dao.updateAddress(address)
    }

    suspend fun deleteAddress(address: AddressEntity) {
        dao.deleteAddress(address)
    }

    suspend fun setDefaultAddress(id: Long) {
        dao.clearDefaultAddress()
        dao.setDefaultAddress(id)
    }

    // --- Coupons ---
    val allCoupons: Flow<List<CouponEntity>> = dao.getAllCoupons()
    val activeCoupons: Flow<List<CouponEntity>> = dao.getActiveCoupons()

    suspend fun saveCoupon(coupon: CouponEntity) {
        if (coupon.id == 0L) {
            dao.insertCoupon(coupon)
        } else {
            dao.updateCoupon(coupon)
        }
    }

    suspend fun deleteCoupon(coupon: CouponEntity) {
        dao.deleteCoupon(coupon)
    }

    // --- Notifications ---
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    // --- Reviews ---
    val allReviews: Flow<List<ReviewEntity>> = dao.getAllReviews()

    suspend fun submitReview(orderId: String, serviceRating: Int, deliveryRating: Int, comment: String) {
        dao.insertReview(
            ReviewEntity(
                orderId = orderId,
                serviceRating = serviceRating,
                deliveryRating = deliveryRating,
                comment = comment
            )
        )
    }

    // --- Support Tickets ---
    val allTickets: Flow<List<SupportTicketEntity>> = dao.getAllTickets()

    suspend fun submitTicket(orderId: String?, subject: String, description: String) {
        dao.insertTicket(
            SupportTicketEntity(
                orderId = orderId,
                subject = subject,
                description = description
            )
        )
    }

    // --- User Profile ---
    val user: Flow<UserEntity?> = dao.getUser()

    suspend fun updateUser(user: UserEntity) {
        dao.updateUser(user)
    }
}
