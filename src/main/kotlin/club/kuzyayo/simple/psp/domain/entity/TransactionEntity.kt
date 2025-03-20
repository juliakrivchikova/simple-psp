package club.kuzyayo.simple.psp.domain.entity

import club.kuzyayo.simple.psp.domain.TransactionStatus
import java.math.BigDecimal
import java.time.LocalDate

class TransactionEntity(
    var id: String? = null,
    var status: TransactionStatus,
    var amount: BigDecimal,
    var currency: String,
    var encryptedCardNumber: String,
    var cardNumberHash: String,
    var expiryDate: LocalDate,
    var merchantId: String,
    var acquirerReferenceNumber: String? = null
) {

    fun copy(id: String? = null, status: TransactionStatus? = null): TransactionEntity {
        return TransactionEntity(
            id = id ?: this.id,
            status = status ?: this.status,
            amount = amount,
            currency = currency,
            encryptedCardNumber = encryptedCardNumber,
            cardNumberHash = cardNumberHash,
            expiryDate = expiryDate,
            merchantId = merchantId,
            acquirerReferenceNumber = acquirerReferenceNumber
        )
    }
}