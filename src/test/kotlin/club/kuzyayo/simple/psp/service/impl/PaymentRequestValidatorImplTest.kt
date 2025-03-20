package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.vo.Amount
import club.kuzyayo.simple.psp.vo.CardNumber
import club.kuzyayo.simple.psp.vo.ExpiryDate
import club.kuzyayo.simple.psp.vo.SecureValue
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentRequestValidatorImplTest {
    private val paymentRequestValidator = PaymentRequestValidatorImpl()

    @Test
    fun `should return success code when request is valid`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.SUCCESS, responseCode)
    }

    @Test
    fun `should return failure code when card number length is less than 12`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CARD_NUMBER, responseCode)
    }

    @Test
    fun `should return failure code when card number length is greater than 19`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("42424242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CARD_NUMBER, responseCode)
    }


    @Test
    fun `should return failure code when card number contains other symbols`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("A242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CARD_NUMBER, responseCode)
    }

    @Test
    fun `should return failure code when card number does not pass Luhn's checksum`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4111111111111110"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CARD_NUMBER, responseCode)
    }

    @Test
    fun `should return failure code when cvv is too short`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("77"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CVV, responseCode)
    }

    @Test
    fun `should return failure code when cvv is too long`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("77777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CVV, responseCode)
    }

    @Test
    fun `should return failure code when cvv contains letters`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("ABC"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CVV, responseCode)
    }

    @Test
    fun `should return failure code when cvv contains other symbols`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("77%"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CVV, responseCode)
    }

    @Test
    fun `should return failure code when expiry date month is invalid`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 0, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_EXPIRY_DATE, responseCode)
    }

    @Test
    fun `should return failure code when expiry date year is invalid`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = -1),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_EXPIRY_DATE, responseCode)
    }

    @Test
    fun `should return failure code when card has expired`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 1, year = 2020),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_EXPIRY_DATE, responseCode)
    }

    @Test
    fun `should return failure code when amount is negative`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal().negate(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_AMOUNT, responseCode)
    }

    @Test
    fun `should return failure code when amount is zero`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = BigDecimal.ZERO, currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_AMOUNT, responseCode)
    }

    @Test
    fun `should return failure code when currency is not 3-letter code`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "$"),
            merchantId = "someMerchantId"
        )

        //when
        val responseCode = paymentRequestValidator.validate(request)

        //then
        assertEquals(ResponseCodes.INVALID_CURRENCY, responseCode)
    }
}