package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

enum class ExpenseCategory(val displayNameEn: String, val displayNameUr: String, val iconName: String) {
    FOOD("Food & Grocery", "کھانا پینا اور راشن", "Restaurant"),
    TRANSPORT("Transport", "ٹرانسپورٹ", "DirectionsBus"),
    PETROL("Petrol / Fuel", "پیٹرول اور ایندھن", "LocalGasStation"),
    SHOPPING("Shopping", "خریداری", "ShoppingBag"),
    ELECTRICITY("Electricity Bill", "بجلی کا بل", "ElectricBolt"),
    GAS("Gas Bill", "گیس کا بل", "LocalFireDepartment"),
    INTERNET("Internet & Cable", "انٹرنیٹ اور کیبل", "Wifi"),
    EDUCATION("Education / Fees", "تعلیم اور فیس", "School"),
    MEDICAL("Medical & Health", "دوا اور صحت", "LocalHospital"),
    RENT("House / Shop Rent", "گھر یا دکان کا کرایہ", "Home"),
    MOBILE("Mobile Load / Bill", "موبائل بیلنس اور بل", "PhoneAndroid"),
    MAINTENANCE("Home Maintenance", "گھر کی مرمت", "Build"),
    LOAN("Loan Repayment", "قرض یا کمیٹی", "AccountBalance"),
    CHARITY("Sadqah & Charity", "صدقہ اور خیرات", "VolunteerActivism"),
    OTHER("Other Expense", "دیگر اخراجات", "Category");

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

enum class IncomeCategory(val displayNameEn: String, val displayNameUr: String, val iconName: String) {
    SALARY("Salary", "تنخواہ", "Work"),
    BUSINESS("Business / Shop", "کاروبار یا دکان", "Store"),
    DELIVERY("Delivery / Ride", "ڈیلیوری یا رائیڈ", "TwoWheeler"),
    FREELANCING("Freelancing / IT", "آن لائن کام اور فری لانسنگ", "LaptopMac"),
    POCKET_MONEY("Pocket Money", "جیب خرچ", "Savings"),
    RENTAL_INCOME("Rental Income", "کرایہ کی آمدنی", "Apartment"),
    INVESTMENT("Profit / Investment", "منافع یا انویسٹمنٹ", "TrendingUp"),
    OTHER("Other Income", "دیگر آمدنی", "AttachMoney");

    companion object {
        fun fromString(value: String): IncomeCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Cash" // Cash, EasyPaisa, JazzCash, Bank, Raast
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val monthYear: String, // e.g. "2026-10"
    val monthlyBudget: Double,
    val categoryBudgetsJson: String = "{}"
)

@Entity(tableName = "bill_reminders")
data class BillReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDate: Long,
    val category: String = "Electricity",
    val notes: String = "",
    val isPaid: Boolean = false,
    val paidDate: Long? = null
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

@Entity(tableName = "daily_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dueDate: Long = System.currentTimeMillis(),
    val timeString: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM
)

enum class DocumentType(val displayNameEn: String, val displayNameUr: String) {
    CNIC("National ID (CNIC)", "شناختی کارڈ"),
    DRIVING_LICENSE("Driving License", "ڈرائیونگ لائسنس"),
    VEHICLE_PAPERS("Vehicle / Bike Papers", "گاڑی یا موٹرسائیکل کاغذات"),
    PASSPORT("Passport", "پاسپورٹ"),
    DOMICILE("Domicile Certificate", "ڈومیسائل"),
    INSURANCE("Insurance Policy", "انشورنس پالیسی"),
    OTHER("Other Document", "دیگر دستاویزات");

    companion object {
        fun fromString(value: String): DocumentType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

@Entity(tableName = "document_reminders")
data class DocumentReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val docType: DocumentType,
    val docNumber: String = "",
    val expiryDate: Long,
    val notes: String = "",
    val reminderDaysBefore: Int = 30
)
