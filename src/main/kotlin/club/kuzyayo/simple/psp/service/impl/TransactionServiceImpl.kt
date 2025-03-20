package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.domain.dao.TransactionDao
import club.kuzyayo.simple.psp.domain.entity.TransactionEntity
import club.kuzyayo.simple.psp.service.DataEncryptor
import club.kuzyayo.simple.psp.service.TransactionService
import club.kuzyayo.simple.psp.vo.ProcessTransactionRequest
import club.kuzyayo.simple.psp.vo.SecureValue
import club.kuzyayo.simple.psp.vo.SendTransactionResponse
import club.kuzyayo.simple.psp.vo.Transaction
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Service
class TransactionServiceImpl(
    private val transactionDao: TransactionDao,
    private val dataEncryptor: DataEncryptor,
) : TransactionService {
    override suspend fun create(transactionRequest: ProcessTransactionRequest): Transaction {
        val savedTransaction = transactionDao.save(transactionRequest.toEntity())
        return savedTransaction.toVo(transactionRequest.cardNumber, transactionRequest.cvv)
    }

    override suspend fun update(transaction: Transaction, transactionResponse: SendTransactionResponse): Transaction {
        val newStatus = if (transactionResponse.responseCode.isSuccessful()) {
            TransactionStatus.APPROVED
        } else {
            TransactionStatus.DENIED
        }

        val entity = TransactionEntity(
            id = transaction.id,
            status = newStatus,
            amount = transaction.amount,
            currency = transaction.currency,
            encryptedCardNumber = dataEncryptor.encrypt(transaction.cardNumber),
            cardNumberHash = dataEncryptor.hash(transaction.cardNumber),
            expiryDate = LocalDate.of(transaction.expiryDate.year, transaction.expiryDate.month, 1)
                .with(TemporalAdjusters.lastDayOfMonth()),
            merchantId = transaction.merchantId,
            acquirerReferenceNumber = transactionResponse.acquirerReferenceNumber
        )

        return transactionDao.save(entity).toVo(transaction.cardNumber, transaction.cvv)
    }

    private fun TransactionEntity.toVo(cardNumber: SecureValue, cvv: SecureValue) = Transaction(
        id = requireNotNull(this.id) { "Saved entity id is not supposed to be null" },
        status = this.status,
        cardNumber = cardNumber,
        expiryDate = this.expiryDate,
        cvv = cvv,
        amount = this.amount,
        currency = this.currency,
        merchantId = this.merchantId,
    )

    private fun ProcessTransactionRequest.toEntity() = TransactionEntity(
        status = TransactionStatus.PENDING,
        amount = this.amount.value,
        currency = this.amount.currency,
        encryptedCardNumber = dataEncryptor.encrypt(this.cardNumber),
        cardNumberHash = dataEncryptor.hash(this.cardNumber),
        expiryDate = LocalDate.of(this.expiryDate.year, this.expiryDate.month, 1)
            .with(TemporalAdjusters.lastDayOfMonth()),
        merchantId = this.merchantId,
    )
}