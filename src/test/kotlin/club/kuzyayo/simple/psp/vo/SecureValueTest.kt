package club.kuzyayo.simple.psp.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class SecureValueTest {

    @Test
    fun `should mask all characters`() {
        assertEquals("***", SecureValue.fullyMasked("123").toString())
    }

    @Test
    fun `should return empty string when value is empty`() {
        assertEquals("", SecureValue.fullyMasked("").toString())
    }

    @Test
    fun `should consider instances as equal when value is the same`() {
        val secureValue1 = SecureValue("123", Triple(1, 1, 1))
        val secureValue2 = SecureValue("123", Triple(0, 3, 0))

        assertEquals(secureValue1, secureValue2)
        assertEquals(secureValue2.hashCode(), secureValue1.hashCode())
    }

    @Test
    fun `should consider instances as not equal when value is not the same`() {
        assertNotEquals(
            SecureValue("123", Triple(1, 1, 1)),
            SecureValue("456", Triple(1, 1, 1))
        )
    }

    @Test
    fun `should return correct string when mask has leading and trailing parts`() {
        assertEquals("123****89", SecureValue("123456789", Triple(3, 4, 2)).toString())
    }

    @Test
    fun `should return correct string when there is no leading part`() {
        assertEquals("**345", SecureValue("12345", Triple(0, 2, 3)).toString())
    }

    @Test
    fun `should return correct string when there is no masking part`() {
        assertEquals("12345", SecureValue("12345", Triple(3, 0, 2)).toString())
    }

    @Test
    fun `should return correct string when there is no trailing part`() {
        assertEquals("123**", SecureValue("12345", Triple(3, 2, 0)).toString())
    }

    @Test
    fun `should return correct string when value is shorter than mask`() {
        assertEquals("****5", SecureValue("12345", Triple(3, 4, 2)).toString())
    }
}