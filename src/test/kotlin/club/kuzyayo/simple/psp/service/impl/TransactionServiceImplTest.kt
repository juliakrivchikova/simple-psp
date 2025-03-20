package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.domain.dao.TransactionDao
import club.kuzyayo.simple.psp.domain.entity.TransactionEntity
import club.kuzyayo.simple.psp.service.DataEncryptor
import club.kuzyayo.simple.psp.vo.*
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TransactionServiceImplTest {
    private val transactionDao: TransactionDao = mock()

    private val dataEncryptor: DataEncryptor = mock()

    private val transactionService = TransactionServiceImpl(transactionDao, dataEncryptor)

    @Test
    fun `should create transaction`() = runBlocking {
        //given
        val request = paymentRequest()
        val encryptedPan = "encryptedPan"
        val panHash = "panHash"
        whenever(dataEncryptor.encrypt(request.cardNumber))
            .thenReturn(encryptedPan)
        whenever(dataEncryptor.hash(request.cardNumber))
            .thenReturn(panHash)
        val id = transactionWillBeCreated(request, encryptedPan, panHash)

        //when
        val transaction = transactionService.create(request)

        //then
        assertEquals(id, transaction.id)
        assertEquals(TransactionStatus.PENDING, transaction.status)
        assertEquals(request.cardNumber, transaction.cardNumber)
        assertEquals(LocalDate.of(request.expiryDate.year, request.expiryDate.month, 31), transaction.expiryDate)
        assertEquals(request.cvv, transaction.cvv)
        assertEquals(request.amount.value, transaction.amount)
        assertEquals(request.amount.currency, transaction.currency)
        assertEquals(request.merchantId, transaction.merchantId)
    }

    @Test
    fun `should save APPROVED transaction when send transaction response is successful`() = runBlocking {
        //given
        val pendingTransaction = transaction(TransactionStatus.PENDING)
        val transactionResponse = mock<SendTransactionResponse> {
            on { it.responseCode } doReturn ResponseCodes.SUCCESS
            on { it.acquirerReferenceNumber } doReturn "I am the number to identify transaction in the external system"
        }
        val encryptedPan = "encryptedPan"
        val panHash = "panHash"
        whenever(dataEncryptor.encrypt(pendingTransaction.cardNumber))
            .thenReturn(encryptedPan)
        whenever(dataEncryptor.hash(pendingTransaction.cardNumber))
            .thenReturn(panHash)
        transactionWillBeUpdated(pendingTransaction, encryptedPan, panHash, TransactionStatus.APPROVED)

        //when
        val resultTransaction = transactionService.update(pendingTransaction, transactionResponse)

        //then
        assertEquals(pendingTransaction.id, resultTransaction.id)
        assertEquals(TransactionStatus.APPROVED, resultTransaction.status)
        assertEquals(pendingTransaction.cardNumber, resultTransaction.cardNumber)
        assertEquals(pendingTransaction.expiryDate, resultTransaction.expiryDate)
        assertEquals(pendingTransaction.cvv, resultTransaction.cvv)
        assertEquals(pendingTransaction.amount, resultTransaction.amount)
        assertEquals(pendingTransaction.currency, resultTransaction.currency)
        assertEquals(pendingTransaction.merchantId, resultTransaction.merchantId)
    }

    @Test
    fun `should save DENIED transaction when send transaction response is successful`() = runBlocking {
        //given
        val pendingTransaction = transaction(TransactionStatus.PENDING)
        val transactionResponse = mock<SendTransactionResponse> {
            on { it.responseCode } doReturn ResponseCodes.TRANSACTION_REJECTED
            on { it.acquirerReferenceNumber } doReturn "I am the number to identify transaction in the external system"
        }
        val encryptedPan = "encryptedPan"
        val panHash = "panHash"
        whenever(dataEncryptor.encrypt(pendingTransaction.cardNumber))
            .thenReturn(encryptedPan)
        whenever(dataEncryptor.hash(pendingTransaction.cardNumber))
            .thenReturn(panHash)
        transactionWillBeUpdated(pendingTransaction, encryptedPan, panHash, TransactionStatus.DENIED)

        //when
        val resultTransaction = transactionService.update(pendingTransaction, transactionResponse)

        //then
        assertEquals(pendingTransaction.id, resultTransaction.id)
        assertEquals(TransactionStatus.DENIED, resultTransaction.status)
        assertEquals(pendingTransaction.cardNumber, resultTransaction.cardNumber)
        assertEquals(pendingTransaction.expiryDate, resultTransaction.expiryDate)
        assertEquals(pendingTransaction.cvv, resultTransaction.cvv)
        assertEquals(pendingTransaction.amount, resultTransaction.amount)
        assertEquals(pendingTransaction.currency, resultTransaction.currency)
        assertEquals(pendingTransaction.merchantId, resultTransaction.merchantId)
    }

    private fun transaction(transactionStatus: TransactionStatus) = Transaction(
        id = "superCoolId",
        status = transactionStatus,
        cardNumber = CardNumber("4242424242424242"),
        expiryDate = LocalDate.of(2029, 5, 31),
        cvv = SecureValue.fullyMasked("777"),
        amount = 1000.toBigDecimal(),
        currency = "USD",
        merchantId = "someMerchantId"
    )

    private suspend fun transactionWillBeCreated(
        request: ProcessPaymentRequest,
        encryptedPan: String,
        panHash: String,
    ): String {
        val id = "superCoolId"
        whenever(transactionDao.save(
            org.mockito.kotlin.check {
                assertNull(it.id)
                assertEquals(TransactionStatus.PENDING, it.status)
                assertEquals(request.amount.value, it.amount)
                assertEquals(request.amount.currency, it.currency)
                assertEquals(encryptedPan, it.encryptedCardNumber)
                assertEquals(panHash, it.cardNumberHash)
                assertEquals(LocalDate.of(request.expiryDate.year, request.expiryDate.month, 31), it.expiryDate)
                assertEquals(request.merchantId, it.merchantId)
            }
        )).thenAnswer {
            val entity = it.arguments[0]!! as TransactionEntity
            entity.id = id
            entity
        }

        return id
    }

    private suspend fun transactionWillBeUpdated(
        pendingTransaction: Transaction,
        encryptedPan: String,
        panHash: String,
        status: TransactionStatus
    ) {
        whenever(transactionDao.save(
            org.mockito.kotlin.check {
                assertEquals(pendingTransaction.id, it.id)
                assertEquals(status, it.status)
                assertEquals(pendingTransaction.amount, it.amount)
                assertEquals(pendingTransaction.currency, it.currency)
                assertEquals(encryptedPan, it.encryptedCardNumber)
                assertEquals(panHash, it.cardNumberHash)
                assertEquals(pendingTransaction.expiryDate, it.expiryDate)
                assertEquals(pendingTransaction.merchantId, it.merchantId)
            }
        )).thenAnswer {
            it.arguments[0]
        }
    }

    private fun paymentRequest() = ProcessPaymentRequest(
        cardNumber = CardNumber("4242424242424242"),
        expiryDate = ExpiryDate(month = 5, year = 2029),
        cvv = SecureValue.fullyMasked("777"),
        amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
        merchantId = "someMerchantId"
    )
}