package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.service.AcquirerClient
import club.kuzyayo.simple.psp.service.AcquirerRouter
import club.kuzyayo.simple.psp.service.impl.acquirer.AcquirerAClient
import club.kuzyayo.simple.psp.service.impl.acquirer.AcquirerBClient
import club.kuzyayo.simple.psp.vo.Transaction
import org.springframework.stereotype.Service

@Service
class AcquirerRouterImpl(
    private val acquirerAClient: AcquirerAClient,
    private val acquirerBClient: AcquirerBClient,
) : AcquirerRouter {

    override suspend fun route(transaction: Transaction): AcquirerClient {
        val binSum = calculateBinSum(transaction.cardNumber.value)
        return if (binSum % 2 == 0) {
            acquirerAClient
        } else {
            acquirerBClient
        }
    }

    private fun calculateBinSum(cardNumber: String): Int {
        val binPart = cardNumber.substring(0, 6)
        return binPart.map { it.toString().toInt() }.sum()
    }
}