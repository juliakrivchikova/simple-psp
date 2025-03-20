package club.kuzyayo.simple.psp.domain.dao

import club.kuzyayo.simple.psp.domain.entity.TransactionEntity
import club.kuzyayo.simple.psp.service.IdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class TransactionDaoImpl(
    private val idGenerator: IdGenerator,
    private val transactionStore: MutableMap<String, TransactionEntity>
) : TransactionDao {

    @Autowired
    constructor(idGenerator: IdGenerator) : this(idGenerator, ConcurrentHashMap<String, TransactionEntity>())

    override suspend fun findById(id: String): TransactionEntity? {
        return transactionStore[id]?.copy()
    }

    override suspend fun save(transaction: TransactionEntity): TransactionEntity {
        val resultEntity = if (transaction.id == null) {
            val generatedId = idGenerator.generate()
            val newEntity = transaction.copy(id = generatedId)
            transactionStore[generatedId] = newEntity
            newEntity
        } else {
            val existingEntity =
                requireNotNull(transactionStore[transaction.id!!]) { "Entity with provided id ${transaction.id} does not exist" }

            updateEntity(existingEntity, transaction)
            existingEntity
        }

        return resultEntity.copy()
    }

    private fun updateEntity(existingEntity: TransactionEntity, transaction: TransactionEntity) {
        existingEntity.status = transaction.status
        existingEntity.amount = transaction.amount
        existingEntity.currency = transaction.currency
        existingEntity.encryptedCardNumber = transaction.encryptedCardNumber
        existingEntity.cardNumberHash = transaction.cardNumberHash
        existingEntity.expiryDate = transaction.expiryDate
        existingEntity.merchantId = transaction.merchantId
        existingEntity.acquirerReferenceNumber = transaction.acquirerReferenceNumber
    }
}