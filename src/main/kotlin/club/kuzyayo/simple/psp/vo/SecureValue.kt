package club.kuzyayo.simple.psp.vo

import com.fasterxml.jackson.annotation.JsonCreator
import kotlin.math.max
import kotlin.math.min


open class SecureValue(
    val value: String,
    mask: Triple<Int, Int, Int>
) {

    private val leadingPartLength: Int = mask.first
    private val maskedPartLength: Int = mask.second
    private val trailingPartLength: Int = mask.third

    @JsonCreator
    constructor(value: String) : this(value, Triple(0, value.length, 0))

    override fun toString(): String {
        return mask(value)
    }

    private fun mask(value: String): String {
        val buf = StringBuilder()

        val len = value.length
        if (len == 0) {
            return value
        }
        if (len > maskedPartLength + trailingPartLength) {
            buf.append(value.substring(0, min(leadingPartLength, len - maskedPartLength - trailingPartLength)))
        }
        for (i in 0 until maskedPartLength) {
            buf.append(REPLACEMENT_CHARACTER)
        }
        if (len > maskedPartLength) {
            buf.append(value.substring(max(maskedPartLength, len - trailingPartLength)))
        }

        return buf.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SecureValue

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        private const val REPLACEMENT_CHARACTER = '*'

        fun fullyMasked(value: String): SecureValue {
            return SecureValue(value, Triple(0, value.length, 0))
        }
    }
}

class CardNumber(value: String) : SecureValue(value, getCardNumberMask(value)) {

    override fun toString(): String {
        return if (value.length >= MIN_CHARS_FOR_MASKING) {
            super.toString()
        } else {
            value
        }
    }

    companion object {
        private const val MIN_CHARS_FOR_MASKING: Int = 12
        private const val MIN_CHARS_FOR_644_MASKING_SCHEME: Int = 16
        private val MASK_444 = Triple(4, 4, 4)
        private val MASK_644 = Triple(6, 4, 4)

        private fun getCardNumberMask(value: String): Triple<Int, Int, Int> {
            return if (value.length >= MIN_CHARS_FOR_644_MASKING_SCHEME) {
                MASK_644
            } else {
                MASK_444
            }
        }
    }
}
