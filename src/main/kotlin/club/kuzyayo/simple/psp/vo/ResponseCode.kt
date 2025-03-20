package club.kuzyayo.simple.psp.vo

enum class ResponseType {
    SUCCESS, FAILURE, ERROR
}

data class ResponseCode(val code: String, val type: ResponseType, val message: String) {
    fun isSuccessful() = type == ResponseType.SUCCESS
}

