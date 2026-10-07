package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.data.model.DeliveryPreference
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.PricingType
import com.example.data.model.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        PriceHistoryEntity::class,
        SystemChargesEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        AddressEntity::class,
        CouponEntity::class,
        NotificationEntity::class,
        DeliveryStaffEntity::class,
        ReviewEntity::class,
        SupportTicketEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FreshFoldDatabase : RoomDatabase() {

    abstract fun freshFoldDao(): FreshFoldDao

    companion object {
        @Volatile
        private var INSTANCE: FreshFoldDatabase? = null

        fun getDatabase(context: Context): FreshFoldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FreshFoldDatabase::class.java,
                    "freshfold_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.freshFoldDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: FreshFoldDao) {
            // Seed User
            dao.insertUser(
                UserEntity(
                    id = 1,
                    name = "Sarah Jenkins",
                    mobile = "+1 555-019-2834",
                    email = "sarah.jenkins@example.com",
                    location = "Austin, TX - Downtown",
                    role = Role.CUSTOMER
                )
            )

            // Seed System Charges
            dao.insertSystemCharges(
                SystemChargesEntity(
                    id = 1,
                    pickupCharge = 0.0,
                    deliveryCharge = 3.50,
                    expressCharge = 7.00,
                    minOrderCharge = 12.00,
                    taxPercentage = 8.25
                )
            )

            // Seed Dynamic Services (Prices managed dynamically by Admin)
            val initialServices = listOf(
                ServiceEntity(
                    name = "Wash & Fold",
                    description = "Everyday clothes washed with hypoallergenic detergent, tumble dried and crisply folded.",
                    category = "Everyday Care",
                    pricingType = PricingType.PER_KG,
                    price = 2.99,
                    unit = "kg",
                    minQuantity = 3.0,
                    estimatedHours = 24,
                    fabricTypes = "Cottons, Linens, Synthetics, Daily Casuals",
                    isActive = true,
                    iconName = "wash"
                ),
                ServiceEntity(
                    name = "Wash & Iron",
                    description = "Thorough eco-wash followed by high-pressure steam pressing and delivered on hangers.",
                    category = "Office & Formal",
                    pricingType = PricingType.PER_KG,
                    price = 4.49,
                    unit = "kg",
                    minQuantity = 2.0,
                    estimatedHours = 24,
                    fabricTypes = "Dress shirts, Trousers, Blouses, Chinos",
                    isActive = true,
                    iconName = "iron"
                ),
                ServiceEntity(
                    name = "Dry Cleaning",
                    description = "Specialized solvent cleaning that protects structure, sheen, and delicate linings.",
                    category = "Delicates",
                    pricingType = PricingType.PER_ITEM,
                    price = 8.99,
                    unit = "item",
                    minQuantity = 1.0,
                    estimatedHours = 48,
                    fabricTypes = "Suits, Blazers, Evening gowns, Heavy coats",
                    isActive = true,
                    iconName = "dryclean"
                ),
                ServiceEntity(
                    name = "Steam Ironing",
                    description = "Professional wrinkle removal with temperature-calibrated steam pressing.",
                    category = "Quick Press",
                    pricingType = PricingType.PER_ITEM,
                    price = 1.99,
                    unit = "item",
                    minQuantity = 3.0,
                    estimatedHours = 12,
                    fabricTypes = "Cotton shirts, Formal pants, Silk tops",
                    isActive = true,
                    iconName = "press"
                ),
                ServiceEntity(
                    name = "Premium Care",
                    description = "Bespoke enzyme hand soak, gentle conditioning, and individual luxury garment packaging.",
                    category = "Luxury",
                    pricingType = PricingType.PER_ITEM,
                    price = 14.99,
                    unit = "item",
                    minQuantity = 1.0,
                    estimatedHours = 72,
                    fabricTypes = "Cashmere, Pure Silk, Embroidery, Designer wear",
                    isActive = true,
                    iconName = "sparkles"
                ),
                ServiceEntity(
                    name = "Express Laundry",
                    description = "Priority expedited service delivered back to your door within 8 hours.",
                    category = "Express",
                    pricingType = PricingType.FIXED,
                    price = 18.99,
                    unit = "order",
                    minQuantity = 1.0,
                    estimatedHours = 8,
                    fabricTypes = "All compatible regular wash items",
                    isActive = true,
                    iconName = "bolt"
                )
            )
            dao.insertServices(initialServices)

            // Seed Initial Price History
            dao.insertPriceHistory(
                PriceHistoryEntity(
                    serviceId = 1,
                    serviceName = "Wash & Fold",
                    previousPrice = 2.49,
                    newPrice = 2.99,
                    changedBy = "System Default",
                    changedAtTimestamp = System.currentTimeMillis() - 86400000L * 7
                )
            )

            // Seed Addresses
            dao.insertAddresses(
                listOf(
                    AddressEntity(
                        userId = 1,
                        label = "Home",
                        recipientName = "Sarah Jenkins",
                        phone = "+1 555-019-2834",
                        streetAddress = "Apt 4B, 301 Congress Ave",
                        landmark = "Opposite City Hall",
                        city = "Austin",
                        postalCode = "78701",
                        isDefault = true
                    ),
                    AddressEntity(
                        userId = 1,
                        label = "Work",
                        recipientName = "Sarah Jenkins",
                        phone = "+1 555-019-2834",
                        streetAddress = "9th Floor, Silicon Labs Tower, 400 W Cesar Chavez",
                        landmark = "Reception Desk",
                        city = "Austin",
                        postalCode = "78701",
                        isDefault = false
                    )
                )
            )

            // Seed Coupons
            dao.insertCoupons(
                listOf(
                    CouponEntity(
                        code = "FRESH50",
                        title = "Welcome 50% Off",
                        description = "Get 50% discount on your first laundry pickup up to $15",
                        isPercent = true,
                        discountValue = 50.0,
                        minOrderValue = 20.0,
                        maxDiscount = 15.0,
                        expiryDate = "31 Dec 2026",
                        isActive = true
                    ),
                    CouponEntity(
                        code = "WEEKEND20",
                        title = "Weekend Breeze 20%",
                        description = "Save 20% on any Dry Cleaning or Premium Care service",
                        isPercent = true,
                        discountValue = 20.0,
                        minOrderValue = 25.0,
                        maxDiscount = 10.0,
                        expiryDate = "31 Dec 2026",
                        isActive = true
                    ),
                    CouponEntity(
                        code = "FREESHIP",
                        title = "Free Delivery",
                        description = "Enjoy zero delivery fee on orders above $25",
                        isPercent = false,
                        discountValue = 3.50,
                        minOrderValue = 25.0,
                        maxDiscount = 3.50,
                        expiryDate = "31 Dec 2026",
                        isActive = true
                    )
                )
            )

            // Seed Delivery Staff
            dao.insertDeliveryStaff(
                listOf(
                    DeliveryStaffEntity(
                        name = "David Miller",
                        phone = "+1 555-384-9211",
                        vehicleNumber="EV-Van #04 (Eco)",
                        activeOrdersCount = 2,
                        rating = 4.9,
                        isOnline = true
                    ),
                    DeliveryStaffEntity(
                        name = "Alex Turner",
                        phone = "+1 555-728-1190",
                        vehicleNumber="Eco-Scooter #12",
                        activeOrdersCount = 1,
                        rating = 4.8,
                        isOnline = true
                    )
                )
            )

            // Seed Active Order #FF10245 (Washing phase)
            val order1 = OrderEntity(
                id = "FF10245",
                userId = 1,
                customerName = "Sarah Jenkins",
                customerPhone = "+1 555-019-2834",
                customerAddress = "Apt 4B, 301 Congress Ave, Austin",
                status = OrderStatus.WASHING,
                pickupDate = "Today",
                pickupTimeSlot = "09:00 AM – 11:00 AM",
                deliveryDate = "Tomorrow",
                deliveryPreference = DeliveryPreference.STANDARD,
                subtotal = 26.91,
                additionalCharges = 0.0,
                expressCharges = 0.0,
                deliveryCharges = 3.50,
                discount = 5.00,
                tax = 2.10,
                finalAmount = 27.51,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.PAID,
                deliveryStaffId = 1,
                deliveryStaffName = "David Miller",
                deliveryStaffPhone = "+1 555-384-9211",
                createdAtTimestamp = System.currentTimeMillis() - 7200000L
            )
            dao.insertOrder(order1)
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(
                        orderId = "FF10245",
                        serviceId = 1,
                        serviceName = "Wash & Fold",
                        pricingType = PricingType.PER_KG,
                        unitPriceSnapshot = 2.99,
                        quantity = 6.0,
                        itemTotal = 17.94
                    ),
                    OrderItemEntity(
                        orderId = "FF10245",
                        serviceId = 2,
                        serviceName = "Wash & Iron",
                        pricingType = PricingType.PER_KG,
                        unitPriceSnapshot = 4.49,
                        quantity = 2.0,
                        itemTotal = 8.98
                    )
                )
            )

            // Seed Completed Past Order #FF10189
            val order2 = OrderEntity(
                id = "FF10189",
                userId = 1,
                customerName = "Sarah Jenkins",
                customerPhone = "+1 555-019-2834",
                customerAddress = "Apt 4B, 301 Congress Ave, Austin",
                status = OrderStatus.DELIVERED,
                pickupDate = "Oct 3, 2026",
                pickupTimeSlot = "11:00 AM – 01:00 PM",
                deliveryDate = "Oct 4, 2026",
                deliveryPreference = DeliveryPreference.STANDARD,
                subtotal = 32.95,
                additionalCharges = 0.0,
                expressCharges = 0.0,
                deliveryCharges = 3.50,
                discount = 0.0,
                tax = 3.01,
                finalAmount = 39.46,
                paymentMethod = PaymentMethod.CARD,
                paymentStatus = PaymentStatus.PAID,
                deliveryStaffId = 1,
                deliveryStaffName = "David Miller",
                deliveryStaffPhone = "+1 555-384-9211",
                createdAtTimestamp = System.currentTimeMillis() - 86400000L * 4
            )
            dao.insertOrder(order2)
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(
                        orderId = "FF10189",
                        serviceId = 3,
                        serviceName = "Dry Cleaning",
                        pricingType = PricingType.PER_ITEM,
                        unitPriceSnapshot = 8.99,
                        quantity = 3.0,
                        itemTotal = 26.97
                    ),
                    OrderItemEntity(
                        orderId = "FF10189",
                        serviceId = 4,
                        serviceName = "Steam Ironing",
                        pricingType = PricingType.PER_ITEM,
                        unitPriceSnapshot = 1.99,
                        quantity = 3.0,
                        itemTotal = 5.97
                    )
                )
            )

            // Seed Notifications
            dao.insertNotifications(
                listOf(
                    NotificationEntity(
                        title = "Your clothes are being washed",
                        message = "Order #FF10245 has entered the eco-friendly washing and softening cycle.",
                        type = "ORDER",
                        timestamp = System.currentTimeMillis() - 3600000L
                    ),
                    NotificationEntity(
                        title = "Driver on the way for pickup",
                        message = "David Miller picked up your laundry bag from Apt 4B.",
                        type = "PICKUP",
                        timestamp = System.currentTimeMillis() - 7200000L
                    ),
                    NotificationEntity(
                        title = "Special Weekend Deal!",
                        message = "Get 20% off all dry cleaning items this weekend using code WEEKEND20.",
                        type = "OFFER",
                        timestamp = System.currentTimeMillis() - 86400000L
                    )
                )
            )
        }
    }
}
