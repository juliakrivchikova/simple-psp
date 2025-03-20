package club.kuzyayo.simple.psp.service.impl.acquirer

import club.kuzyayo.simple.psp.ResponseCodes.SUCCESS
import club.kuzyayo.simple.psp.ResponseCodes.TRANSACTION_REJECTED
import club.kuzyayo.simple.psp.service.AcquirerClient
import club.kuzyayo.simple.psp.vo.ResponseCode
import club.kuzyayo.simple.psp.vo.SendTransactionRequest
import club.kuzyayo.simple.psp.vo.SendTransactionResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AcquirerAClient : AcquirerClient {

    private val logger = LoggerFactory.getLogger(AcquirerAClient::class.java)

    override suspend fun sendTransaction(sendTransactionRequest: SendTransactionRequest): SendTransactionResponse {
        val cardNumberLastDigit = sendTransactionRequest.cardNumber.value.last()

        if (!cardNumberLastDigit.isDigit()) {
            throw IllegalArgumentException("The last symbol of the card number must be a digit.")
        }

        val responseCode = if (cardNumberLastDigit.toString().toInt() % 2 == 0) {
            logger.debug("Card number last digit is even, transaction is approved")
            SUCCESS
        } else {
            logger.debug("Card number last digit is odd, transaction is denied")
            TRANSACTION_REJECTED
        }

        return SendTransactionRs(responseCode = responseCode)
    }

    private class SendTransactionRs(
        override val acquirerReferenceNumber: String? = null,
        override val responseCode: ResponseCode
    ) : SendTransactionResponse
}