package club.kuzyayo.simple.psp.service.impl.id.generator

import club.kuzyayo.simple.psp.service.IdGenerator
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
class UuidIdGenerator : IdGenerator {

    override fun generate(): String {
        return java.util.UUID.randomUUID().toString()
    }
}