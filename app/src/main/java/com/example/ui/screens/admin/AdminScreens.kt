package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CouponEntity
import com.example.data.local.entity.DeliveryStaffEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.PriceHistoryEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.SystemChargesEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PricingType
import com.example.data.model.Role
import com.example.ui.components.OrderStatusBadge
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
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    orders: List<OrderEntity>,
    services: List<ServiceEntity>,
    priceHistory: List<PriceHistoryEntity>,
    systemCharges: SystemChargesEntity,
    deliveryStaff: List<DeliveryStaffEntity>,
    coupons: List<CouponEntity>,
    onOpenCustomerView: () -> Unit,
    onSaveService: (ServiceEntity) -> Unit,
    onDeleteService: (ServiceEntity) -> Unit,
    onUpdateSystemCharges: (SystemChargesEntity) -> Unit,
    onUpdateOrderStatus: (orderId: String, OrderStatus) -> Unit,
    onOverrideOrderPrice: (orderId: String, newPrice: Double, reason: String) -> Unit,
    onAssignDeliveryStaff: (orderId: String, DeliveryStaffEntity) -> Unit,
    onSaveCoupon: (CouponEntity) -> Unit,
    onDeleteCoupon: (CouponEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Pricing & Services", "Orders", "Coupons")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {
        // Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FreshFold Admin Console",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "Single Source of Truth • Real-Time Pricing",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                onClick = onOpenCustomerView,
                shape = RoundedCornerShape(12.dp),
                color = TealContainer,
                modifier = Modifier.testTag("exit_admin_btn")
            ) {
                Text(
                    text = "Exit to Customer App",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceWhite,
            contentColor = TealPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = TealPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> AdminOverviewTab(orders = orders, services = services)
            1 -> AdminPricingTab(
                services = services,
                charges = systemCharges,
                history = priceHistory,
                onSaveService = onSaveService,
                onDeleteService = onDeleteService,
                onUpdateCharges = onUpdateSystemCharges
            )
            2 -> AdminOrdersTab(
                orders = orders,
                deliveryStaff = deliveryStaff,
                onUpdateStatus = onUpdateOrderStatus,
                onOverridePrice = onOverrideOrderPrice,
                onAssignStaff = onAssignDeliveryStaff
            )
            3 -> AdminCouponsTab(
                coupons = coupons,
                onSaveCoupon = onSaveCoupon,
                onDeleteCoupon = onDeleteCoupon
            )
        }
    }
}

@Composable
fun AdminOverviewTab(
    orders: List<OrderEntity>,
    services: List<ServiceEntity>
) {
    val totalRevenue = orders.sumOf { it.adminOverrideAmount ?: it.finalAmount }
    val pendingPickups = orders.count { it.status == OrderStatus.PLACED || it.status == OrderStatus.CONFIRMED }
    val activeWash = orders.count { it.status == OrderStatus.WASHING || it.status == OrderStatus.IRONING || it.status == OrderStatus.QUALITY_CHECK }
    val completed = orders.count { it.status == OrderStatus.DELIVERED }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Business Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
        }

        // Metric Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Revenue",
                    value = "$${String.format(Locale.US, "%.2f", totalRevenue)}",
                    icon = Icons.Default.MonetizationOn,
                    tint = StatusSuccess,
                    bg = StatusSuccessBg,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Total Orders",
                    value = "${orders.size}",
                    icon = Icons.Default.ReceiptLong,
                    tint = TealPrimary,
                    bg = TealContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Pending Pickups",
                    value = "$pendingPickups",
                    icon = Icons.Default.TrendingUp,
                    tint = AmberAccent,
                    bg = AmberContainer,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "In Processing",
                    value = "$activeWash",
                    icon = Icons.Default.Settings,
                    tint = MintSecondary,
                    bg = MintContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Simulated Analytics Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Weekly Pickup & Washing Volume",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )

                    // Bar Chart
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val heights = listOf(0.4f, 0.65f, 0.5f, 0.85f, 0.95f, 0.7f, 0.45f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { i, day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height((100 * heights[i]).dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(if (i == 4) TealPrimary else TealContainer)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = SlateTextSecondary)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = SlateTextPrimary)
        }
    }
}

