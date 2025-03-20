package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.service.AcquirerClient
import club.kuzyayo.simple.psp.service.AcquirerRouter
import club.kuzyayo.simple.psp.service.TransactionService
import club.kuzyayo.simple.psp.vo.*
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class TransactionProcessorImplTest {
    private val transactionService: TransactionService = mock()

    private val acquirerRouter: AcquirerRouter = mock()

    private val transactionProcessor = TransactionProcessorImpl(transactionService, acquirerRouter)

    @Test
    fun `should process transaction`() = runBlocking {
        //given
        val request = paymentRequest()
        val id = "superCoolId"
        val pendingTransaction = transactionWillBeCreated(request, id)
        val acquirerClient = acquirerWillBeResolved(pendingTransaction)
        val sendTransactionResponse = transactionWillBeSentToAcquirer(
            pendingTransaction = pendingTransaction,
            acquirerClient = acquirerClient,
            responseCode = ResponseCodes.SUCCESS
        )
        val approvedTransaction = transactionWillBeUpdated(pendingTransaction, sendTransactionResponse)


        //when
        val response = transactionProcessor.process(request)

        //then
        assertEquals(id, response.id)
        assertEquals(sendTransactionResponse.responseCode, response.responseCode)
        assertEquals(approvedTransaction.status, response.status)
    }

    private suspend fun transactionWillBeUpdated(
        pendingTransaction: Transaction,
        sendTransactionResponse: SendTransactionResponse
    ): Transaction {
        val approvedTransaction = pendingTransaction.copy(status = TransactionStatus.APPROVED)
        whenever(transactionService.update(pendingTransaction, sendTransactionResponse))
            .thenReturn(approvedTransaction)
        return approvedTransaction
    }

    private suspend fun acquirerWillBeResolved(pendingTransaction: Transaction): AcquirerClient {
        val acquirerClient = mock<AcquirerClient>()
        whenever(acquirerRouter.route(pendingTransaction))
            .thenReturn(acquirerClient)
        return acquirerClient
    }

    private suspend fun transactionWillBeCreated(
        request: ProcessPaymentRequest,
        id: String
    ): Transaction {
        val pendingTransaction = transaction(request, id, TransactionStatus.PENDING)

        whenever(transactionService.create(request))
            .thenReturn(pendingTransaction)
        return pendingTransaction
    }

    private fun paymentRequest() = ProcessPaymentRequest(
        cardNumber = CardNumber("4111111111111111"),
        expiryDate = ExpiryDate(month = 5, year = 2029),
        cvv = SecureValue.fullyMasked("777"),
        amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
        merchantId = "someMerchantId"
    )

    private suspend fun transactionWillBeSentToAcquirer(
        pendingTransaction: Transaction,
        acquirerClient: AcquirerClient,
        responseCode: ResponseCode
    ): SendTransactionResponse {
        val sendTransactionResponse = mock<SendTransactionResponse> {
            on { it.responseCode } doReturn responseCode
        }
        whenever(acquirerClient.sendTransaction(check {
            assertEquals(pendingTransaction.cardNumber, it.cardNumber)
            assertEquals(pendingTransaction.expiryDate, it.expiryDate)
            assertEquals(pendingTransaction.cvv, it.cvv)
            assertEquals(pendingTransaction.amount, it.amount)
            assertEquals(pendingTransaction.currency, it.currency)
            assertEquals(pendingTransaction.merchantId, it.merchantId)
        })).thenReturn(sendTransactionResponse)
        return sendTransactionResponse
    }

    private fun transaction(
        request: ProcessPaymentRequest,
        id: String,
        status: TransactionStatus
    ) = Transaction(
        id = id,
        status = status,
        cardNumber = request.cardNumber,
        expiryDate = LocalDate.of(2029, 5, 31),
        cvv = request.cvv,
        amount = request.amount.value,
        currency = request.amount.currency,
        merchantId = request.merchantId
    )
}