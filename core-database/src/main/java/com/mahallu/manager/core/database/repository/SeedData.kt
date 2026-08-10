package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.DonationDao
import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.dao.SettingsDao
import com.mahallu.manager.core.database.dao.SubscriptionDao
import com.mahallu.manager.core.database.dao.UserDao
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.entity.SettingsEntity
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.entity.UserEntity
import com.mahallu.manager.core.security.PasswordHasher
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedData @Inject constructor(
    private val userDao: UserDao,
    private val familyDao: FamilyDao,
    private val memberDao: MemberDao,
    private val donationDao: DonationDao,
    private val financeDao: FinanceDao,
    private val subscriptionDao: SubscriptionDao,
    private val settingsDao: SettingsDao
) {
    suspend fun seedIfEmpty() {
        seedAdminUser()
        seedSettings()
        if (familyDao.count() == 0) seedFamiliesAndMembers()
    }

    private suspend fun seedAdminUser() {
        if (userDao.getByUsername("admin") != null) return
        userDao.insert(
            UserEntity(
                id = "user-admin-001",
                username = "admin",
                passwordHash = PasswordHasher.hash("admin123"),
                fullName = "System Administrator",
                role = "ADMINISTRATOR",
                phone = "+91 9876543210",
                email = "admin@mahallu.app"
            )
        )
        // Also seed a secretary account for demo
        userDao.insert(
            UserEntity(
                id = "user-secretary-001",
                username = "secretary",
                passwordHash = PasswordHasher.hash("secretary123"),
                fullName = "Shahid K",
                role = "SECRETARY",
                phone = "+91 9876501234",
                email = "secretary@mahallu.app"
            )
        )
        userDao.insert(
            UserEntity(
                id = "user-treasurer-001",
                username = "treasurer",
                passwordHash = PasswordHasher.hash("treasurer123"),
                fullName = "Ibrahim Kutty",
                role = "TREASURER",
                phone = "+91 9876512345",
                email = "treasurer@mahallu.app"
            )
        )
    }

    private suspend fun seedSettings() {
        if (settingsDao.get("mahallu.name") != null) return
        val defaults = listOf(
            "mahallu.name" to "Al Noor Mahallu",
            "mahallu.address" to "Green View Street, North Area, Malappuram, Kerala - 676505",
            "mahallu.phone" to "+91 483 2731000",
            "mahallu.email" to "info@alnoormahallu.org",
            "mahallu.established" to "1992",
            "mahallu.registration_no" to "MPM/MHL/1992/0042",
            "default_subscription_amount" to "500",
            "currency_symbol" to "₹",
            "theme_mode" to "system",
            "backup.auto_enabled" to "true",
            "backup.retention_days" to "30",
            "backup.last_at" to "0",
            "drive.folder_id" to ""
        )
        defaults.forEach { (k, v) ->
            settingsDao.upsert(SettingsEntity(k, v))
        }
    }

    private suspend fun seedFamiliesAndMembers() {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000
        val year = 365L * day

        val families = listOf(
            FamilyEntity(
                id = "fam-1001", familyNumber = "FAM-1001",
                houseName = "Abdul Rahman House", houseNumber = "12",
                ward = "Ward 3", area = "North Area",
                address = "House No: 12, Green View Street, North Area",
                pincode = "676505", primaryMobile = "+91 9876543210",
                status = "ACTIVE"
            ),
            FamilyEntity(
                id = "fam-1002", familyNumber = "FAM-1002",
                houseName = "Ibrahim House", houseNumber = "24",
                ward = "Ward 2", area = "Palm Avenue",
                address = "House No: 24, Palm Avenue, East Area",
                pincode = "676505", primaryMobile = "+91 9876501234",
                status = "ACTIVE"
            ),
            FamilyEntity(
                id = "fam-1003", familyNumber = "FAM-1003",
                houseName = "Mohammed Ali House", houseNumber = "7",
                ward = "Ward 1", area = "Sunset Road",
                address = "House No: 7, Sunset Road, West Area",
                pincode = "676505", primaryMobile = "+91 9876512345",
                status = "ACTIVE"
            ),
            FamilyEntity(
                id = "fam-1004", familyNumber = "FAM-1004",
                houseName = "Hassan House", houseNumber = "18",
                ward = "Ward 4", area = "Beach Road",
                address = "House No: 18, Beach Road, South Area",
                pincode = "676505", primaryMobile = "+91 9876523456",
                status = "ACTIVE"
            ),
            FamilyEntity(
                id = "fam-1005", familyNumber = "FAM-1005",
                houseName = "Ismail House", houseNumber = "31",
                ward = "Ward 3", area = "Hill Top Road",
                address = "House No: 31, Hill Top Road, North Area",
                pincode = "676505", primaryMobile = "+91 9876534567",
                status = "ACTIVE"
            )
        )
        familyDao.upsertAll(families)

        val members = mutableListOf<MemberEntity>()
        fun dob(yearsAgo: Int) = now - yearsAgo * year

        // Family 1 - 4 members
        members += MemberEntity("mem-5421", "MEM-5421", "fam-1001", "Muhammed Safwan", "MALE",
            dob(26), "Business", "B.Com", "B+", "MARRIED",
            "+91 9876543210", null, "Indian", null, null, null, "HEAD")
        members += MemberEntity("mem-5422", "MEM-5422", "fam-1001", "Fathima Safwana", "FEMALE",
            dob(24), "Teacher", "B.Ed", "A+", "MARRIED",
            "+91 9876543211", null, "Indian", null, null, null, "SPOUSE")
        members += MemberEntity("mem-5423", "MEM-5423", "fam-1001", "Ayaan Safwan", "MALE",
            dob(5), "Student", null, "O+", "SINGLE",
            null, null, "Indian", null, null, null, "SON")
        members += MemberEntity("mem-5424", "MEM-5424", "fam-1001", "Mariam Safwana", "FEMALE",
            dob(3), "Student", null, null, "SINGLE",
            null, null, "Indian", null, null, null, "DAUGHTER")

        // Family 2 - 3 members
        members += MemberEntity("mem-5501", "MEM-5501", "fam-1002", "Ibrahim Kutty", "MALE",
            dob(55), "Retired", "SSLC", "A+", "MARRIED",
            "+91 9876501234", null, "Indian", null, null, null, "HEAD")
        members += MemberEntity("mem-5502", "MEM-5502", "fam-1002", "Khadeeja Beevi", "FEMALE",
            dob(50), "Homemaker", "SSLC", "B+", "MARRIED",
            "+91 9876501235", null, "Indian", null, null, null, "SPOUSE")
        members += MemberEntity("mem-5503", "MEM-5503", "fam-1002", "Naseer Ahmed", "MALE",
            dob(28), "Engineer", "B.Tech", "O+", "SINGLE",
            "+91 9876501236", null, "Indian", null, null, null, "SON")

        // Family 3 - 3 members
        members += MemberEntity("mem-5601", "MEM-5601", "fam-1003", "Mohammed Ali", "MALE",
            dob(60), "Business", "Plus Two", "AB+", "MARRIED",
            "+91 9876512345", null, "Indian", null, null, null, "HEAD")
        members += MemberEntity("mem-5602", "MEM-5602", "fam-1003", "Rukhiya Beevi", "FEMALE",
            dob(55), "Homemaker", "SSLC", "A+", "MARRIED",
            "+91 9876512346", null, "Indian", null, null, null, "SPOUSE")
        members += MemberEntity("mem-5603", "MEM-5603", "fam-1003", "Salman Ali", "MALE",
            dob(30), "Doctor", "MBBS", "B+", "MARRIED",
            "+91 9876512347", null, "Indian", null, null, null, "SON")

        // Family 4 - 4 members
        members += MemberEntity("mem-5701", "MEM-5701", "fam-1004", "Hassan Khan", "MALE",
            dob(45), "Teacher", "M.A.", "O+", "MARRIED",
            "+91 9876523456", null, "Indian", null, null, null, "HEAD")
        members += MemberEntity("mem-5702", "MEM-5702", "fam-1004", "Sumayya Khan", "FEMALE",
            dob(40), "Teacher", "B.Ed", "A+", "MARRIED",
            "+91 9876523457", null, "Indian", null, null, null, "SPOUSE")
        members += MemberEntity("mem-5703", "MEM-5703", "fam-1004", "Rayyan Khan", "MALE",
            dob(15), "Student", null, "O+", "SINGLE",
            null, null, "Indian", null, null, null, "SON")
        members += MemberEntity("mem-5704", "MEM-5704", "fam-1004", "Zara Khan", "FEMALE",
            dob(12), "Student", null, null, "SINGLE",
            null, null, "Indian", null, null, null, "DAUGHTER")

        // Family 5 - 3 members
        members += MemberEntity("mem-5801", "MEM-5801", "fam-1005", "Ismail Rawther", "MALE",
            dob(70), "Retired", "SSLC", "B+", "MARRIED",
            "+91 9876534567", null, "Indian", null, null, null, "HEAD")
        members += MemberEntity("mem-5802", "MEM-5802", "fam-1005", "Fathima Ismail", "FEMALE",
            dob(65), "Homemaker", null, "O+", "MARRIED",
            null, null, "Indian", null, null, null, "SPOUSE")
        members += MemberEntity("mem-5803", "MEM-5803", "fam-1005", "Ashraf Ismail", "MALE",
            dob(35), "Shopkeeper", "Plus Two", "A+", "MARRIED",
            "+91 9876534568", null, "Indian", null, null, null, "SON")

        memberDao.upsertAll(members)

        // Donations
        val cal = Calendar.getInstance()
        val donations = listOf(
            DonationEntity("don-001", "DON-2025-00056", "Abdul Rahman", "+91 9876543210", "fam-1001",
                2000.0, "MASJID", "Friday Jumu'ah", now - 7 * day, "UPI", "UPI12345", null, "admin"),
            DonationEntity("don-002", "DON-2025-00055", "Ibrahim Kutty", "+91 9876501234", "fam-1002",
                5000.0, "BUILDING", "Building fund", now - 14 * day, "BANK", "TXN5678", null, "admin"),
            DonationEntity("don-003", "DON-2025-00054", "Naseer Ahmed", "+91 9876501236", "fam-1002",
                1000.0, "GENERAL", "General", now - 21 * day, "CASH", null, null, "admin"),
            DonationEntity("don-004", "DON-2025-00053", "Fathima Beevi", "+91 9876512346", "fam-1003",
                3000.0, "WELFARE", "Welfare fund", now - 30 * day, "UPI", "UPI9999", null, "admin"),
            DonationEntity("don-005", "DON-2025-00052", "Salman Ali", "+91 9876512347", "fam-1003",
                7500.0, "EDUCATION", "Education fund", now - 45 * day, "BANK", "TXN1111", null, "admin")
        )
        donationDao.upsertAll(donations)
        financeDao.upsertAll(donations.map { financeEntryFromDonation(it) })

        // Subscriptions for current month
        val subs = listOf(
            SubscriptionEntity("sub-001", "RCP-2025-00123", "fam-1001", "mem-5421",
                "MONTHLY", 500.0, now - 2 * day, "CASH", null, "Monthly subscription", "admin",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1),
            SubscriptionEntity("sub-002", "RCP-2025-00124", "fam-1002", "mem-5501",
                "MONTHLY", 500.0, now - 5 * day, "UPI", "UPI/1234", null, "admin",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1),
            SubscriptionEntity("sub-003", "RCP-2025-00125", "fam-1003", "mem-5601",
                "MONTHLY", 500.0, now - 7 * day, "CASH", null, null, "admin",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1),
            SubscriptionEntity("sub-004", "RCP-2025-00126", "fam-1004", "mem-5701",
                "QUARTERLY", 1500.0, now - 10 * day, "BANK", "TXN8888", null, "admin",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1),
            SubscriptionEntity("sub-005", "RCP-2025-00127", "fam-1005", "mem-5801",
                "MONTHLY", 500.0, now - 12 * day, "CASH", null, null, "admin",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        )
        subscriptionDao.upsertAll(subs)
        financeDao.upsertAll(subs.map { financeEntryFromSubscription(it) })
    }
}