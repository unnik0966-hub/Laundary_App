package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.database.FreshFoldDatabase
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CouponEntity
import com.example.data.local.entity.DeliveryStaffEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderItemEntity
import com.example.data.local.entity.PriceHistoryEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.SystemChargesEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.CartItem
import com.example.data.model.DeliveryPreference
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PriceCalculationResult
import com.example.data.model.PricingType
import com.example.data.model.Role
import com.example.data.repository.FreshFoldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination {
    SPLASH,
    ONBOARDING,
    AUTH_LOGIN,
    AUTH_OTP,
    AUTH_REGISTER,
    MAIN_APP,
    SERVICES_CATALOG,
    BOOKING_FLOW,
    ORDER_SUCCESS,
    ORDER_TRACKING,
    ORDER_DETAILS,
    NOTIFICATIONS,
    ADDRESS_MANAGER,
    SUPPORT_CENTER,
    ADMIN_DASHBOARD,
    ADMIN_PRICING,
    ADMIN_ORDERS,
    ADMIN_COUPONS,
    DELIVERY_STAFF_VIEW
}

enum class NavTab {
    HOME,
    ORDERS,
    OFFERS,
    SUPPORT,
    PROFILE
}

class FreshFoldViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FreshFoldDatabase.getDatabase(application)
    private val repository = FreshFoldRepository(database.freshFoldDao())

    // App Navigation State
    private val _currentDestination = MutableStateFlow(AppDestination.SPLASH)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _currentNavTab = MutableStateFlow(NavTab.HOME)
    val currentNavTab: StateFlow<NavTab> = _currentNavTab.asStateFlow()

    private val _activeRole = MutableStateFlow(Role.CUSTOMER)
    val activeRole: StateFlow<Role> = _activeRole.asStateFlow()

    // Data streams from Repository
    val activeServices: StateFlow<List<ServiceEntity>> = repository.activeServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCoupons: StateFlow<List<CouponEntity>> = repository.activeCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val addresses: StateFlow<List<AddressEntity>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val priceHistory: StateFlow<List<PriceHistoryEntity>> = repository.priceHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemCharges: StateFlow<SystemChargesEntity> = repository.systemCharges
        .combine(MutableStateFlow(SystemChargesEntity())) { charges, default ->
            charges ?: default
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SystemChargesEntity())

    val deliveryStaff: StateFlow<List<DeliveryStaffEntity>> = repository.allDeliveryStaff
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val user: StateFlow<UserEntity> = repository.user
        .combine(MutableStateFlow(UserEntity())) { u, default -> u ?: default }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserEntity())

    // --- Booking Experience State ---
    private val _bookingStep = MutableStateFlow(1) // 1 to 6
    val bookingStep: StateFlow<Int> = _bookingStep.asStateFlow()

    private val _cartItems = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<Long, CartItem>> = _cartItems.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressEntity?>(null)
    val selectedAddress: StateFlow<AddressEntity?> = _selectedAddress.asStateFlow()

    private val _selectedPickupDate = MutableStateFlow("Tomorrow")
    val selectedPickupDate: StateFlow<String> = _selectedPickupDate.asStateFlow()

    private val _selectedPickupTimeSlot = MutableStateFlow("09:00 AM – 11:00 AM")
    val selectedPickupTimeSlot: StateFlow<String> = _selectedPickupTimeSlot.asStateFlow()

    private val _selectedDeliveryPreference = MutableStateFlow(DeliveryPreference.STANDARD)
    val selectedDeliveryPreference: StateFlow<DeliveryPreference> = _selectedDeliveryPreference.asStateFlow()

    private val _selectedCoupon = MutableStateFlow<CouponEntity?>(null)
    val selectedCoupon: StateFlow<CouponEntity?> = _selectedCoupon.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.UPI)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    // Real-Time Dynamic Price Calculation
    val priceCalculation: StateFlow<PriceCalculationResult> = combine(
        _cartItems,
        systemCharges,
        _selectedDeliveryPreference,
        _selectedCoupon
    ) { items, charges, delPref, coupon ->
        repository.calculatePrice(items.values.toList(), charges, delPref, coupon)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PriceCalculationResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    )

    // Current viewed order (for Tracking / Details)
    private val _selectedOrderId = MutableStateFlow<String?>(null)
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    private val _latestCreatedOrder = MutableStateFlow<OrderEntity?>(null)
    val latestCreatedOrder: StateFlow<OrderEntity?> = _latestCreatedOrder.asStateFlow()

    // Auth Form State
    var authPhoneNumber = MutableStateFlow("+1 (555) 019-2834")
    var authOtpCode = MutableStateFlow("")
    var authName = MutableStateFlow("Sarah Jenkins")
    var authEmail = MutableStateFlow("sarah.jenkins@example.com")
    var authLocation = MutableStateFlow("Austin, TX - Downtown")

    init {
        // Seed database if cold start
        viewModelScope.launch {
            FreshFoldDatabase.populateInitialData(database.freshFoldDao())
        }
    }

    // --- Navigation Handlers ---
    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun selectNavTab(tab: NavTab) {
        _currentNavTab.value = tab
        _currentDestination.value = AppDestination.MAIN_APP
    }

    fun switchRole(role: Role) {
        _activeRole.value = role
        when (role) {
            Role.CUSTOMER -> navigateTo(AppDestination.MAIN_APP)
            Role.ADMIN -> navigateTo(AppDestination.ADMIN_DASHBOARD)
            Role.DELIVERY_STAFF -> navigateTo(AppDestination.DELIVERY_STAFF_VIEW)
        }
    }

    // --- Cart & Booking Handlers ---
    fun startBookingFlow(initialService: ServiceEntity? = null) {
        _bookingStep.value = 1
        if (initialService != null) {
            updateCartItemQuantity(initialService, initialService.minQuantity)
        }
        navigateTo(AppDestination.BOOKING_FLOW)
    }

    fun updateCartItemQuantity(service: ServiceEntity, qty: Double) {
        val current = _cartItems.value.toMutableMap()
        if (qty <= 0.0) {
            current.remove(service.id)
        } else {
            current[service.id] = CartItem(
                serviceId = service.id,
                serviceName = service.name,
                pricingType = service.pricingType,
                unitPrice = service.price,
                unit = service.unit,
                quantity = qty
            )
        }
        _cartItems.value = current
    }

    fun setBookingStep(step: Int) {
        _bookingStep.value = step.coerceIn(1, 6)
    }

    fun nextBookingStep() {
        if (_bookingStep.value < 6) {
            _bookingStep.value += 1
        }
    }

    fun previousBookingStep() {
        if (_bookingStep.value > 1) {
            _bookingStep.value -= 1
        } else {
            navigateTo(AppDestination.MAIN_APP)
        }
    }

    fun selectPickupDate(date: String) {
        _selectedPickupDate.value = date
    }

    fun selectTimeSlot(slot: String) {
        _selectedPickupTimeSlot.value = slot
    }

    fun selectDeliveryPreference(pref: DeliveryPreference) {
        _selectedDeliveryPreference.value = pref
    }

    fun selectAddress(address: AddressEntity) {
        _selectedAddress.value = address
    }

    fun applyCoupon(coupon: CouponEntity?) {
        _selectedCoupon.value = coupon
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun confirmOrder() {
        viewModelScope.launch {
            val userVal = user.value
            val addr = _selectedAddress.value?.streetAddress ?: "Apt 4B, 301 Congress Ave, Austin"
            val deliveryDateText = if (_selectedDeliveryPreference.value == DeliveryPreference.EXPRESS) {
                "${_selectedPickupDate.value} Evening (Within 12h)"
            } else {
                "Day after ${_selectedPickupDate.value}"
            }

            val created = repository.placeOrder(
                customerName = userVal.name,
                customerPhone = userVal.mobile,
                customerAddress = addr,
                pickupDate = _selectedPickupDate.value,
                pickupTimeSlot = _selectedPickupTimeSlot.value,
                deliveryDate = deliveryDateText,
                deliveryPreference = _selectedDeliveryPreference.value,
                items = _cartItems.value.values.toList(),
                calculation = priceCalculation.value,
                paymentMethod = _selectedPaymentMethod.value
            )

            _latestCreatedOrder.value = created
            _selectedOrderId.value = created.id
            _cartItems.value = emptyMap()
            _selectedCoupon.value = null
            _bookingStep.value = 1
            navigateTo(AppDestination.ORDER_SUCCESS)
        }
    }

    fun reorderOrder(orderId: String) {
        viewModelScope.launch {
            val items = database.freshFoldDao().getOrderItemsDirect(orderId)
            val allServs = activeServices.value.associateBy { it.id }
            val newCart = mutableMapOf<Long, CartItem>()

            for (item in items) {
                val currentService = allServs[item.serviceId]
                // Use current dynamic price from backend/admin
                val price = currentService?.price ?: item.unitPriceSnapshot
                val unit = currentService?.unit ?: "item"
                newCart[item.serviceId] = CartItem(
                    serviceId = item.serviceId,
                    serviceName = item.serviceName,
                    pricingType = item.pricingType,
                    unitPrice = price,
                    unit = unit,
                    quantity = item.quantity
                )
            }

            _cartItems.value = newCart
            _bookingStep.value = 2 // Go to quantity check
            navigateTo(AppDestination.BOOKING_FLOW)
        }
    }

    fun openOrderTracking(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(AppDestination.ORDER_TRACKING)
    }

    fun openOrderDetails(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(AppDestination.ORDER_DETAILS)
    }

    // --- Admin Operations ---
    fun adminUpdateServicePrice(serviceId: Long, newPrice: Double) {
        viewModelScope.launch {
            repository.updateServicePrice(serviceId, newPrice, "Admin Owner")
            // Also update any in-cart active prices dynamically
            val current = _cartItems.value.toMutableMap()
            if (current.containsKey(serviceId)) {
                val item = current[serviceId]!!
                current[serviceId] = item.copy(unitPrice = newPrice)
                _cartItems.value = current
            }
        }
    }

    fun adminSaveService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.saveService(service, "Admin Owner")
        }
    }

    fun adminDeleteService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
        }
    }

    fun adminUpdateSystemCharges(charges: SystemChargesEntity) {
        viewModelScope.launch {
            repository.updateSystemCharges(charges)
        }
    }

    fun adminUpdateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun adminOverrideOrderPrice(orderId: String, newPrice: Double, reason: String) {
        viewModelScope.launch {
            repository.overrideOrderPrice(orderId, newPrice, reason)
        }
    }

    fun adminAssignDeliveryStaff(orderId: String, staff: DeliveryStaffEntity) {
        viewModelScope.launch {
            repository.assignDeliveryStaff(orderId, staff)
        }
    }

    fun adminSaveCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.saveCoupon(coupon)
        }
    }

    fun adminDeleteCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.deleteCoupon(coupon)
        }
    }

    // --- Delivery Staff Actions ---
    fun deliveryMarkPickedUp(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.PICKED_UP)
        }
    }

    fun deliveryMarkDelivered(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.DELIVERED)
        }
    }

    // --- Support & Reviews ---
    fun submitReview(orderId: String, serviceRating: Int, deliveryRating: Int, comment: String) {
        viewModelScope.launch {
            repository.submitReview(orderId, serviceRating, deliveryRating, comment)
        }
    }

    fun submitSupportTicket(orderId: String?, subject: String, description: String) {
        viewModelScope.launch {
            repository.submitTicket(orderId, subject, description)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    // --- Address Book ---
    fun addAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.addAddress(address)
        }
    }

    fun deleteAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.deleteAddress(address)
        }
    }
}
