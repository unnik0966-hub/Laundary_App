package com.example.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CouponEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.model.CartItem
import com.example.data.model.DeliveryPreference
import com.example.data.model.PaymentMethod
import com.example.data.model.PriceCalculationResult
import com.example.data.model.SlotAvailability
import com.example.data.model.TimeSlot
import com.example.ui.components.PriceBreakdownCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.BackgroundClean
import com.example.ui.theme.BorderLight
import com.example.ui.theme.MintContainer
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import java.util.Locale

@Composable
fun BookingScreen(
    currentStep: Int,
    services: List<ServiceEntity>,
    cartItems: Map<Long, CartItem>,
    addresses: List<AddressEntity>,
    selectedAddress: AddressEntity?,
    selectedPickupDate: String,
    selectedPickupTimeSlot: String,
    selectedDeliveryPreference: DeliveryPreference,
    selectedCoupon: CouponEntity?,
    selectedPaymentMethod: PaymentMethod,
    priceCalculation: PriceCalculationResult,
    coupons: List<CouponEntity>,
    onUpdateQuantity: (ServiceEntity, Double) -> Unit,
    onSelectAddress: (AddressEntity) -> Unit,
    onSelectPickupDate: (String) -> Unit,
    onSelectTimeSlot: (String) -> Unit,
    onSelectDeliveryPreference: (DeliveryPreference) -> Unit,
    onApplyCoupon: (CouponEntity?) -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onConfirmOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {
        // Top Header with Step indicator
        BookingProgressHeader(
            currentStep = currentStep,
            onBackClick = onPreviousStep
        )

        // Step Content inside scrollable body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                1 -> Step1SelectServices(
                    services = services,
                    cartItems = cartItems,
                    onUpdateQuantity = onUpdateQuantity
                )
                2 -> Step2AdjustQuantity(
                    services = services,
                    cartItems = cartItems,
                    onUpdateQuantity = onUpdateQuantity
                )
                3 -> Step3SelectAddress(
                    addresses = addresses,
                    selectedAddress = selectedAddress ?: addresses.firstOrNull(),
                    onSelectAddress = onSelectAddress
                )
                4 -> Step4SelectDateTime(
                    selectedDate = selectedPickupDate,
                    selectedSlot = selectedPickupTimeSlot,
                    onDateSelect = onSelectPickupDate,
                    onSlotSelect = onSelectTimeSlot
                )
                5 -> Step5DeliveryPreference(
                    selectedPreference = selectedDeliveryPreference,
                    onSelectPreference = onSelectDeliveryPreference
                )
                6 -> Step6ReviewOrder(
                    cartItems = cartItems.values.toList(),
                    selectedAddress = selectedAddress ?: addresses.firstOrNull(),
                    pickupDate = selectedPickupDate,
                    pickupSlot = selectedPickupTimeSlot,
                    deliveryPreference = selectedDeliveryPreference,
                    calculation = priceCalculation,
                    selectedCoupon = selectedCoupon,
                    coupons = coupons,
                    selectedPaymentMethod = selectedPaymentMethod,
                    onApplyCoupon = onApplyCoupon,
                    onSelectPaymentMethod = onSelectPaymentMethod
                )
            }
        }

        // Persistent Sticky Bottom Action Bar with live total
        BookingBottomBar(
            currentStep = currentStep,
            totalAmount = priceCalculation.finalTotal,
            isNextEnabled = when (currentStep) {
                1 -> cartItems.isNotEmpty()
                2 -> cartItems.isNotEmpty()
                else -> true
            },
            onNextClick = if (currentStep == 6) onConfirmOrder else onNextStep
        )
    }
}

@Composable
fun BookingProgressHeader(
    currentStep: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepTitles = listOf(
        "1. Select Services",
        "2. Quantity & Weight",
        "3. Pickup Address",
        "4. Date & Time",
        "5. Delivery Speed",
        "6. Review & Confirm"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("booking_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SlateTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stepTitles.getOrElse(currentStep - 1) { "Booking" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "Step $currentStep of 6",
                    style = MaterialTheme.typography.labelSmall,
                    color = TealPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (step in 1..6) {
                val isCompleted = step <= currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isCompleted) TealPrimary else BorderLight)
                )
            }
        }
    }
}

