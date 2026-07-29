package com.mahallu.manager

import com.google.common.truth.Truth.assertThat
import com.mahallu.manager.core.ui.util.Formatters
import org.junit.Test

class FormattersTest {
    @Test fun `currency formats with rupee symbol and Indian grouping`() {
        assertThat(Formatters.currency(1500.0)).isEqualTo("₹1,500")
        assertThat(Formatters.currency(150000.0)).isEqualTo("₹1,50,000")
    }

    @Test fun `currencyShort uses K L Cr suffixes`() {
        assertThat(Formatters.currencyShort(500.0)).isEqualTo("₹500")
        assertThat(Formatters.currencyShort(2500.0)).isEqualTo("₹2.5K")
        assertThat(Formatters.currencyShort(150000.0)).isEqualTo("₹1.5L")
    }

    @Test fun `calculateAge computes from dob timestamp`() {
        val now = System.currentTimeMillis()
        val thirtyYearsAgo = now - (30L * 365 * 24 * 60 * 60 * 1000)
        val age = Formatters.calculateAge(thirtyYearsAgo)
        assertThat(age).isAtLeast(29)
        assertThat(age).isAtMost(31)
    }
}