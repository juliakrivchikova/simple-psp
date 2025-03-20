package club.kuzyayo.simple.psp.vo.api

import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.vo.*

data class ProcessPaymentRequest(
    override val cardNumber: CardNumber,
    override val expiryDate: ExpiryDate,
    override val cvv: SecureValue,
    override val amount: Amount,
    override val merchantId: String
) : ProcessTransactionRequest

data class ProcessPaymentResponse(
    val error: ResponseCode? = null,
    val result: ProcessPaymentResult? = null
)

data class ProcessPaymentResult(
    val id: String,
    val status: TransactionStatus,
)