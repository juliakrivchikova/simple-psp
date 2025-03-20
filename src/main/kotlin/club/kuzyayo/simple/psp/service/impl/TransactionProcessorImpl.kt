package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.service.AcquirerRouter
import club.kuzyayo.simple.psp.service.TransactionProcessor
import club.kuzyayo.simple.psp.service.TransactionService
import club.kuzyayo.simple.psp.vo.*
import org.springframework.stereotype.Service

@Service
class TransactionProcessorImpl(
    private val transactionService: TransactionService,
    private val acquirerRouter: AcquirerRouter,
) : TransactionProcessor {

    override suspend fun process(transactionRequest: ProcessTransactionRequest): ProcessTransactionResponse {
        val transaction = transactionService.create(transactionRequest)
        val acquirerClient = acquirerRouter.route(transaction)
        val sendTransactionResponse = acquirerClient.sendTransaction(transaction)
        val resultTransaction = transactionService.update(transaction, sendTransactionResponse)

        return ProcessTransactionRs(resultTransaction, sendTransactionResponse)
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
}