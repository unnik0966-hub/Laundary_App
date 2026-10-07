package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.AddressEntity
import com.example.data.model.Role
import com.example.ui.components.FreshFoldBottomNavBar
import com.example.ui.components.FreshFoldHeader
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthLoginScreen
import com.example.ui.screens.auth.AuthOtpScreen
import com.example.ui.screens.auth.OnboardingScreen
import com.example.ui.screens.auth.SplashScreen
import com.example.ui.screens.booking.BookingScreen
import com.example.ui.screens.booking.OrderSuccessScreen
import com.example.ui.screens.delivery.DeliveryStaffScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.offers.OffersScreen
import com.example.ui.screens.offers.SupportScreen
import com.example.ui.screens.orders.OrderHistoryScreen
import com.example.ui.screens.orders.OrderTrackingScreen
import com.example.ui.screens.profile.NotificationScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ReviewExperienceDialog
import com.example.ui.screens.services.ServiceDiscoveryScreen
import com.example.ui.theme.BackgroundClean
import com.example.ui.theme.FreshFoldTheme
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.FreshFoldViewModel
import com.example.ui.viewmodel.NavTab

class MainActivity : ComponentActivity() {

    private val viewModel: FreshFoldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreshFoldTheme {
                FreshFoldApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FreshFoldApp(viewModel: FreshFoldViewModel) {
    val destination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val navTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val role by viewModel.activeRole.collectAsStateWithLifecycle()

    val user by viewModel.user.collectAsStateWithLifecycle()
    val activeServices by viewModel.activeServices.collectAsStateWithLifecycle()
    val allServices by viewModel.allServices.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val activeCoupons by viewModel.activeCoupons.collectAsStateWithLifecycle()
    val allCoupons by viewModel.allCoupons.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val priceHistory by viewModel.priceHistory.collectAsStateWithLifecycle()
    val systemCharges by viewModel.systemCharges.collectAsStateWithLifecycle()
    val deliveryStaff by viewModel.deliveryStaff.collectAsStateWithLifecycle()

    val bookingStep by viewModel.bookingStep.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val selectedAddress by viewModel.selectedAddress.collectAsStateWithLifecycle()
    val selectedPickupDate by viewModel.selectedPickupDate.collectAsStateWithLifecycle()
    val selectedPickupTimeSlot by viewModel.selectedPickupTimeSlot.collectAsStateWithLifecycle()
    val selectedDeliveryPreference by viewModel.selectedDeliveryPreference.collectAsStateWithLifecycle()
    val selectedCoupon by viewModel.selectedCoupon.collectAsStateWithLifecycle()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()
    val priceCalculation by viewModel.priceCalculation.collectAsStateWithLifecycle()

    val selectedOrderId by viewModel.selectedOrderId.collectAsStateWithLifecycle()
    val latestCreatedOrder by viewModel.latestCreatedOrder.collectAsStateWithLifecycle()

    var showReviewDialogForOrderId by remember { mutableStateOf<String?>(null) }
    var authPhoneNumber by remember { mutableStateOf("+1 (555) 019-2834") }
    var authOtpCode by remember { mutableStateOf("") }

    // Global back handling
    BackHandler(enabled = destination != AppDestination.MAIN_APP && destination != AppDestination.SPLASH) {
        when (destination) {
            AppDestination.BOOKING_FLOW -> viewModel.previousBookingStep()
            AppDestination.ORDER_SUCCESS -> viewModel.navigateTo(AppDestination.MAIN_APP)
            AppDestination.ORDER_TRACKING -> viewModel.navigateTo(AppDestination.MAIN_APP)
            AppDestination.SERVICES_CATALOG -> viewModel.navigateTo(AppDestination.MAIN_APP)
            AppDestination.NOTIFICATIONS -> viewModel.navigateTo(AppDestination.MAIN_APP)
            AppDestination.AUTH_OTP -> viewModel.navigateTo(AppDestination.AUTH_LOGIN)
            AppDestination.AUTH_LOGIN -> viewModel.navigateTo(AppDestination.ONBOARDING)
            AppDestination.ADMIN_DASHBOARD, AppDestination.DELIVERY_STAFF_VIEW -> viewModel.switchRole(Role.CUSTOMER)
            else -> viewModel.navigateTo(AppDestination.MAIN_APP)
        }
    }

    Crossfade(targetState = destination, label = "ScreenTransition") { currentDest ->
        when (currentDest) {
            AppDestination.SPLASH -> {
                SplashScreen(
                    onSplashFinished = { viewModel.navigateTo(AppDestination.ONBOARDING) }
                )
            }

            AppDestination.ONBOARDING -> {
                OnboardingScreen(
                    onFinished = { viewModel.navigateTo(AppDestination.AUTH_LOGIN) }
                )
            }

            AppDestination.AUTH_LOGIN -> {
                AuthLoginScreen(
                    phoneValue = authPhoneNumber,
                    onPhoneChange = { authPhoneNumber = it },
                    onContinueClick = { viewModel.navigateTo(AppDestination.AUTH_OTP) }
                )
            }

            AppDestination.AUTH_OTP -> {
                AuthOtpScreen(
                    phoneNumber = authPhoneNumber,
                    otpValue = authOtpCode,
                    onOtpChange = { authOtpCode = it },
                    onVerifyClick = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                    onResendClick = {}
                )
            }

            AppDestination.SERVICES_CATALOG -> {
                ServiceDiscoveryScreen(
                    services = activeServices,
                    onBackClick = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                    onSelectService = { service ->
                        viewModel.startBookingFlow(service)
                    }
                )
            }

            AppDestination.BOOKING_FLOW -> {
                BookingScreen(
                    currentStep = bookingStep,
                    services = activeServices,
                    cartItems = cartItems,
                    addresses = addresses,
                    selectedAddress = selectedAddress,
                    selectedPickupDate = selectedPickupDate,
                    selectedPickupTimeSlot = selectedPickupTimeSlot,
                    selectedDeliveryPreference = selectedDeliveryPreference,
                    selectedCoupon = selectedCoupon,
                    selectedPaymentMethod = selectedPaymentMethod,
                    priceCalculation = priceCalculation,
                    coupons = activeCoupons,
                    onUpdateQuantity = { s, q -> viewModel.updateCartItemQuantity(s, q) },
                    onSelectAddress = { viewModel.selectAddress(it) },
                    onSelectPickupDate = { viewModel.selectPickupDate(it) },
                    onSelectTimeSlot = { viewModel.selectTimeSlot(it) },
                    onSelectDeliveryPreference = { viewModel.selectDeliveryPreference(it) },
                    onApplyCoupon = { viewModel.applyCoupon(it) },
                    onSelectPaymentMethod = { viewModel.selectPaymentMethod(it) },
                    onNextStep = { viewModel.nextBookingStep() },
                    onPreviousStep = { viewModel.previousBookingStep() },
                    onConfirmOrder = { viewModel.confirmOrder() }
                )
            }

            AppDestination.ORDER_SUCCESS -> {
                OrderSuccessScreen(
                    order = latestCreatedOrder,
                    onTrackOrderClick = { id -> viewModel.openOrderTracking(id) },
                    onBackToHomeClick = {
                        viewModel.selectNavTab(NavTab.HOME)
                        viewModel.navigateTo(AppDestination.MAIN_APP)
                    }
                )
            }

            AppDestination.ORDER_TRACKING -> {
                val order = allOrders.firstOrNull { it.id == selectedOrderId }
                OrderTrackingScreen(
                    order = order,
                    orderItems = emptyList(),
                    onBackClick = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                    onRateExperienceClick = {
                        showReviewDialogForOrderId = order?.id
                    }
                )
            }

            AppDestination.NOTIFICATIONS -> {
                NotificationScreen(
                    notifications = notifications,
                    onBackClick = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                    onMarkAllReadClick = { viewModel.markNotificationsRead() }
                )
            }

            AppDestination.ADMIN_DASHBOARD -> {
                AdminDashboardScreen(
                    orders = allOrders,
                    services = allServices,
                    priceHistory = priceHistory,
                    systemCharges = systemCharges,
                    deliveryStaff = deliveryStaff,
                    coupons = allCoupons,
                    onOpenCustomerView = { viewModel.switchRole(Role.CUSTOMER) },
                    onSaveService = { viewModel.adminSaveService(it) },
                    onDeleteService = { viewModel.adminDeleteService(it) },
                    onUpdateSystemCharges = { viewModel.adminUpdateSystemCharges(it) },
                    onUpdateOrderStatus = { id, st -> viewModel.adminUpdateOrderStatus(id, st) },
                    onOverrideOrderPrice = { id, p, r -> viewModel.adminOverrideOrderPrice(id, p, r) },
                    onAssignDeliveryStaff = { id, s -> viewModel.adminAssignDeliveryStaff(id, s) },
                    onSaveCoupon = { viewModel.adminSaveCoupon(it) },
                    onDeleteCoupon = { viewModel.adminDeleteCoupon(it) }
                )
            }

            AppDestination.DELIVERY_STAFF_VIEW -> {
                DeliveryStaffScreen(
                    orders = allOrders,
                    onMarkPickedUp = { viewModel.deliveryMarkPickedUp(it) },
                    onMarkDelivered = { viewModel.deliveryMarkDelivered(it) },
                    onExitDriverMode = { viewModel.switchRole(Role.CUSTOMER) }
                )
            }

            else -> {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundClean),
                    topBar = {
                        FreshFoldHeader(
                            userName = user.name.split(" ").firstOrNull() ?: "Sarah",
                            location = user.location,
                            activeRole = role,
                            unreadNotifCount = notifications.count { !it.isRead },
                            onNotificationClick = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
                            onRoleChange = { newRole -> viewModel.switchRole(newRole) }
                        )
                    },
                    bottomBar = {
                        FreshFoldBottomNavBar(
                            selectedTab = navTab,
                            onTabSelected = { tab -> viewModel.selectNavTab(tab) }
                        )
                    },
                    floatingActionButton = {
                        // Prominent "Schedule Pickup" FAB that never obstructs the navigation
                        FloatingActionButton(
                            onClick = { viewModel.startBookingFlow() },
                            containerColor = TealPrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .testTag("main_schedule_pickup_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Schedule Pickup",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (navTab) {
                            NavTab.HOME -> {
                                HomeScreen(
                                    userName = user.name.split(" ").firstOrNull() ?: "Sarah",
                                    services = activeServices,
                                    activeOrders = allOrders,
                                    coupons = activeCoupons,
                                    pastOrders = allOrders,
                                    onSchedulePickupClick = { viewModel.startBookingFlow() },
                                    onServiceSelect = { service -> viewModel.startBookingFlow(service) },
                                    onTrackOrderClick = { id -> viewModel.openOrderTracking(id) },
                                    onViewAllServicesClick = { viewModel.navigateTo(AppDestination.SERVICES_CATALOG) },
                                    onClaimOfferClick = { coupon ->
                                        viewModel.applyCoupon(coupon)
                                        viewModel.startBookingFlow()
                                    },
                                    onReorderClick = { id -> viewModel.reorderOrder(id) }
                                )
                            }

                            NavTab.ORDERS -> {
                                OrderHistoryScreen(
                                    orders = allOrders,
                                    onTrackOrderClick = { id -> viewModel.openOrderTracking(id) },
                                    onReorderClick = { id -> viewModel.reorderOrder(id) }
                                )
                            }

                            NavTab.OFFERS -> {
                                OffersScreen(
                                    coupons = activeCoupons,
                                    onApplyCoupon = { coupon ->
                                        viewModel.applyCoupon(coupon)
                                        viewModel.startBookingFlow()
                                    }
                                )
                            }

                            NavTab.SUPPORT -> {
                                SupportScreen(
                                    orders = allOrders,
                                    onSubmitComplaint = { ordId, sub, desc ->
                                        viewModel.submitSupportTicket(ordId, sub, desc)
                                    }
                                )
                            }

                            NavTab.PROFILE -> {
                                ProfileScreen(
                                    user = user,
                                    activeRole = role,
                                    onRoleChange = { newRole -> viewModel.switchRole(newRole) },
                                    onOpenAddressesClick = {},
                                    onOpenNotificationsClick = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Review dialog if triggered
    if (showReviewDialogForOrderId != null) {
        ReviewExperienceDialog(
            orderId = showReviewDialogForOrderId!!,
            onDismiss = { showReviewDialogForOrderId = null },
            onSubmit = { servRating, delRating, comment ->
                viewModel.submitReview(showReviewDialogForOrderId!!, servRating, delRating, comment)
            }
        )
    }
}
