package club.kuzyayo.simple.psp.vo

import club.kuzyayo.simple.psp.domain.TransactionStatus
import java.math.BigDecimal

interface ProcessTransactionRequest {
    val cardNumber: SecureValue
    val expiryDate: ExpiryDate
    val cvv: SecureValue
    val amount: Amount
    val merchantId: String
}

interface ProcessTransactionResponse {
    val id: String
    val responseCode: ResponseCode
    val status: TransactionStatus
}

data class ExpiryDate(val month: Int, val year: Int)

data class Amount(val value: BigDecimal, val currency: String)
