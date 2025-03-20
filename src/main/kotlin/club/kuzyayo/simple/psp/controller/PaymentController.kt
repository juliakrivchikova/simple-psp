package club.kuzyayo.simple.psp.controller

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.service.PaymentRequestValidator
import club.kuzyayo.simple.psp.service.TransactionProcessor
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentResponse
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentResult
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentRequestValidator: PaymentRequestValidator,
    private val transactionProcessor: TransactionProcessor,
) {

    private val logger = LoggerFactory.getLogger(PaymentController::class.java)

    @PostMapping
    suspend fun process(requestEntity: RequestEntity<ProcessPaymentRequest>): ResponseEntity<ProcessPaymentResponse> {
        logger.debug(
            "Payment request processing started for card number {} and amount {}",
            requestEntity.body?.cardNumber, requestEntity.body?.amount?.value
        )

        val paymentRequest = requestEntity.body ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        val validationResponse = paymentRequestValidator.validate(paymentRequest)

        if (!validationResponse.isSuccessful()) {
            logger.debug(
                "Payment request with card number {} and amount {} is not valid: {}",
                paymentRequest.cardNumber, paymentRequest.amount.value, validationResponse.message
            )
            return ResponseEntity(
                ProcessPaymentResponse(error = validationResponse),
                HttpStatus.BAD_REQUEST
            )
        }

        try {
            val processingResult = transactionProcessor.process(paymentRequest)

            val responseBody = ProcessPaymentResponse(
                error = if (processingResult.responseCode.isSuccessful()) {
                    null
                } else {
                    processingResult.responseCode
                },
                result = ProcessPaymentResult(
                    id = processingResult.id,
                    status = processingResult.status
                )
            )

            logger.debug(
                "Payment request processing finished for card number {} and amount {}, transaction id is {}, status is {}",
                paymentRequest.cardNumber, paymentRequest.amount.value, processingResult.id, processingResult.status
            )
            return ResponseEntity(responseBody, HttpStatus.OK)
        } catch (e: Exception) {
            logger.error(
                "Payment request processing failed for card number ${paymentRequest.cardNumber} " +
                        "and amount ${paymentRequest.amount}", e
            )
            return ResponseEntity(
                ProcessPaymentResponse(error = ResponseCodes.SYSTEM_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }
}