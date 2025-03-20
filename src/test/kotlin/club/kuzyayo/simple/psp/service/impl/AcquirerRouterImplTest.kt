package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.service.impl.acquirer.AcquirerAClient
import club.kuzyayo.simple.psp.service.impl.acquirer.AcquirerBClient
import club.kuzyayo.simple.psp.vo.CardNumber
import club.kuzyayo.simple.psp.vo.Transaction
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertSame

class AcquirerRouterImplTest {
    private val acquirerAClient = mock<AcquirerAClient>()

    private val acquirerBClient = mock<AcquirerBClient>()

    private val acquirerRouter = AcquirerRouterImpl(acquirerAClient, acquirerBClient)

    @Test
    fun `should route to acquirer A when sum of the digits in the BIN is even`() {
        //given
        val transaction = mock<Transaction> {
            on { it.cardNumber } doReturn CardNumber("4242424242424242")
        }

        //when
        val acquirerClient = runBlocking { acquirerRouter.route(transaction) }

        //then
        assertSame(acquirerAClient, acquirerClient)
    }

    @Test
    fun `should route to acquirer B when sum of the digits in the BIN is odd`() {
        //given
        val transaction = mock<Transaction> {
            on { it.cardNumber } doReturn CardNumber("4111111111111111")
        }

        //when
        val acquirerClient = runBlocking { acquirerRouter.route(transaction) }

        //then
        assertSame(acquirerBClient, acquirerClient)
    }
}