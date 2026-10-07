package com.example.ui.screens.profile

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.Role
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
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary

@Composable
fun ProfileScreen(
    user: UserEntity,
    activeRole: Role,
    onRoleChange: (Role) -> Unit,
    onOpenAddressesClick: () -> Unit,
    onOpenNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = user.mobile,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextMuted
                        )
                    }
                }
            }
        }

        // Role Switcher Section (Critical for seamless switching between Customer, Admin, and Delivery Staff)
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
                        text = "App Mode / Role Switcher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Switch between customer booking, administrative price & order management, or delivery driver view.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Customer
                        OutlinedButton(
                            onClick = { onRoleChange(Role.CUSTOMER) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (activeRole == Role.CUSTOMER) ButtonDefaults.buttonColors(containerColor = TealContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                "Customer",
                                fontWeight = FontWeight.Bold,
                                color = if (activeRole == Role.CUSTOMER) TealPrimary else SlateTextSecondary
                            )
                        }

                        // Admin
                        OutlinedButton(
                            onClick = { onRoleChange(Role.ADMIN) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (activeRole == Role.ADMIN) ButtonDefaults.buttonColors(containerColor = AmberContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                "Admin",
                                fontWeight = FontWeight.Bold,
                                color = if (activeRole == Role.ADMIN) AmberAccent else SlateTextSecondary
                            )
                        }

                        // Delivery Staff
                        OutlinedButton(
                            onClick = { onRoleChange(Role.DELIVERY_STAFF) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (activeRole == Role.DELIVERY_STAFF) ButtonDefaults.buttonColors(containerColor = MintContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                "Driver",
                                fontWeight = FontWeight.Bold,
                                color = if (activeRole == Role.DELIVERY_STAFF) MintSecondary else SlateTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Settings Items
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ProfileMenuRow(
                        title = "Saved Pickup Addresses",
                        subtitle = "Manage Home, Office, and frequent addresses",
                        icon = Icons.Default.LocationOn,
                        onClick = onOpenAddressesClick
                    )
                    HorizontalDivider(color = BorderLight)
                    ProfileMenuRow(
                        title = "Notifications & Alerts",
                        subtitle = "Pickup reminders, status changes & offers",
                        icon = Icons.Default.Notifications,
                        onClick = onOpenNotificationsClick
                    )
                    HorizontalDivider(color = BorderLight)
                    ProfileMenuRow(
                        title = "Security & Privacy",
                        subtitle = "Data encryption & authentication settings",
                        icon = Icons.Default.Security,
                        onClick = {
                            Toast.makeText(context, "All user data encrypted and stored locally.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TealContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = SlateTextSecondary)
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = SlateTextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun NotificationScreen(
    notifications: List<NotificationEntity>,
    onBackClick: () -> Unit,
    onMarkAllReadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SlateTextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
            }

            IconButton(onClick = onMarkAllReadClick) {
                Icon(Icons.Default.DoneAll, contentDescription = "Mark All Read", tint = TealPrimary)
            }
        }

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications yet", color = SlateTextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "ORDER" -> TealContainer
                                            "DELIVERY" -> MintContainer
                                            "OFFER" -> AmberContainer
                                            else -> BackgroundClean
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "ORDER" -> Icons.Default.Check
                                        "DELIVERY" -> Icons.Default.DeliveryDining
                                        "OFFER" -> Icons.Default.Star
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "ORDER" -> TealPrimary
                                        "DELIVERY" -> MintSecondary
                                        "OFFER" -> AmberAccent
                                        else -> SlateTextSecondary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodyMedium,
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
fun ReviewExperienceDialog(
    orderId: String,
    onDismiss: () -> Unit,
    onSubmit: (serviceRating: Int, deliveryRating: Int, comment: String) -> Unit
) {
    var serviceStars by remember { mutableIntStateOf(5) }
    var deliveryStars by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "How was your laundry experience?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Order #$orderId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )

                // Service rating
                Column {
                    Text("Cleanliness & Fabric Care:", style = MaterialTheme.typography.labelMedium, color = SlateTextPrimary)
                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { serviceStars = i }) {
                                Icon(
                                    imageVector = if (i <= serviceStars) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (i <= serviceStars) AmberAccent else SlateTextMuted
                                )
                            }
                        }
                    }
                }

                // Delivery rating
                Column {
                    Text("Valet & Delivery Punctuality:", style = MaterialTheme.typography.labelMedium, color = SlateTextPrimary)
                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { deliveryStars = i }) {
                                Icon(
                                    imageVector = if (i <= deliveryStars) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (i <= deliveryStars) AmberAccent else SlateTextMuted
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = reviewComment,
                    onValueChange = { reviewComment = it },
                    label = { Text("Comments (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(serviceStars, deliveryStars, reviewComment)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextSecondary)
            }
        }
    )
}
