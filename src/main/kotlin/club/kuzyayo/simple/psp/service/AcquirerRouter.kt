package club.kuzyayo.simple.psp.service

import club.kuzyayo.simple.psp.service.impl.AcquirerRouterImpl
import club.kuzyayo.simple.psp.vo.Transaction

/**
 * Interface for routing transactions to an appropriate acquirer client.
 *
 * Implementations of this interface determine which [AcquirerClient] should process a given transaction.
 * The routing decision can be based on any custom logic, such as the transaction's card number or BIN.
 *
 * @see AcquirerRouterImpl
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface AcquirerRouter {

    /**
     * Routes the given transaction to a suitable acquirer client.
     *
     * @param transaction the transaction to route.
     * @return the selected [AcquirerClient] to process the transaction.
     */
    suspend fun route(transaction: Transaction): AcquirerClient
}