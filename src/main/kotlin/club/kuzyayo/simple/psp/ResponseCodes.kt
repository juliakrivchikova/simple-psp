package club.kuzyayo.simple.psp

import club.kuzyayo.simple.psp.vo.ResponseCode
import club.kuzyayo.simple.psp.vo.ResponseType

object ResponseCodes {

    val SUCCESS = ResponseCode("00", ResponseType.SUCCESS, "Success")

    val INVALID_CARD_NUMBER = ResponseCode("01", ResponseType.FAILURE, "Invalid Card number")

    val INVALID_EXPIRY_DATE = ResponseCode("02", ResponseType.FAILURE, "Invalid expiry date")

    val INVALID_AMOUNT = ResponseCode("03", ResponseType.FAILURE, "Invalid amount")

    val INVALID_CURRENCY = ResponseCode("04", ResponseType.FAILURE, "Invalid currency")

    val INVALID_CVV = ResponseCode("05", ResponseType.FAILURE, "Invalid CVV")

    val TRANSACTION_REJECTED = ResponseCode("06", ResponseType.FAILURE, "Transaction rejected")

    val SYSTEM_ERROR = ResponseCode("07", ResponseType.ERROR, "System error")
}