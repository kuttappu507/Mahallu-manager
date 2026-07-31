package com.mahallu.manager.feature.certificates

import androidx.compose.runtime.Composable

/**
 * Thin wrappers around the unified [CertificateFormScreen] for each certificate
 * type. Each wrapper passes the right [type] string and a back-callback.
 */
@Composable
fun MembershipCertificateScreen(onBack: () -> Unit) =
    CertificateFormScreen(type = "MEMBERSHIP", onBack = onBack)

@Composable
fun ResidenceCertificateScreen(onBack: () -> Unit) =
    CertificateFormScreen(type = "RESIDENCE", onBack = onBack)

@Composable
fun MarriageCertificateScreen(onBack: () -> Unit) =
    CertificateFormScreen(type = "MARRIAGE", onBack = onBack)

@Composable
fun DeathCertificateScreen(onBack: () -> Unit) =
    CertificateFormScreen(type = "DEATH", onBack = onBack)
