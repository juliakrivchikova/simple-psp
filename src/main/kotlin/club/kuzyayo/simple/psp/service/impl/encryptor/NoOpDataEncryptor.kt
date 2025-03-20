package club.kuzyayo.simple.psp.service.impl.encryptor

import club.kuzyayo.simple.psp.service.DataEncryptor
import club.kuzyayo.simple.psp.vo.SecureValue
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service


@Primary
@Service
class NoOpDataEncryptor : DataEncryptor {
    override fun encrypt(data: SecureValue): String = data.toString()

    override fun decrypt(data: String): SecureValue = SecureValue.fullyMasked(data)

    override fun hash(data: SecureValue): String = data.toString()
}