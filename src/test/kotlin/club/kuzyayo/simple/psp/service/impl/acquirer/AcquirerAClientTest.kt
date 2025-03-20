package club.kuzyayo.simple.psp.service.impl.acquirer

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.vo.CardNumber
import club.kuzyayo.simple.psp.vo.SecureValue
import club.kuzyayo.simple.psp.vo.SendTransactionRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AcquirerAClientTest {
    private val acquirerAClient = AcquirerAClient()

    @Test
    fun `should return with success when last card number digit is even`() {
        //given
        val request = mock<SendTransactionRequest> {
            on { it.cardNumber } doReturn SecureValue.fullyMasked("4242424242424242")
        }

        //when
        val response = runBlocking { acquirerAClient.sendTransaction(request) }

        //then
        assertEquals(ResponseCodes.SUCCESS, response.responseCode)
    }

    @Test
    fun `should return with failure when last card number digit is odd`() {
        //given
        val request = mock<SendTransactionRequest> {
            on { it.cardNumber } doReturn CardNumber("4111111111111111")
        }

        //when
        val response = runBlocking { acquirerAClient.sendTransaction(request) }

        //then
        assertEquals(ResponseCodes.TRANSACTION_REJECTED, response.responseCode)
    }
}