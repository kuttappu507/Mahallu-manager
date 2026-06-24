package com.mahallu.core.database

import androidx.room.TypeConverter
import com.mahallu.core.database.entity.*
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromFamilyStatus(status: FamilyStatus): String = status.name

    @TypeConverter
    fun toFamilyStatus(value: String): FamilyStatus = FamilyStatus.valueOf(value)

    @TypeConverter
    fun fromMemberStatus(status: MemberStatus): String = status.name

    @TypeConverter
    fun toMemberStatus(value: String): MemberStatus = MemberStatus.valueOf(value)

    @TypeConverter
    fun fromGender(gender: Gender): String = gender.name

    @TypeConverter
    fun toGender(value: String): Gender = Gender.valueOf(value)

    @TypeConverter
    fun fromMaritalStatus(status: MaritalStatus): String = status.name

    @TypeConverter
    fun toMaritalStatus(value: String): MaritalStatus = MaritalStatus.valueOf(value)

    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter
    fun fromSubscriptionType(type: SubscriptionType): String = type.name

    @TypeConverter
    fun toSubscriptionType(value: String): SubscriptionType = SubscriptionType.valueOf(value)

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = PaymentMethod.valueOf(value)

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = PaymentStatus.valueOf(value)

    @TypeConverter
    fun fromDonationPurpose(purpose: DonationPurpose): String = purpose.name

    @TypeConverter
    fun toDonationPurpose(value: String): DonationPurpose = DonationPurpose.valueOf(value)

    @TypeConverter
    fun fromFinanceType(type: FinanceType): String = type.name

    @TypeConverter
    fun toFinanceType(value: String): FinanceType = FinanceType.valueOf(value)

    @TypeConverter
    fun fromFinanceCategory(category: FinanceCategory): String = category.name

    @TypeConverter
    fun toFinanceCategory(value: String): FinanceCategory = FinanceCategory.valueOf(value)

    @TypeConverter
    fun fromWelfareCategory(category: WelfareCategory): String = category.name

    @TypeConverter
    fun toWelfareCategory(value: String): WelfareCategory = WelfareCategory.valueOf(value)

    @TypeConverter
    fun fromWelfareStatus(status: WelfareStatus): String = status.name

    @TypeConverter
    fun toWelfareStatus(value: String): WelfareStatus = WelfareStatus.valueOf(value)
}
