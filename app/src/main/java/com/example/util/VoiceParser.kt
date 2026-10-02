package com.example.util

import com.example.data.model.ExpenseCategory
import com.example.data.model.IncomeCategory
import com.example.data.model.TransactionType

data class ParsedCandidate(
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val description: String
)

object VoiceParser {
    /**
     * Parses natural language sentences in Urdu, Roman Urdu, or English.
     * Example: "Aj 1500 rupay kamaye aur 300 rupay petrol par kharch kiye"
     */
    fun parseInput(input: String): List<ParsedCandidate> {
        val candidates = mutableListOf<ParsedCandidate>()
        val lowercase = input.lowercase().trim()

        // Split by clauses (aur, and, +, comma, etc.)
        val clauses = lowercase.split(Regex("(aur|and|\\+|،|,|phir|also)"))

        for (clause in clauses) {
            val trimmed = clause.trim()
            if (trimmed.isEmpty()) continue

            // Find number amount in clause
            val numberMatch = Regex("(\\d+([.,]\\d+)?)").find(trimmed) ?: continue
            val amount = numberMatch.value.replace(",", "").toDoubleOrNull() ?: continue
            if (amount <= 0.0) continue

            // Check if Income
            val isIncome = trimmed.contains("kamaye") ||
                    trimmed.contains("income") ||
                    trimmed.contains("salary") ||
                    trimmed.contains("aaye") ||
                    trimmed.contains("mila") ||
                    trimmed.contains("received") ||
                    trimmed.contains("کمائے") ||
                    trimmed.contains("آمدنی")

            if (isIncome) {
                val category = when {
                    trimmed.contains("salary") || trimmed.contains("تنخواہ") -> IncomeCategory.SALARY.name
                    trimmed.contains("shop") || trimmed.contains("business") || trimmed.contains("دکان") -> IncomeCategory.BUSINESS.name
                    trimmed.contains("freelance") || trimmed.contains("upwork") || trimmed.contains("fiverr") -> IncomeCategory.FREELANCING.name
                    trimmed.contains("delivery") || trimmed.contains("bykea") || trimmed.contains("indrive") -> IncomeCategory.DELIVERY.name
                    trimmed.contains("pocket") || trimmed.contains("جیب خرچ") -> IncomeCategory.POCKET_MONEY.name
                    else -> IncomeCategory.OTHER.name
                }
                candidates.add(
                    ParsedCandidate(
                        type = TransactionType.INCOME,
                        amount = amount,
                        category = category,
                        description = input.take(60)
                    )
                )
            } else {
                // By default assume expense if "kharch", "diye", "spent", "paid", or general purchase
                val category = when {
                    trimmed.contains("petrol") || trimmed.contains("fuel") || trimmed.contains("پیٹرول") -> ExpenseCategory.PETROL.name
                    trimmed.contains("chai") || trimmed.contains("samosa") || trimmed.contains("khana") ||
                            trimmed.contains("food") || trimmed.contains("ration") || trimmed.contains("roti") -> ExpenseCategory.FOOD.name
                    trimmed.contains("bijli") || trimmed.contains("electricity") || trimmed.contains("lesco") || trimmed.contains("kelectric") -> ExpenseCategory.ELECTRICITY.name
                    trimmed.contains("gas") || trimmed.contains("sngpl") || trimmed.contains("گیس") -> ExpenseCategory.GAS.name
                    trimmed.contains("net") || trimmed.contains("internet") || trimmed.contains("wifi") || trimmed.contains("ptcl") -> ExpenseCategory.INTERNET.name
                    trimmed.contains("rent") || trimmed.contains("kiraya") || trimmed.contains("کرایہ") -> ExpenseCategory.RENT.name
                    trimmed.contains("bus") || trimmed.contains("rickshaw") || trimmed.contains("uber") || trimmed.contains("travel") -> ExpenseCategory.TRANSPORT.name
                    trimmed.contains("dawa") || trimmed.contains("medicine") || trimmed.contains("doctor") || trimmed.contains("medical") -> ExpenseCategory.MEDICAL.name
                    trimmed.contains("load") || trimmed.contains("mobile") || trimmed.contains("jazz") || trimmed.contains("telenor") -> ExpenseCategory.MOBILE.name
                    else -> ExpenseCategory.OTHER.name
                }
                candidates.add(
                    ParsedCandidate(
                        type = TransactionType.EXPENSE,
                        amount = amount,
                        category = category,
                        description = input.take(60)
                    )
                )
            }
        }

        return candidates
    }
}
