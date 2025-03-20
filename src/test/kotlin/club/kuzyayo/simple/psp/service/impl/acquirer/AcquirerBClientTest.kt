package club.kuzyayo.simple.psp.service.impl.acquirer

import club.kuzyayo.simple.psp.vo.SendTransactionRequest
import club.kuzyayo.simple.psp.vo.SendTransactionResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertSame

class AcquirerBClientTest {
    private val acquirerAClient = mock<AcquirerAClient>()

    private val acquirerBClient = AcquirerBClient(acquirerAClient)

    @Test
    fun `should use the same logic as acquirer A`() = runBlocking {
        //given
        val request = mock<SendTransactionRequest>()
        val acquirerAResponse = mock<SendTransactionResponse>()
        whenever(acquirerAClient.sendTransaction(request)).thenReturn(acquirerAResponse)

        //when
        val actualResponse = acquirerBClient.sendTransaction(request)

        //then
        assertSame(acquirerAResponse, actualResponse)
    }
}