package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.PriceCalculationResult
import com.example.data.model.Role
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.BorderLight
import com.example.ui.theme.MintContainer
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorBg
import com.example.ui.theme.StatusProgress
import com.example.ui.theme.StatusProgressBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.NavTab
import java.util.Locale

@Composable
fun FreshFoldHeader(
    userName: String,
    location: String,
    activeRole: Role,
    unreadNotifCount: Int,
    onNotificationClick: () -> Unit,
    onRoleChange: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = TealPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Hello, $userName 👋",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Role switcher pill button
            Box {
                Surface(
                    onClick = { roleMenuExpanded = true },
                    shape = RoundedCornerShape(20.dp),
                    color = when (activeRole) {
                        Role.CUSTOMER -> TealContainer
                        Role.ADMIN -> AmberContainer
                        Role.DELIVERY_STAFF -> MintContainer
                    },
                    modifier = Modifier.testTag("role_switcher_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (activeRole) {
                                Role.CUSTOMER -> Icons.Default.Person
                                Role.ADMIN -> Icons.Default.AdminPanelSettings
                                Role.DELIVERY_STAFF -> Icons.Default.DeliveryDining
                            },
                            contentDescription = "Role",
                            modifier = Modifier.size(14.dp),
                            tint = when (activeRole) {
                                Role.CUSTOMER -> TealPrimary
                                Role.ADMIN -> AmberAccent
                                Role.DELIVERY_STAFF -> MintSecondary
                            }
                        )
                        Text(
                            text = when (activeRole) {
                                Role.CUSTOMER -> "Customer"
                                Role.ADMIN -> "Admin"
                                Role.DELIVERY_STAFF -> "Driver"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                    }
                }

                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Customer App") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary) },
                        onClick = {
                            roleMenuExpanded = false
                            onRoleChange(Role.CUSTOMER)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Admin Console") },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AmberAccent) },
                        onClick = {
                            roleMenuExpanded = false
                            onRoleChange(Role.ADMIN)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delivery Staff Mode") },
                        leadingIcon = { Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = MintSecondary) },
                        onClick = {
                            roleMenuExpanded = false
                            onRoleChange(Role.DELIVERY_STAFF)
                        }
                    )
                }
            }

            // Notification Bell
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, BorderLight, CircleShape)
                    .testTag("notification_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifCount > 0) {
                            Badge(containerColor = AmberAccent) {
                                Text(unreadNotifCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = SlateTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FreshFoldBottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .shadow(12.dp),
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(NavTab.HOME, "Home", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
            Triple(NavTab.ORDERS, "Orders", Pair(Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)),
            Triple(NavTab.OFFERS, "Offers", Pair(Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer)),
            Triple(NavTab.SUPPORT, "Support", Pair(Icons.Filled.HeadsetMic, Icons.Outlined.HeadsetMic)),
            Triple(NavTab.PROFILE, "Profile", Pair(Icons.Filled.Person, Icons.Outlined.Person))
        )

        items.forEach { (tab, label, icons) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer,
                    unselectedIconColor = SlateTextMuted,
                    unselectedTextColor = SlateTextMuted
                ),
                modifier = Modifier.testTag("nav_tab_${label.lowercase()}")
            )
        }
    }
}

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text) = when (status) {
        OrderStatus.PLACED -> Triple(StatusProgressBg, StatusProgress, "Order Placed")
        OrderStatus.CONFIRMED -> Triple(TealContainer, TealPrimary, "Confirmed")
        OrderStatus.PICKED_UP -> Triple(MintContainer, MintSecondary, "Picked Up")
        OrderStatus.WASHING -> Triple(StatusProgressBg, StatusProgress, "Washing")
        OrderStatus.IRONING -> Triple(AmberContainer, AmberAccent, "Steam Ironing")
        OrderStatus.QUALITY_CHECK -> Triple(TealContainer, TealPrimary, "Quality Check")
        OrderStatus.READY -> Triple(MintContainer, MintSecondary, "Ready")
        OrderStatus.OUT_FOR_DELIVERY -> Triple(AmberContainer, AmberAccent, "Out for Delivery")
        OrderStatus.DELIVERED -> Triple(StatusSuccessBg, StatusSuccess, "Delivered")
        OrderStatus.CANCELLED -> Triple(StatusErrorBg, StatusError, "Cancelled")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriceBreakdownCard(
    calculation: PriceCalculationResult,
    hasCouponApplied: Boolean = false,
    couponCode: String? = null,
    onRemoveCoupon: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Bill Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )

            PriceRow(
                label = "Service Charges",
                amount = calculation.serviceCharges
            )

            if (calculation.additionalCharges > 0.0) {
                PriceRow(
                    label = "Pickup Fee",
                    amount = calculation.additionalCharges
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pickup Fee",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess
                    )
                }
            }

            if (calculation.expressCharges > 0.0) {
                PriceRow(
                    label = "Express Priority Charge",
                    amount = calculation.expressCharges,
                    highlight = true
                )
            }

            if (calculation.deliveryCharges == 0.0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Delivery Fee",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                    Text(
                        text = "FREE (Above $30)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess
                    )
                }
            } else {
                PriceRow(
                    label = "Delivery Fee",
                    amount = calculation.deliveryCharges
                )
            }

            if (hasCouponApplied && calculation.discount > 0.0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Coupon Discount (${couponCode ?: "APPLIED"})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusSuccess,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (onRemoveCoupon != null) {
                            Text(
                                text = " [Remove]",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusError,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable { onRemoveCoupon() }
                            )
                        }
                    }
                    Text(
                        text = "-$${String.format(Locale.US, "%.2f", calculation.discount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess
                    )
                }
            }

            PriceRow(
                label = "Taxes & Eco Surcharge (8.25%)",
                amount = calculation.tax
            )

            HorizontalDivider(color = BorderLight, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Final Total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "All inclusive of taxes",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextMuted
                    )
                }

                Text(
                    text = "$${String.format(Locale.US, "%.2f", calculation.finalTotal)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    amount: Double,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) AmberAccent else SlateTextSecondary,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = "$${String.format(Locale.US, "%.2f", amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) AmberAccent else SlateTextPrimary
        )
    }
}
