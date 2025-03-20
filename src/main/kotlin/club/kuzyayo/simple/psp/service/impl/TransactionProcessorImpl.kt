package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.service.AcquirerRouter
import club.kuzyayo.simple.psp.service.TransactionProcessor
import club.kuzyayo.simple.psp.service.TransactionService
import club.kuzyayo.simple.psp.vo.*
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class TransactionProcessorImpl(
    private val transactionService: TransactionService,
    private val acquirerRouter: AcquirerRouter,
) : TransactionProcessor {

    private val logger = LoggerFactory.getLogger(TransactionProcessorImpl::class.java)

    override suspend fun process(transactionRequest: ProcessTransactionRequest): ProcessTransactionResponse {
        val transaction = transactionService.create(transactionRequest)
        return MDC.putCloseable(MDC_ID_KEY, transaction.id).use {
            withContext(MDCContext()) {
                doProcess(transaction)
            }
        }
    }

    private suspend fun doProcess(transaction: Transaction): ProcessTransactionResponse {
        logger.info("Transaction processing started")
        val acquirerClient = acquirerRouter.route(transaction)
        val sendTransactionResponse = acquirerClient.sendTransaction(transaction)
        val resultTransaction = transactionService.update(transaction, sendTransactionResponse)

        val processingResult = ProcessTransactionRs(resultTransaction, sendTransactionResponse)
        logger.info("Transaction processing finished, status = {}", processingResult.status)
        return processingResult
    }

    private class ProcessTransactionRs(
        val transaction: Transaction,
        val sendTransactionResponse: SendTransactionResponse
    ) : ProcessTransactionResponse {

        override val id: String
            get() = transaction.id

        override val status: TransactionStatus
            get() = transaction.status

        override val responseCode: ResponseCode
            get() = sendTransactionResponse.responseCode
    }

    companion object {
        const val MDC_ID_KEY = "id"
    }
}