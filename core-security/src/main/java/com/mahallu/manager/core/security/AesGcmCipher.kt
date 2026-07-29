package com.mahallu.manager.core.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-256-GCM encryption for backup payloads.
 * Output format: [12-byte IV][cipher+tag bytes], encoded as base64.
 */
object AesGcmCipher {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH = 12
    private const val TAG_BITS = 128

    fun encrypt(plainBytes: ByteArray, keyBytes: ByteArray): ByteArray {
        val key: SecretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        val cipherText = cipher.doFinal(plainBytes)
        return iv + cipherText
    }

    fun decrypt(payload: ByteArray, keyBytes: ByteArray): ByteArray {
        val key: SecretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = payload.copyOfRange(0, IV_LENGTH)
        val cipherText = payload.copyOfRange(IV_LENGTH, payload.size)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        return cipher.doFinal(cipherText)
    }

    fun generateKey(): ByteArray = ByteArray(32).also { SecureRandom().nextBytes(it) }

    fun toBase64(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
    fun fromBase64(b64: String): ByteArray = Base64.decode(b64, Base64.NO_WRAP)
}