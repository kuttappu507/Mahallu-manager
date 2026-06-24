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
    fun toFamilyStatus(value: String): FamilyStatus = enumValueOf(value)

    @TypeConverter
    fun fromGender(gender: Gender): String = gender.name

    @TypeConverter
    fun toGender(value: String): Gender = enumValueOf(value)

    @TypeConverter
    fun fromMaritalStatus(status: MaritalStatus): String = status.name

    @TypeConverter
    fun toMaritalStatus(value: String): MaritalStatus = enumValueOf(value)

    @TypeConverter
    fun fromMemberStatus(status: MemberStatus): String = status.name

    @TypeConverter
    fun toMemberStatus(value: String): MemberStatus = enumValueOf(value)

    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = enumValueOf(value)

    @TypeConverter
    fun fromSubscriptionType(type: SubscriptionType): String = type.name

    @TypeConverter
    fun toSubscriptionType(value: String): SubscriptionType = enumValueOf(value)

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = enumValueOf(value)

    @TypeConverter
    fun fromDonationPurpose(purpose: DonationPurpose): String = purpose.name

    @TypeConverter
    fun toDonationPurpose(value: String): DonationPurpose = enumValueOf(value)

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = enumValueOf(value)

    @TypeConverter
    fun fromFinanceCategory(category: FinanceCategory): String = category.name

    @TypeConverter
    fun toFinanceCategory(value: String): FinanceCategory = enumValueOf(value)

    @TypeConverter
    fun fromReferenceType(type: ReferenceType): String = type.name

    @TypeConverter
    fun toReferenceType(value: String): ReferenceType = enumValueOf(value)

    @TypeConverter
    fun fromWelfareType(type: WelfareType): String = type.name

    @TypeConverter
    fun toWelfareType(value: String): WelfareType = enumValueOf(value)

    @TypeConverter
    fun fromWelfareStatus(status: WelfareStatus): String = status.name

    @TypeConverter
    fun toWelfareStatus(value: String): WelfareStatus = enumValueOf(value)

    @TypeConverter
    fun fromBackupFrequency(frequency: BackupFrequency): String = frequency.name

    @TypeConverter
    fun toBackupFrequency(value: String): BackupFrequency = enumValueOf(value)
}
