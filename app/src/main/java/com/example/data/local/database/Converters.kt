package com.example.data.local.database

import androidx.room.TypeConverter
import com.example.data.model.DeliveryPreference
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.PricingType
import com.example.data.model.Role

class Converters {
    @TypeConverter
    fun fromRole(role: Role): String = role.name

    @TypeConverter
    fun toRole(value: String): Role = try { Role.valueOf(value) } catch (e: Exception) { Role.CUSTOMER }

    @TypeConverter
    fun fromPricingType(type: PricingType): String = type.name

    @TypeConverter
    fun toPricingType(value: String): PricingType = try { PricingType.valueOf(value) } catch (e: Exception) { PricingType.PER_KG }

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String = status.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = try { OrderStatus.valueOf(value) } catch (e: Exception) { OrderStatus.PLACED }

    @TypeConverter
    fun fromDeliveryPreference(pref: DeliveryPreference): String = pref.name

    @TypeConverter
    fun toDeliveryPreference(value: String): DeliveryPreference = try { DeliveryPreference.valueOf(value) } catch (e: Exception) { DeliveryPreference.STANDARD }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try { PaymentMethod.valueOf(value) } catch (e: Exception) { PaymentMethod.UPI }

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = try { PaymentStatus.valueOf(value) } catch (e: Exception) { PaymentStatus.PENDING }
}
