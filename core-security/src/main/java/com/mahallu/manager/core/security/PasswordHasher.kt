package com.mahallu.manager.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password hashing using PBKDF2-HMAC-SHA256 with random salt.
 * 100k iterations, 256-bit key. Salt+hash stored as base64 strings.
 */
object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val ALGO = "PBKDF2WithHmacSHA256"

    fun hash(password: String): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password, salt)
        val encoder = java.util.Base64.getEncoder()
        val saltB64 = encoder.encodeToString(salt)
        val hashB64 = encoder.encodeToString(hash)
        return "$saltB64:$hashB64"
    }

    fun verify(password: String, stored: String): Boolean {
        return try {
            val parts = stored.split(":")
            if (parts.size != 2) return false
            val decoder = java.util.Base64.getDecoder()
            val salt = decoder.decode(parts[0])
            val expected = decoder.decode(parts[1])
            val actual = pbkdf2(password, salt)
            MessageDigest.isEqual(expected, actual)
        } catch (e: Exception) {
            false
        }
    }

    private fun pbkdf2(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return SecretKeyFactory.getInstance(ALGO).generateSecret(spec).encoded
    }
}