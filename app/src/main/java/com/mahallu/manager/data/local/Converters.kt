package com.mahallu.manager.data.local

import androidx.room.TypeConverter
import com.mahallu.manager.data.local.entity.*
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }

    @TypeConverter
    fun fromFamilyStatus(status: FamilyStatus): String {
        return status.name
    }

    @TypeConverter
    fun toFamilyStatus(value: String): FamilyStatus {
        return FamilyStatus.valueOf(value)
    }

    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(value: String): Gender {
        return Gender.valueOf(value)
    }

    @TypeConverter
    fun fromMaritalStatus(status: MaritalStatus): String {
        return status.name
    }

    @TypeConverter
    fun toMaritalStatus(value: String): MaritalStatus {
        return MaritalStatus.valueOf(value)
    }

    @TypeConverter
    fun fromSubscriptionType(type: SubscriptionType): String {
        return type.name
    }

    @TypeConverter
    fun toSubscriptionType(value: String): SubscriptionType {
        return SubscriptionType.valueOf(value)
    }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String {
        return method.name
    }

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod {
        return PaymentMethod.valueOf(value)
    }

    @TypeConverter
    fun fromDonationPurpose(purpose: DonationPurpose): String {
        return purpose.name
    }

    @TypeConverter
    fun toDonationPurpose(value: String): DonationPurpose {
        return DonationPurpose.valueOf(value)
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactionCategory(category: TransactionCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTransactionCategory(value: String): TransactionCategory {
        return TransactionCategory.valueOf(value)
    }

    @TypeConverter
    fun fromWelfareCategory(category: WelfareCategory): String {
        return category.name
    }

    @TypeConverter
    fun toWelfareCategory(value: String): WelfareCategory {
        return WelfareCategory.valueOf(value)
    }

    @TypeConverter
    fun fromWelfareStatus(status: WelfareStatus): String {
        return status.name
    }

    @TypeConverter
    fun toWelfareStatus(value: String): WelfareStatus {
        return WelfareStatus.valueOf(value)
    }

    @TypeConverter
    fun fromBackupFrequency(frequency: BackupFrequency): String {
        return frequency.name
    }

    @TypeConverter
    fun toBackupFrequency(value: String): BackupFrequency {
        return BackupFrequency.valueOf(value)
    }

    @TypeConverter
    fun fromAppTheme(theme: AppTheme): String {
        return theme.name
    }

    @TypeConverter
    fun toAppTheme(value: String): AppTheme {
        return AppTheme.valueOf(value)
    }
}