@Composable
fun Step1SelectServices(
    services: List<ServiceEntity>,
    cartItems: Map<Long, CartItem>,
    onUpdateQuantity: (ServiceEntity, Double) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Choose Laundry Services",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "Add services to your basket. Prices are dynamically fetched from system.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextSecondary
            )
        }

        items(services) { service ->
            val inCart = cartItems.containsKey(service.id)
            val currentQty = cartItems[service.id]?.quantity ?: 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = if (inCart) androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = service.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", service.price)} / ${service.unit}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }

                    if (!inCart) {
                        Button(
                            onClick = { onUpdateQuantity(service, service.minQuantity) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.testTag("add_service_${service.id}")
                        ) {
                            Text("Add", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { onUpdateQuantity(service, currentQty - 1.0) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(TealContainer)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = TealPrimary)
                            }

                            Text(
                                text = "${currentQty.toInt()} ${service.unit}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )

                            IconButton(
                                onClick = { onUpdateQuantity(service, currentQty + 1.0) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(TealPrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step2AdjustQuantity(
    services: List<ServiceEntity>,
    cartItems: Map<Long, CartItem>,
    onUpdateQuantity: (ServiceEntity, Double) -> Unit
) {
    val serviceMap = services.associateBy { it.id }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Specify Weight & Quantity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "Final weight will be accurately measured on our digital calibrated scale during collection.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextSecondary
            )
        }

        items(cartItems.values.toList()) { item ->
            val service = serviceMap[item.serviceId]
            if (service != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.serviceName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = "Rate: $${String.format(Locale.US, "%.2f", item.unitPrice)} / ${item.unit}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateTextSecondary
                                )
                            }

                            Text(
                                text = "$${String.format(Locale.US, "%.2f", item.total)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimated ${item.unit.uppercase()}:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextPrimary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(service, (item.quantity - 1.0).coerceAtLeast(1.0)) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TealContainer)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = TealPrimary)
                                }

                                Text(
                                    text = "${item.quantity.toInt()} ${item.unit}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary
                                )

                                IconButton(
                                    onClick = { onUpdateQuantity(service, item.quantity + 1.0) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step3SelectAddress(
    addresses: List<AddressEntity>,
    selectedAddress: AddressEntity?,
    onSelectAddress: (AddressEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Select Pickup Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "Our FreshFold driver will collect laundry bags at this location.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextSecondary
            )
        }

        items(addresses) { address ->
            val isSelected = selectedAddress?.id == address.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAddress(address) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) TealContainer else BackgroundClean),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (address.label.equals("Home", ignoreCase = true)) Icons.Default.Home else Icons.Default.Work,
                            contentDescription = null,
                            tint = if (isSelected) TealPrimary else SlateTextSecondary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = address.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            if (address.isDefault) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MintContainer,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = "DEFAULT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MintSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = address.streetAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = "${address.city}, ${address.postalCode} • Ph: ${address.phone}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextMuted
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectAddress(address) },
                        colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                    )
                }
            }
        }
    }
}

@Composable
fun Step4SelectDateTime(
    selectedDate: String,
    selectedSlot: String,
    onDateSelect: (String) -> Unit,
    onSlotSelect: (String) -> Unit
) {
    val dates = listOf("Today", "Tomorrow", "Wednesday, Oct 8", "Thursday, Oct 9")
    val slots = listOf(
        TimeSlot("1", "09:00 AM – 11:00 AM", "Morning", SlotAvailability.AVAILABLE),
        TimeSlot("2", "11:00 AM – 01:00 PM", "Mid-day", SlotAvailability.LIMITED),
        TimeSlot("3", "02:00 PM – 04:00 PM", "Afternoon", SlotAvailability.AVAILABLE),
        TimeSlot("4", "04:00 PM – 06:00 PM", "Evening", SlotAvailability.AVAILABLE),
        TimeSlot("5", "06:00 PM – 08:00 PM", "Late Evening", SlotAvailability.LIMITED)
    )

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Select Pickup Day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(dates) { date ->
                    val isSelected = selectedDate == date
                    Surface(
                        onClick = { onDateSelect(date) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) TealPrimary else SurfaceWhite,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SlateTextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select Time Slot",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "Driver will arrive within this 2-hour window.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextSecondary
            )
        }

        items(slots) { slot ->
            val isSelected = selectedSlot == slot.timeRange
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSlotSelect(slot.timeRange) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isSelected) TealPrimary else SlateTextSecondary
                        )
                        Column {
                            Text(
                                text = slot.timeRange,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = slot.day,
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateTextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (slot.availability) {
                            SlotAvailability.AVAILABLE -> StatusSuccessBg
                            SlotAvailability.LIMITED -> StatusWarningBg
                            SlotAvailability.UNAVAILABLE -> StatusErrorBg
                        }
                    ) {
                        Text(
                            text = slot.availability.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (slot.availability) {
                                SlotAvailability.AVAILABLE -> StatusSuccess
                                SlotAvailability.LIMITED -> StatusWarning
                                SlotAvailability.UNAVAILABLE -> StatusError
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step5DeliveryPreference(
    selectedPreference: DeliveryPreference,
    onSelectPreference: (DeliveryPreference) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Delivery Speed Preference",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SlateTextPrimary
        )

        // Standard Delivery Card
        val isStandard = selectedPreference == DeliveryPreference.STANDARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectPreference(DeliveryPreference.STANDARD) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = if (isStandard) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Standard Care (24-48h)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Normal eco-wash & natural finish with zero extra charges.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Included in normal fee",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                RadioButton(
                    selected = isStandard,
                    onClick = { onSelectPreference(DeliveryPreference.STANDARD) },
                    colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                )
            }
        }

        // Express Priority Delivery Card
        val isExpress = selectedPreference == DeliveryPreference.EXPRESS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectPreference(DeliveryPreference.EXPRESS) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = if (isExpress) androidx.compose.foundation.BorderStroke(2.dp, AmberAccent) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Express Priority (< 12h)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "⚡ FAST",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Same-day turnaround for urgent garments, meetings, or travel.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+$7.00 Express Priority Surcharge",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                RadioButton(
                    selected = isExpress,
                    onClick = { onSelectPreference(DeliveryPreference.EXPRESS) },
                    colors = RadioButtonDefaults.colors(selectedColor = AmberAccent)
                )
            }
        }
    }
}

