package club.kuzyayo.simple.psp.domain.dao

import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.domain.entity.TransactionEntity
import club.kuzyayo.simple.psp.service.IdGenerator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TransactionDaoImplTest {

    @Test
    fun `should return transaction by id when it is present in storage`() = runBlocking {
        //given
        val id = "777"
        val existingTransaction = transactionEntity(id = id, status = TransactionStatus.PENDING)
        val transactionStore = ConcurrentHashMap<String, TransactionEntity>()
        transactionStore[id] = existingTransaction
        val transactionDao = TransactionDaoImpl(idGenerator = mock(), transactionStore = transactionStore)

        //when
        val actualFoundEntity = transactionDao.findById(id)

        //then
        assertNotNull(actualFoundEntity)
        assertEquals(id, actualFoundEntity.id)
        transactionDataIsCorrect(expectedTransaction = existingTransaction, actualTransaction = actualFoundEntity)
    }

    @Test
    fun `should return null when transaction is not present in storage`() = runBlocking {
        //given
        val transactionDao = TransactionDaoImpl(
            idGenerator = mock(),
            transactionStore = ConcurrentHashMap<String, TransactionEntity>()
        )

        //when
        val actualFoundEntity = transactionDao.findById("777")

        //then
        assertNull(actualFoundEntity)
    }


    @Test
    fun `should save transaction when it is new`() = runBlocking {
        //given
        val id = "777"
        val idGenerator: IdGenerator = mock {
            on { it.generate() } doReturn id
        }
        val transactionStore = ConcurrentHashMap<String, TransactionEntity>()
        val transactionDao = TransactionDaoImpl(idGenerator, transactionStore)
        val inputEntity = transactionEntity(id = null, status = TransactionStatus.PENDING)

        //when
        val resultEntity = transactionDao.save(inputEntity)

        //then
        assertEquals(id, resultEntity.id)
        transactionDataIsCorrect(expectedTransaction = inputEntity, actualTransaction = resultEntity)

        val entityInStorage = transactionStore[id]
        assertNotNull(entityInStorage)
        assertEquals(id, entityInStorage.id)
        transactionDataIsCorrect(expectedTransaction = inputEntity, actualTransaction = entityInStorage)
    }

    @Test
    fun `should update transaction when it already exists`() = runBlocking {
        //given
        val id = "777"
        val transactionStore = ConcurrentHashMap<String, TransactionEntity>()
        val existingEntity = transactionEntity(id = id, status = TransactionStatus.PENDING)
        transactionStore[id] = existingEntity
        val transactionDao = TransactionDaoImpl(idGenerator = mock(), transactionStore)
        val inputEntity = existingEntity.copy(status = TransactionStatus.APPROVED)

        //when
        val resultEntity = transactionDao.save(inputEntity)

        //then
        assertEquals(id, resultEntity.id)
        transactionDataIsCorrect(expectedTransaction = inputEntity, actualTransaction = resultEntity)

        val entityInStorage = transactionStore[id]
        assertNotNull(entityInStorage)
        assertEquals(id, entityInStorage.id)
        transactionDataIsCorrect(expectedTransaction = inputEntity, actualTransaction = entityInStorage)
    }

    @Test
    fun `should fail to save transaction with invalid id`() = runBlocking {
        //given
        val transactionDao =
            TransactionDaoImpl(idGenerator = mock(), transactionStore = ConcurrentHashMap<String, TransactionEntity>())
        val inputEntity = transactionEntity(id = "777", status = TransactionStatus.PENDING)

        //when
        val exception = assertThrows<IllegalArgumentException> { transactionDao.save(inputEntity) }

        //then
        assertEquals("Entity with provided id 777 does not exist", exception.message)
    }


    private fun transactionDataIsCorrect(
        expectedTransaction: TransactionEntity,
        actualTransaction: TransactionEntity
    ) {
        assertEquals(expectedTransaction.status, actualTransaction.status)
        assertEquals(expectedTransaction.amount, actualTransaction.amount)
        assertEquals(expectedTransaction.currency, actualTransaction.currency)
        assertEquals(expectedTransaction.encryptedCardNumber, actualTransaction.encryptedCardNumber)
        assertEquals(expectedTransaction.cardNumberHash, actualTransaction.cardNumberHash)
        assertEquals(expectedTransaction.expiryDate, actualTransaction.expiryDate)
        assertEquals(expectedTransaction.merchantId, actualTransaction.merchantId)
    }

    private fun transactionEntity(id: String?, status: TransactionStatus) = TransactionEntity(
        id = id,
        status = status,
        amount = 1000.toBigDecimal(),
        currency = "USD",
        encryptedCardNumber = "encryptedCardNumber",
        cardNumberHash = "cardNumberHash",
        expiryDate = LocalDate.now().plusYears(1),
        merchantId = "merchantId",
    )
}