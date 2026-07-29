package com.mahallu.manager.core.security

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AesGcmCipherTest {
    @Test fun `round trip encrypt and decrypt returns original bytes`() {
        val key = AesGcmCipher.generateKey()
        val original = "Mahallu Manager 2025 🔐".toByteArray(Charsets.UTF_8)
        val encrypted = AesGcmCipher.encrypt(original, key)
        val decrypted = AesGcmCipher.decrypt(encrypted, key)
        assertThat(decrypted.toString(Charsets.UTF_8)).isEqualTo("Mahallu Manager 2025 🔐")
    }

    @Test fun `decryption fails when tampered`() {
        val key = AesGcmCipher.generateKey()
        val encrypted = AesGcmCipher.encrypt("hello".toByteArray(), key)
        encrypted[encrypted.size - 1] = (encrypted[encrypted.size - 1].toInt() xor 1).toByte()
        var threw = false
        try {
            AesGcmCipher.decrypt(encrypted, key)
        } catch (t: Throwable) {
            threw = true
        }
        assertThat(threw).isTrue()
    }

    @Test fun `each encryption produces different ciphertext (random IV)`() {
        val key = AesGcmCipher.generateKey()
        val data = "hello".toByteArray()
        val e1 = AesGcmCipher.encrypt(data, key)
        val e2 = AesGcmCipher.encrypt(data, key)
        assertThat(e1.toList()).isNotEqualTo(e2.toList())
    }

    @Test fun `decryption with wrong key fails`() {
        val k1 = AesGcmCipher.generateKey()
        val k2 = AesGcmCipher.generateKey()
        val encrypted = AesGcmCipher.encrypt("secret".toByteArray(), k1)
        var threw = false
        try {
            AesGcmCipher.decrypt(encrypted, k2)
        } catch (t: Throwable) {
            threw = true
        }
        assertThat(threw).isTrue()
    }
}