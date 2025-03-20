package club.kuzyayo.simple.psp.vo

import java.math.BigDecimal
import java.time.LocalDate

interface SendTransactionRequest {
    val cardNumber: SecureValue
    val expiryDate: LocalDate
    val cvv: SecureValue
    val amount: BigDecimal
    val currency: String
    val merchantId: String
}

interface SendTransactionResponse {
    val responseCode: ResponseCode
    val acquirerReferenceNumber: String?
}