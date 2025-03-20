package club.kuzyayo.simple.psp.service

import club.kuzyayo.simple.psp.service.impl.acquirer.AcquirerAClient
import club.kuzyayo.simple.psp.service.impl.acquirer.AcquirerBClient
import club.kuzyayo.simple.psp.vo.ResponseType
import club.kuzyayo.simple.psp.vo.SendTransactionRequest
import club.kuzyayo.simple.psp.vo.SendTransactionResponse

/**
 * Interface for interacting with an acquirer.
 *
 * @see AcquirerAClient
 * @see AcquirerBClient
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface AcquirerClient {

    /**
     * Sends request to execute transaction to the acquirer and returns [SendTransactionResponse] based on acquirer's response.
     * In case of any problems such as network errors, returns the appropriate response code of [ResponseType.ERROR] type.
     *
     * @param sendTransactionRequest the request to send to the acquirer.
     * @return the response based on acquirer data.
     */
    suspend fun sendTransaction(sendTransactionRequest: SendTransactionRequest): SendTransactionResponse
}