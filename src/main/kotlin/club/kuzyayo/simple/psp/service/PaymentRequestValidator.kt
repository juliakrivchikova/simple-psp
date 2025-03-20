package club.kuzyayo.simple.psp.service

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.service.impl.PaymentRequestValidatorImpl
import club.kuzyayo.simple.psp.vo.ResponseCode
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest

/**
 * Interface for payment request validation.
 *
 * @see PaymentRequestValidatorImpl
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface PaymentRequestValidator {

    /**
     * Validates the provided payment request.
     *
     * This method verifies:
     * - Card number validity: if invalid, returns [ResponseCodes.INVALID_CARD_NUMBER]
     * - CVV validity: if invalid, returns [ResponseCodes.INVALID_CVV]
     * - Expiry date validity: if invalid, returns [ResponseCodes.INVALID_EXPIRY_DATE]
     * - Amount validity: if not greater than zero, returns [ResponseCodes.INVALID_AMOUNT]
     * - Currency code validity: if it does not match the three-letter uppercase format, returns [ResponseCodes.INVALID_CURRENCY]
     *
     * If all validations pass, it returns [ResponseCodes.SUCCESS].
     *
     * @param paymentRequest the payment request to validate.
     * @return a [ResponseCode] indicating the validation result.
     */
    fun validate(paymentRequest: ProcessPaymentRequest): ResponseCode
}