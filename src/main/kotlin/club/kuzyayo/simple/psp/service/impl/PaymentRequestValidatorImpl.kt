package club.kuzyayo.simple.psp.service.impl

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.service.PaymentRequestValidator
import club.kuzyayo.simple.psp.vo.ExpiryDate
import club.kuzyayo.simple.psp.vo.ResponseCode
import club.kuzyayo.simple.psp.vo.SecureValue
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.YearMonth

@Service
class PaymentRequestValidatorImpl : PaymentRequestValidator {
    override fun validate(paymentRequest: ProcessPaymentRequest): ResponseCode {
        if (!isCardNumberValid(paymentRequest.cardNumber)) {
            return ResponseCodes.INVALID_CARD_NUMBER
        }

        if (!isCvvValid(paymentRequest.cvv)) {
            return ResponseCodes.INVALID_CVV
        }

        if (!isExpiryDateValid(paymentRequest.expiryDate)) {
            return ResponseCodes.INVALID_EXPIRY_DATE
        }

        if (!isAmountValid(paymentRequest.amount.value)) {
            return ResponseCodes.INVALID_AMOUNT
        }

        if (!isCurrencyCodeValid(paymentRequest.amount.currency)) {
            return ResponseCodes.INVALID_CURRENCY
        }

        return ResponseCodes.SUCCESS
    }

    private fun isCurrencyCodeValid(currencyCode: String): Boolean {
        return CURRENCY_CODE_REGEX.matches(currencyCode)
    }

    private fun isAmountValid(amount: BigDecimal): Boolean {
        return amount > BigDecimal.ZERO
    }

    fun isExpiryDateValid(expiry: ExpiryDate): Boolean {
        if (expiry.month !in 1..12) return false

        val expiryYearMonth = YearMonth.of(expiry.year, expiry.month)
        val currentYearMonth = YearMonth.now()

        return !expiryYearMonth.isBefore(currentYearMonth)
    }

    fun isCardNumberValid(cardNumber: SecureValue): Boolean {
        if (cardNumber.value.any { !it.isDigit() }
            || cardNumber.value.length < 12
            || cardNumber.value.length > 19
        ) {
            return false
        }

        val digits = cardNumber.value.map { it.toString().toInt() }

        return calculateLuhnsChecksum(digits) % 10 == 0
    }

    fun isCvvValid(cvv: SecureValue): Boolean {
        return cvv.value.all { it.isDigit() }
                && (cvv.value.length == 3 || cvv.value.length == 4)
    }

    fun calculateLuhnsChecksum(digits: List<Int>): Int {
        return digits.reversed().mapIndexed { index, digit ->
            if (index % 2 == 1) {
                val doubled = digit * 2
                if (doubled > 9) doubled - 9 else doubled
            } else {
                digit
            }
        }.sum()
    }

    companion object {
        private val CURRENCY_CODE_REGEX = Regex("^[A-Z]{3}$")
    }
}
