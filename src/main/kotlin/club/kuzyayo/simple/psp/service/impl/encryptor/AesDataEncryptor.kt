package club.kuzyayo.simple.psp.service.impl.encryptor

import club.kuzyayo.simple.psp.service.DataEncryptor
import club.kuzyayo.simple.psp.vo.SecureValue
import org.springframework.stereotype.Service

@Service
class AesDataEncryptor : DataEncryptor {
    override fun encrypt(data: SecureValue): String {
        TODO("Not yet implemented")
    }

    override fun decrypt(data: String): SecureValue {
        TODO("Not yet implemented")
    }

    override fun hash(data: SecureValue): String {
        TODO("Not yet implemented")
    }
}