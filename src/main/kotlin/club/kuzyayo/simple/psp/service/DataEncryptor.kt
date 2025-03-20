package club.kuzyayo.simple.psp.service

import club.kuzyayo.simple.psp.service.impl.encryptor.AesDataEncryptor
import club.kuzyayo.simple.psp.service.impl.encryptor.NoOpDataEncryptor
import club.kuzyayo.simple.psp.vo.SecureValue

/**
 * Interface for data encryption operations.
 *
 * @see AesDataEncryptor
 * @see NoOpDataEncryptor
 *
 * @author Iuliia Svetlichnaya
 * @since 0.0.1
 */
interface DataEncryptor {

    /**
     * Encrypts provided [data].
     *
     * @param data the data to encrypt
     * @return the encrypted data
     */
    fun encrypt(data: SecureValue): String

    /**
     * Decrypts provided [data].
     *
     * @param data the data to decrypt
     * @return the decrypted data
     */
    fun decrypt(data: String): SecureValue

    /**
     * Calculates hash of provided [data].
     *
     * @param data the data to hash
     * @return the hash of the data
     */
    fun hash(data: SecureValue): String
}