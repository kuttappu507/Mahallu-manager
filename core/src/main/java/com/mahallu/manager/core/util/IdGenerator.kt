package com.mahallu.manager.core.util

import java.util.UUID

object IdGenerator {
    fun newId(): String = UUID.randomUUID().toString()

    fun prefixed(prefix: String): String = "${prefix}-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"

    fun familyNumber(): String = "FAM-${System.currentTimeMillis().toString().takeLast(7)}"
    fun memberId(): String = "MEM-${System.currentTimeMillis().toString().takeLast(7)}"
    fun receiptNumber(): String = "RCP-${System.currentTimeMillis().toString().takeLast(9)}"
    fun donationReceipt(): String = "DON-${System.currentTimeMillis().toString().takeLast(9)}"
    fun marriageReg(): String = "MR-${System.currentTimeMillis().toString().takeLast(7)}"
    fun deathReg(): String = "DR-${System.currentTimeMillis().toString().takeLast(7)}"
    fun welfareId(): String = "WEL-${System.currentTimeMillis().toString().takeLast(7)}"
    fun certId(): String = "CRT-${System.currentTimeMillis().toString().takeLast(7)}"
    fun txnId(): String = "TXN-${System.currentTimeMillis().toString().takeLast(10)}"
    fun backupId(): String = "BKP-${System.currentTimeMillis().toString().takeLast(10)}"
}