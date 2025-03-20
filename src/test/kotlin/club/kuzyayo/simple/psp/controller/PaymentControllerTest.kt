package club.kuzyayo.simple.psp.controller

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.ResponseCodes.INVALID_CARD_NUMBER
import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.vo.Amount
import club.kuzyayo.simple.psp.vo.CardNumber
import club.kuzyayo.simple.psp.vo.ExpiryDate
import club.kuzyayo.simple.psp.vo.SecureValue
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentRequest
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class PaymentControllerTest {
    @Autowired
    lateinit var client: TestRestTemplate

    @Test
    fun `should return approved transaction when last digit of card number is even`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4242424242424242"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseEntity: ResponseEntity<ProcessPaymentResponse> = runBlocking {
            client.postForEntity<ProcessPaymentResponse>(
                URI("/api/v1/payments"),
                request
            )
        }

        //then
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertNotNull(responseEntity.body)
        assertNull(responseEntity.body!!.error)
        val result = responseEntity.body!!.result
        assertNotNull(result)
        assertEquals(TransactionStatus.APPROVED, result.status)
    }

    @Test
    fun `should return denied transaction when last digit of card number is odd`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("4111111111111111"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseEntity: ResponseEntity<ProcessPaymentResponse> = runBlocking {
            client.postForEntity<ProcessPaymentResponse>(
                URI("/api/v1/payments"),
                request
            )
        }

        //then
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertNotNull(responseEntity.body)
        assertEquals(ResponseCodes.TRANSACTION_REJECTED, responseEntity.body!!.error)
        val result = responseEntity.body!!.result
        assertNotNull(result)
        assertEquals(TransactionStatus.DENIED, result.status)
    }

    @Test
    fun `should return http status 400 when request is not valid`() {
        //given
        val request = ProcessPaymentRequest(
            cardNumber = CardNumber("invalidCardNumber"),
            expiryDate = ExpiryDate(month = 5, year = 2029),
            cvv = SecureValue.fullyMasked("777"),
            amount = Amount(value = 1000.toBigDecimal(), currency = "USD"),
            merchantId = "someMerchantId"
        )

        //when
        val responseEntity: ResponseEntity<ProcessPaymentResponse> = runBlocking {
            client.postForEntity<ProcessPaymentResponse>(
                URI("/api/v1/payments"),
                request
            )
        }

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        assertNotNull(responseEntity.body)
        assertEquals(INVALID_CARD_NUMBER, responseEntity.body!!.error)
    }
}