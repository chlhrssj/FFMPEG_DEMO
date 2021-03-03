package com.rssj.basecore.base

import android.util.Log

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException

import org.json.JSONException

import java.net.ConnectException
import java.net.SocketTimeoutException

import retrofit2.HttpException
import java.io.IOException


/**
 * Exception  handle
 */
class ExceptionHandle {


    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        val UNKNOWN = 1000

        /**
         * 解析错误
         */
        val PARSE_ERROR = 1001

        /**
         * 网络错误
         */
        val NETWORD_ERROR = 1002

        /**
         * 协议出错
         */
        val HTTP_ERROR = 1003

        /**
         * 无法连接到服务器
         */
        val SERVER_ADDRESS_ERROR = 1004

        /**
         * 证书出错
         */
        val SSL_ERROR = 1005

        /**
         * 无网络
         */
        val NO_NETWORK = 1006

        val UNKONW_HOST_EXCEPTION = 1007

        /**
         * Token 过期
         */
        val TOKEN_EXPIRED_EXCEPTION = 1008

        /**
         * 返回数据错误
         * 接口请求成功，但是返回数据错误，实际为接口请求失败
         */
        val REPONSE_MSG_ERROE=1009
    }

    class ResponseThrowable(throwable: Throwable, var code: Int) : Exception(throwable) {
        override var message: String? = null
    }

    /**
     * ServerException发生后，将自动转换为ResponeThrowable 返回在onError(e)中
     */
    internal inner class ServerException : RuntimeException() {
        var code: Int = 0
        override var message: String? = null
    }

    /**
     * 返回数据code不为200，数据体错误
     * 在使用Coroutines 与 okhttp中，在拦截器中抛出异常，其异常只能是
     * IOException的子类，否则报错
     *
     * https://stackoverflow.com/questions/58697459/handle-exceptions-thrown-by-a-custom-okhttp-interceptor-in-kotlin-coroutines
     *
     *
     * https://stackoverflow.com/questions/65359590/interceptor-throws-ioexception-unknownhostexception-when-i-turn-internet-off
     *
     * 但在RxJava结合Okhttp中，却可以在拦截器中抛出任何类型的错误，RxJava自己做了处理(暂不知如何处理的。。。)
     *
     */
    class ResponseMsgErrorException(val code:Int,val msg: String) : IOException(msg)

    companion object {

        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504

        fun handleException(e: Throwable): ResponseThrowable {
            val ex: ResponseThrowable
            Log.e(
                "Error tag", "e.toString = " + e.toString() + "\n" + "exception:"
                        + e.message + "\n" + "cause: " + e.cause
            )
            e.printStackTrace()//打印出堆栈信息，看出错的地方
            if (e is HttpException) {
                ex = ResponseThrowable(e, ERROR.HTTP_ERROR)
                when (e.code()) {
                    UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT,
                    INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE -> {
                        ex.code = e.code()
                        ex.message = "网络错误"
                    }
                    else -> {
                        ex.code = e.code()
                        ex.message = "网络错误"
                    }
                }
                return ex
            } else if (e is ServerException) {
                ex = ResponseThrowable(e, e.code)
                ex.message = e.message
                return ex
            } else if (e is ResponseMsgErrorException) {
                ex = ResponseThrowable(e, e.code)
                ex.message =e.msg
                return ex
            } else if (e is JsonParseException
                || e is JSONException ||
                e is MalformedJsonException
            ) {
                ex = ResponseThrowable(e, ERROR.PARSE_ERROR)
                ex.message = "解析错误"
                return ex
            } else if (e is ConnectException) {
                ex = ResponseThrowable(e, ERROR.NETWORD_ERROR)
                ex.message = "连接失败"
                return ex
            } else if (e is SocketTimeoutException) {
                //"无法连接到服务器"
                ex = ResponseThrowable(e, ERROR.SERVER_ADDRESS_ERROR)
                ex.message = "连接服务器超时"
                return ex
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                ex = ResponseThrowable(e, ERROR.SSL_ERROR)
                ex.message = "证书验证失败"
                return ex
            } else if (e is java.net.UnknownHostException) {
                ex = ResponseThrowable(e, ERROR.UNKONW_HOST_EXCEPTION)
                ex.message = "未知HOST"
                return ex
            } else {
                ex = ResponseThrowable(e, ERROR.UNKNOWN)
                ex.message = "未知错误"
                return ex
            }/*|| e instanceof ParseException*/
        }
    }

}
