package com.cebolao.app.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val brLocale = Locale.Builder().setLanguage("pt").setRegion("BR").build()

    fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(brLocale).format(amount)
    }

    fun formatCurrency(amount: Long): String {
        return NumberFormat.getCurrencyInstance(brLocale).format(amount / 100.0)
    }

    fun formatFriendlyDate(date: java.time.LocalDate): String {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", brLocale)
        return formatter.format(date).replaceFirstChar { it.uppercase() }
    }
}
