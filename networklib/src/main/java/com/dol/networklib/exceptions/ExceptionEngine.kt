package com.dol.networklib.exceptions

import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * Created by dlj on 2019/9/23.
 *  customer exceptions
 */
object ExceptionEngine {
    val SERVER_ERROR = 1001
    val SERVER_DATA_ERROR = 1002
    val CONNECT_ERROR=1003
    val CONNECT_TIMEOUT=1004

    fun handleException(e: Throwable): GeneralException {
        return when (e) {
            is HttpException -> {
                GeneralException(SERVER_ERROR, "网络错误")
            }

            is ParseException, is JSONException,is JsonParseException -> {
                GeneralException(SERVER_DATA_ERROR, "解析错误")
            }

            is ConnectException,is UnknownHostException->{
                GeneralException(CONNECT_ERROR,"连接错误")
            }

            is InterruptedException->{
                GeneralException(CONNECT_TIMEOUT,"连接超时")
            }

            is ServerException -> {
                GeneralException(SERVER_ERROR, e.msg)
            }

            else -> {
                GeneralException(SERVER_ERROR, "网络错误")
            }
        }
    }
}