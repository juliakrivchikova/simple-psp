package club.kuzyayo.simple.psp.controller

import club.kuzyayo.simple.psp.ResponseCodes
import club.kuzyayo.simple.psp.ResponseCodes.INVALID_CARD_NUMBER
import club.kuzyayo.simple.psp.domain.TransactionStatus
import club.kuzyayo.simple.psp.vo.api.ProcessPaymentResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
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
        val request = paymentRequest("4242424242424242")

        //when
        val responseEntity: ResponseEntity<ProcessPaymentResponse> = runBlocking {
            client.exchange(
                URI("/api/v1/payments"),
                HttpMethod.POST,
                HttpEntity(
                    request,
                    HttpHeaders().apply {
                        contentType = MediaType.APPLICATION_JSON
                    }),
                ProcessPaymentResponse::class.java
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
        val request = paymentRequest("4111111111111111")

        //when
        val responseEntity: ResponseEntity<ProcessPaymentResponse> = runBlocking {
            client.exchange(
                URI("/api/v1/payments"),
                HttpMethod.POST,
                HttpEntity(
                    request,
                    HttpHeaders().apply {
                        contentType = MediaType.APPLICATION_JSON
                    }),
                ProcessPaymentResponse::class.java
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
        val request = paymentRequest("invalidCardNumber")

        //when
        val responseEntity: ResponseEntity<ProcessPaymentResponse> = runBlocking {
            client.exchange(
                URI("/api/v1/payments"),
                HttpMethod.POST,
                HttpEntity(
                    request,
                    HttpHeaders().apply {
                        contentType = MediaType.APPLICATION_JSON
                    }),
                ProcessPaymentResponse::class.java
            )
        }

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        assertNotNull(responseEntity.body)
        assertEquals(INVALID_CARD_NUMBER, responseEntity.body!!.error)
    }

    private fun paymentRequest(cardNumber: String) = "{\n" +
            "  \"cardNumber\": \"$cardNumber\",\n" +
            "  \"expiryDate\": {\n" +
            "    \"month\": 12,\n" +
            "    \"year\": 2029\n" +
            "  },\n" +
            "  \"cvv\": \"123\",\n" +
            "  \"amount\": {\n" +
            "    \"value\": 100.50,\n" +
            "    \"currency\": \"USD\"\n" +
            "  },\n" +
            "  \"merchantId\": \"merchant123\"\n" +
            "}"
}