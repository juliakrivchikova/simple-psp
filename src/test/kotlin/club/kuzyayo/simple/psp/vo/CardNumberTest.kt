package club.kuzyayo.simple.psp.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardNumberTest {

    @Test
    fun `should not mask when card number length is less than 12`() {
        assertEquals("01234567890", CardNumber("01234567890").toString())
    }

    @Test
    fun `should mask with 4 leading and 4 trailing digits when card number has minimal length`() {
        assertEquals("1111****1111", CardNumber("111111111111").toString())
    }

    @Test
    fun `should mask with 6 leading and 4 trailing digits when card number length is 16`() {
        assertEquals("012345****3456", CardNumber("0123456789123456").toString())
    }
}