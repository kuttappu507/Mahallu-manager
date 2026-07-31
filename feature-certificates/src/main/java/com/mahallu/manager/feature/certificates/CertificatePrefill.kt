package com.mahallu.manager.feature.certificates

import java.util.concurrent.atomic.AtomicReference

/**
 * In-process holder used to pre-fill the certificate form when navigating from
 * a marriage / death / member record. After being consumed once, the value
 * should be cleared (the screen does this in onResume / LaunchedEffect).
 */
data class CertificatePrefillData(
    val memberName: String? = null,
    val fatherName: String? = null,
    val address: String? = null,
    val memberNumber: String? = null,
    val brideName: String? = null,
    val groomName: String? = null,
    val witnesses: String? = null,
    val registrationNumber: String? = null,
    val date: String? = null,
    val deceasedName: String? = null
)

object CertificatePrefillHolder {
    private val ref = AtomicReference<CertificatePrefillData?>(null)

    fun set(data: CertificatePrefillData?) {
        ref.set(data)
    }

    fun consume(): CertificatePrefillData? {
        val v = ref.get()
        ref.set(null)
        return v
    }

    fun peek(): CertificatePrefillData? = ref.get()
}
