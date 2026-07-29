package com.mahallu.manager.core.security

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PasswordHasherTest {
    @Test fun `hash produces different output for different passwords`() {
        val h1 = PasswordHasher.hash("password123")
        val h2 = PasswordHasher.hash("password456")
        assertThat(h1).isNotEqualTo(h2)
    }

    @Test fun `hash produces different output for same password (random salt)`() {
        val h1 = PasswordHasher.hash("password123")
        val h2 = PasswordHasher.hash("password123")
        assertThat(h1).isNotEqualTo(h2)
    }

    @Test fun `verify returns true for correct password`() {
        val stored = PasswordHasher.hash("password123")
        assertThat(PasswordHasher.verify("password123", stored)).isTrue()
    }

    @Test fun `verify returns false for wrong password`() {
        val stored = PasswordHasher.hash("password123")
        assertThat(PasswordHasher.verify("wrong", stored)).isFalse()
    }

    @Test fun `verify returns false for malformed hash`() {
        assertThat(PasswordHasher.verify("anything", "not-a-valid-hash")).isFalse()
    }
}