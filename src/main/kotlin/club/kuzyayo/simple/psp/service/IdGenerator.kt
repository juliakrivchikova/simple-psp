package club.kuzyayo.simple.psp.service

/**
 * Interface for generating unique identifiers.
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface IdGenerator {

    /**
     * Generates a unique identifier.
     *
     * @return a string representation of a generated identifier.
     */
    fun generate(): String
}