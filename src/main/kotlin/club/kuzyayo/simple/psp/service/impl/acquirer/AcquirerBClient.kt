package club.kuzyayo.simple.psp.service.impl.acquirer

import club.kuzyayo.simple.psp.service.AcquirerClient
import org.springframework.stereotype.Service


@Service
class AcquirerBClient(private val acquirerAClient: AcquirerAClient) : AcquirerClient by acquirerAClient