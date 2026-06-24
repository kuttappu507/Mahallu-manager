package com.mahallu.core.database

import androidx.room.TypeConverter
import com.mahallu.core.database.entity.*

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name
    
    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)
    
    @TypeConverter
    fun fromFamilyStatus(value: FamilyStatus): String = value.name
    
    @TypeConverter
    fun toFamilyStatus(value: String): FamilyStatus = FamilyStatus.valueOf(value)
    
    @TypeConverter
    fun fromGender(value: Gender): String = value.name
    
    @TypeConverter
    fun toGender(value: String): Gender = Gender.valueOf(value)
    
    @TypeConverter
    fun fromMaritalStatus(value: MaritalStatus): String = value.name
    
    @TypeConverter
    fun toMaritalStatus(value: String): MaritalStatus = MaritalStatus.valueOf(value)
    
    @TypeConverter
    fun fromSubscriptionType(value: SubscriptionType): String = value.name
    
    @TypeConverter
    fun toSubscriptionType(value: String): SubscriptionType = SubscriptionType.valueOf(value)
    
    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name
    
    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = PaymentMethod.valueOf(value)
    
    @TypeConverter
    fun fromDonationPurpose(value: DonationPurpose): String = value.name
    
    @TypeConverter
    fun toDonationPurpose(value: String): DonationPurpose = DonationPurpose.valueOf(value)
    
    @TypeConverter
    fun fromIncomeCategory(value: IncomeCategory): String = value.name
    
    @TypeConverter
    fun toIncomeCategory(value: String): IncomeCategory = IncomeCategory.valueOf(value)
    
    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name
    
    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)
    
    @TypeConverter
    fun fromWelfareCategory(value: WelfareCategory): String = value.name
    
    @TypeConverter
    fun toWelfareCategory(value: String): WelfareCategory = WelfareCategory.valueOf(value)
    
    @TypeConverter
    fun fromApprovalStatus(value: ApprovalStatus): String = value.name
    
    @TypeConverter
    fun toApprovalStatus(value: String): ApprovalStatus = ApprovalStatus.valueOf(value)
    
    @TypeConverter
    fun fromBackupType(value: BackupType): String = value.name
    
    @TypeConverter
    fun toBackupType(value: String): BackupType = BackupType.valueOf(value)
    
    @TypeConverter
    fun fromBackupStatus(value: BackupStatus): String = value.name
    
    @TypeConverter
    fun toBackupStatus(value: String): BackupStatus = BackupStatus.valueOf(value)
}
