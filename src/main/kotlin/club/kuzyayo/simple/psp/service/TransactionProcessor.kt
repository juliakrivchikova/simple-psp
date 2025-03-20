package club.kuzyayo.simple.psp.service

import club.kuzyayo.simple.psp.service.impl.TransactionProcessorImpl
import club.kuzyayo.simple.psp.vo.ProcessTransactionRequest
import club.kuzyayo.simple.psp.vo.ProcessTransactionResponse

/**
 * Interface for transaction processing operations.
 *
 * @see TransactionProcessorImpl
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface TransactionProcessor {

    /**
     * Executes the [transactionRequest] processing and persists the result.
     *
     * @param transactionRequest the transaction details to be processed.
     * @return the result of the transaction processing.
     */
    suspend fun process(transactionRequest: ProcessTransactionRequest): ProcessTransactionResponse
}