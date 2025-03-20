package club.kuzyayo.simple.psp.service

import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.service.impl.TransactionServiceImpl
import club.kuzyayo.simple.psp.vo.ProcessTransactionRequest
import club.kuzyayo.simple.psp.vo.SendTransactionResponse
import club.kuzyayo.simple.psp.vo.Transaction


/**
 * Interface for transaction persistence operations.
 *
 * <p>This interface abstracts the persistence logic for transaction data, including conversion and encryption.
 *
 * @see TransactionServiceImpl
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface TransactionService {

    /**
     * Creates a new transaction in a [TransactionStatus.PENDING] state.
     *
     * @param transactionRequest the details for creating the transaction.
     * @return the persisted transaction as a [Transaction] value object.
     */
    suspend fun create(transactionRequest: ProcessTransactionRequest): Transaction

    /**
     * Updates an existing transaction according to the provided [transactionResponse].
     *
     * @param transaction the original transaction.
     * @param transactionResponse the details for updating the transaction.
     * @return the [Transaction] value object representing the updated transaction.
     */
    suspend fun update(transaction: Transaction, transactionResponse: SendTransactionResponse): Transaction
}