@Composable
fun Step6ReviewOrder(
    cartItems: List<CartItem>,
    selectedAddress: AddressEntity?,
    pickupDate: String,
    pickupSlot: String,
    deliveryPreference: DeliveryPreference,
    calculation: PriceCalculationResult,
    selectedCoupon: CouponEntity?,
    coupons: List<CouponEntity>,
    selectedPaymentMethod: PaymentMethod,
    onApplyCoupon: (CouponEntity?) -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Review Order & Confirm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Schedule Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pickup Time:", style = MaterialTheme.typography.bodyMedium, color = SlateTextSecondary)
                        Text("$pickupDate • $pickupSlot", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Speed:", style = MaterialTheme.typography.bodyMedium, color = SlateTextSecondary)
                        Text(deliveryPreference.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TealPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pickup Address:", style = MaterialTheme.typography.bodyMedium, color = SlateTextSecondary)
                        Text(selectedAddress?.label ?: "Home", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
                    }
                }
            }
        }

        // Coupon Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Apply Promo Code",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(coupons) { coupon ->
                            val isApplied = selectedCoupon?.id == coupon.id
                            Surface(
                                onClick = {
                                    if (isApplied) onApplyCoupon(null) else onApplyCoupon(coupon)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isApplied) TealContainer else BackgroundClean,
                                border = if (isApplied) androidx.compose.foundation.BorderStroke(1.dp, TealPrimary) else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Text(
                                    text = if (isApplied) "${coupon.code} ✓" else coupon.code,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isApplied) TealPrimary else SlateTextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Bill Breakdown (Real-time recalculation)
        item {
            PriceBreakdownCard(
                calculation = calculation,
                hasCouponApplied = selectedCoupon != null,
                couponCode = selectedCoupon?.code,
                onRemoveCoupon = { onApplyCoupon(null) }
            )
        }

        // Payment Method Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )

                    PaymentMethod.values().forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPaymentMethod(method) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = when (method) {
                                        PaymentMethod.UPI -> Icons.Default.QrCode
                                        PaymentMethod.CARD -> Icons.Default.CreditCard
                                        PaymentMethod.NET_BANKING -> Icons.Default.Payment
                                        PaymentMethod.COD -> Icons.Default.Money
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) TealPrimary else SlateTextSecondary
                                )
                                Text(
                                    text = method.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = SlateTextPrimary
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectPaymentMethod(method) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingBottomBar(
    currentStep: Int,
    totalAmount: Double,
    isNextEnabled: Boolean,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Price",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", totalAmount)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }

            Button(
                onClick = onNextClick,
                enabled = isNextEnabled,
                modifier = Modifier
                    .width(180.dp)
                    .height(50.dp)
                    .testTag("booking_primary_cta"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(
                    text = if (currentStep == 6) "Confirm Pickup" else "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
