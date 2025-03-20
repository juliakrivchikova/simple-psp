package club.kuzyayo.simple.psp.vo

import club.kuzyayo.simple.psp.domain.TransactionStatus
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val id: String,
    val status: TransactionStatus,
    override val cardNumber: SecureValue,
    override val expiryDate: LocalDate,
    override val cvv: SecureValue,
    override val amount: BigDecimal,
    override val currency: String,
    override val merchantId: String,
) : SendTransactionRequest