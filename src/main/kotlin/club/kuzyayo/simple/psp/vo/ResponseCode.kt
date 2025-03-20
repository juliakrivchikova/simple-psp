package club.kuzyayo.simple.psp.vo

import com.fasterxml.jackson.annotation.JsonIgnore

enum class ResponseType {
    SUCCESS, FAILURE, ERROR
}

data class ResponseCode(val code: String, val type: ResponseType, val message: String) {
    @JsonIgnore
    fun isSuccessful() = type == ResponseType.SUCCESS
}