@Composable
fun AdminPricingTab(
    services: List<ServiceEntity>,
    charges: SystemChargesEntity,
    history: List<PriceHistoryEntity>,
    onSaveService: (ServiceEntity) -> Unit,
    onDeleteService: (ServiceEntity) -> Unit,
    onUpdateCharges: (SystemChargesEntity) -> Unit
) {
    var editingService by remember { mutableStateOf<ServiceEntity?>(null) }
    var isNewServiceDialogOpen by remember { mutableStateOf(false) }
    var isChargesDialogOpen by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // System Charges Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dynamic System Charges",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "Base fees applied to all customer orders",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateTextSecondary
                            )
                        }

                        Button(
                            onClick = { isChargesDialogOpen = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                            modifier = Modifier.testTag("edit_system_charges_btn")
                        ) {
                            Text("Edit Fees", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee: $${String.format(Locale.US, "%.2f", charges.deliveryCharge)}", style = MaterialTheme.typography.bodyMedium, color = SlateTextPrimary)
                        Text("Express Surcharge: $${String.format(Locale.US, "%.2f", charges.expressCharge)}", style = MaterialTheme.typography.bodyMedium, color = SlateTextPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pickup Fee: $${String.format(Locale.US, "%.2f", charges.pickupCharge)}", style = MaterialTheme.typography.bodyMedium, color = SlateTextPrimary)
                        Text("Sales Tax: ${charges.taxPercentage}%", style = MaterialTheme.typography.bodyMedium, color = SlateTextPrimary)
                    }
                }
            }
        }

        // Laundry Services Header & Add button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Laundry Services Catalog",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Real-time rates (no hardcoded pricing)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        editingService = ServiceEntity(
                            name = "",
                            description = "",
                            category = "Everyday Care",
                            pricingType = PricingType.PER_KG,
                            price = 3.50,
                            unit = "kg"
                        )
                        isNewServiceDialogOpen = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("add_new_service_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Service", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Services list
        items(services) { service ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = service.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TealContainer,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = service.pricingType.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = service.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary,
                            maxLines = 1
                        )

                        Text(
                            text = "Rate: $${String.format(Locale.US, "%.2f", service.price)} / ${service.unit} • Est: ${service.estimatedHours}h",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TealPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                editingService = service
                                isNewServiceDialogOpen = true
                            },
                            modifier = Modifier.testTag("edit_service_${service.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealPrimary)
                        }

                        IconButton(
                            onClick = { onDeleteService(service) },
                            modifier = Modifier.testTag("delete_service_${service.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError)
                        }
                    }
                }
            }
        }

        // Price History Audit Log
        item {
            Text(
                text = "Price Change Audit Trail",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
        }

        items(history.take(5)) { hist ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = hist.serviceName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "Changed by ${hist.changedBy}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", hist.previousPrice)} → $${String.format(Locale.US, "%.2f", hist.newPrice)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.US)
                        Text(
                            text = sdf.format(Date(hist.changedAtTimestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextMuted
                        )
                    }
                }
            }
        }
    }

    // Edit/Add Service Dialog
    if (isNewServiceDialogOpen && editingService != null) {
        var sName by remember { mutableStateOf(editingService!!.name) }
        var sDesc by remember { mutableStateOf(editingService!!.description) }
        var sPrice by remember { mutableStateOf(editingService!!.price.toString()) }
        var sUnit by remember { mutableStateOf(editingService!!.unit) }
        var sType by remember { mutableStateOf(editingService!!.pricingType) }
        var sHours by remember { mutableStateOf(editingService!!.estimatedHours.toString()) }

        AlertDialog(
            onDismissRequest = { isNewServiceDialogOpen = false },
            title = {
                Text(
                    text = if (editingService!!.id == 0L) "Add Laundry Service" else "Edit Service Price & Details",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sName,
                        onValueChange = { sName = it },
                        label = { Text("Service Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = sDesc,
                        onValueChange = { sDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sPrice,
                            onValueChange = { sPrice = it },
                            label = { Text("Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = sUnit,
                            onValueChange = { sUnit = it },
                            label = { Text("Unit (kg, item)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Pricing Type Radio Pills
                        PricingType.values().forEach { pt ->
                            Surface(
                                onClick = { sType = pt },
                                shape = RoundedCornerShape(8.dp),
                                color = if (sType == pt) TealContainer else BackgroundClean,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = pt.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sType == pt) TealPrimary else SlateTextSecondary,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = sHours,
                        onValueChange = { sHours = it },
                        label = { Text("Est. Completion Hours") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedPrice = sPrice.toDoubleOrNull() ?: 2.99
                        val parsedHours = sHours.toIntOrNull() ?: 24
                        onSaveService(
                            editingService!!.copy(
                                name = sName,
                                description = sDesc,
                                price = parsedPrice,
                                unit = sUnit,
                                pricingType = sType,
                                estimatedHours = parsedHours
                            )
                        )
                        isNewServiceDialogOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save Price", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isNewServiceDialogOpen = false }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }

    // Edit Charges Dialog
    if (isChargesDialogOpen) {
        var dCharge by remember { mutableStateOf(charges.deliveryCharge.toString()) }
        var eCharge by remember { mutableStateOf(charges.expressCharge.toString()) }
        var pCharge by remember { mutableStateOf(charges.pickupCharge.toString()) }
        var tPerc by remember { mutableStateOf(charges.taxPercentage.toString()) }

        AlertDialog(
            onDismissRequest = { isChargesDialogOpen = false },
            title = { Text("Update System Surcharges & Taxes", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = dCharge,
                        onValueChange = { dCharge = it },
                        label = { Text("Standard Delivery Fee ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = eCharge,
                        onValueChange = { eCharge = it },
                        label = { Text("Express Delivery Surcharge ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = pCharge,
                        onValueChange = { pCharge = it },
                        label = { Text("Pickup Fee ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = tPerc,
                        onValueChange = { tPerc = it },
                        label = { Text("Tax Percentage (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateCharges(
                            charges.copy(
                                deliveryCharge = dCharge.toDoubleOrNull() ?: 3.50,
                                expressCharge = eCharge.toDoubleOrNull() ?: 7.00,
                                pickupCharge = pCharge.toDoubleOrNull() ?: 0.0,
                                taxPercentage = tPerc.toDoubleOrNull() ?: 8.25
                            )
                        )
                        isChargesDialogOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent)
                ) {
                    Text("Update Charges", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isChargesDialogOpen = false }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<OrderEntity>,
    deliveryStaff: List<DeliveryStaffEntity>,
    onUpdateStatus: (orderId: String, OrderStatus) -> Unit,
    onOverridePrice: (orderId: String, newPrice: Double, reason: String) -> Unit,
    onAssignStaff: (orderId: String, DeliveryStaffEntity) -> Unit
) {
    var overrideDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders) { order ->
            var statusDropdownOpen by remember { mutableStateOf(false) }
            var staffDropdownOpen by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "Customer: ${order.customerName} (${order.customerPhone})",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateTextSecondary
                            )
                        }

                        OrderStatusBadge(status = order.status)
                    }

                    Text(
                        text = "Address: ${order.customerAddress}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pickup: ${order.pickupDate} (${order.pickupTimeSlot})",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextMuted
                        )
                        Text(
                            text = "Total: $${String.format(Locale.US, "%.2f", order.adminOverrideAmount ?: order.finalAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }

                    if (order.adminOverrideAmount != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberContainer
                        ) {
                            Text(
                                text = "Price Adjusted: $${String.format(Locale.US, "%.2f", order.adminOverrideAmount)} • Reason: ${order.adminOverrideReason}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    // Admin Actions: Change Status, Assign Driver, Override Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Change Status
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { statusDropdownOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Update Status", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            DropdownMenu(
                                expanded = statusDropdownOpen,
                                onDismissRequest = { statusDropdownOpen = false }
                            ) {
                                OrderStatus.values().forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st.display) },
                                        onClick = {
                                            onUpdateStatus(order.id, st)
                                            statusDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        // Override Price
                        OutlinedButton(
                            onClick = { overrideDialogOrder = order },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Adjust Bill", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Price Override Dialog
    if (overrideDialogOrder != null) {
        val ord = overrideDialogOrder!!
        var adjustedAmount by remember { mutableStateOf(ord.finalAmount.toString()) }
        var adjustReason by remember { mutableStateOf("Weight variance adjustment") }

        AlertDialog(
            onDismissRequest = { overrideDialogOrder = null },
            title = { Text("Manual Order Price Override", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Order #${ord.id} - Current: $${String.format(Locale.US, "%.2f", ord.finalAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )

                    OutlinedTextField(
                        value = adjustedAmount,
                        onValueChange = { adjustedAmount = it },
                        label = { Text("New Final Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = adjustReason,
                        onValueChange = { adjustReason = it },
                        label = { Text("Reason for Adjustment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = adjustedAmount.toDoubleOrNull() ?: ord.finalAmount
                        onOverridePrice(ord.id, parsed, adjustReason)
                        overrideDialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent)
                ) {
                    Text("Apply Adjustment", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { overrideDialogOrder = null }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }
}

@Composable
fun AdminCouponsTab(
    coupons: List<CouponEntity>,
    onSaveCoupon: (CouponEntity) -> Unit,
    onDeleteCoupon: (CouponEntity) -> Unit
) {
    var isNewCouponDialogOpen by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Discount Coupons",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )

                Button(
                    onClick = { isNewCouponDialogOpen = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Promo", fontWeight = FontWeight.Bold)
                }
            }
        }

        items(coupons) { coupon ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = coupon.code,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TealPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (coupon.isActive) StatusSuccessBg else AmberContainer,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = if (coupon.isActive) "ACTIVE" else "DISABLED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (coupon.isActive) StatusSuccess else AmberAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(text = coupon.title, style = MaterialTheme.typography.bodyMedium, color = SlateTextPrimary)
                        Text(
                            text = "Value: ${if (coupon.isPercent) "${coupon.discountValue.toInt()}%" else "$${coupon.discountValue}"} • Min: $${coupon.minOrderValue.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = coupon.isActive,
                            onCheckedChange = { onSaveCoupon(coupon.copy(isActive = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                        )
                        IconButton(onClick = { onDeleteCoupon(coupon) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusError)
                        }
                    }
                }
            }
        }
    }

    if (isNewCouponDialogOpen) {
        var cCode by remember { mutableStateOf("") }
        var cTitle by remember { mutableStateOf("") }
        var cVal by remember { mutableStateOf("20") }
        var cMin by remember { mutableStateOf("25") }
        var cIsPercent by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { isNewCouponDialogOpen = false },
            title = { Text("Create Promo Coupon", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = cCode,
                        onValueChange = { cCode = it.uppercase() },
                        label = { Text("Coupon Code (e.g. SUMMER30)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cTitle,
                        onValueChange = { cTitle = it },
                        label = { Text("Promo Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cVal,
                        onValueChange = { cVal = it },
                        label = { Text("Discount Value (% or $)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cMin,
                        onValueChange = { cMin = it },
                        label = { Text("Minimum Order Value ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cCode.isNotBlank()) {
                            onSaveCoupon(
                                CouponEntity(
                                    code = cCode,
                                    title = cTitle,
                                    description = "Special promotional offer code",
                                    isPercent = cIsPercent,
                                    discountValue = cVal.toDoubleOrNull() ?: 10.0,
                                    minOrderValue = cMin.toDoubleOrNull() ?: 20.0,
                                    isActive = true
                                )
                            )
                            isNewCouponDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Create Coupon", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isNewCouponDialogOpen = false }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }
}
