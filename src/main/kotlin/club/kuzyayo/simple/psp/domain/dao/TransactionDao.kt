package club.kuzyayo.simple.psp.domain.dao

import club.kuzyayo.simple.psp.domain.entity.TransactionEntity

/**
 * Interface defining the operations for accessing and managing transaction data.
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface TransactionDao {

    /**
     * Finds a transaction by its unique identifier.
     *
     * @param id the unique identifier of the transaction.
     * @return the corresponding [TransactionEntity] if it exists, or null otherwise.
     */
    suspend fun findById(id: String): TransactionEntity?

    /**
     * Persists the given transaction.
     *
     * @param transaction the [TransactionEntity] to save.
     * @return the saved [TransactionEntity] with any updates applied (e.g., generated IDs).
     */
    suspend fun save(transaction: TransactionEntity): TransactionEntity
}