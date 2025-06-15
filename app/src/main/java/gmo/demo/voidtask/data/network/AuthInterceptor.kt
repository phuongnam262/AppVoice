package gmo.demo.voidtask.data.network

import android.text.TextUtils
import android.util.Log
import gmo.demo.voidtask.data.preference.PreferenceHelper
import gmo.demo.voidtask.util.ApiException
import gmo.demo.voidtask.util.ConstantUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class AuthInterceptor(private val requireToken: Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val url = chain.request().url()
                .newBuilder()
                .build()

            val newRequest: Request? = if (requireToken){
                chain.request().newBuilder()
                    .addHeader("api_token", PreferenceHelper.getString(ConstantUtils.API_TOKEN, ""))
                    .url(url)
                    .build()
            }else{
                chain.request().newBuilder()
                    .url(url)
                    .build()
            }
            if (newRequest != null) {
                exportCURL(newRequest)
            }
            val response = newRequest?.let { chain.proceed(it) }
            exportResponse(response?.body())
            return response!!
        }catch (e: IOException) {
            e.printStackTrace()
            throw ApiException(e.message.toString())
        }
    }

    /**
     * Show curl on debug mode
     */
    private fun exportCURL(request: Request) {
        try {
            val str = StringBuilder()
            str.append("curl -X ")
            str.append(request.method()).append(" \"")
            str.append(request.url().toString()).append("\"")
            val headerList = request.headers().toString()
                .split("\n").filter { it.isNotBlank() }
            for (header: String in headerList) {
                str.append(" -H \"").append(header).append("\"")
            }
            if (request.body() == null) {
                str.append(" -H \"").append(
                    "Content-Type: " +
                            "application/x-www-form-urlencoded;charset=UTF-8"
                ).append("\"")
            } else if (request.body()?.contentType() != null) {
                str.append(" -H \"").append(
                    "Content-Type: " +
                            request.body()?.contentType()?.toString()
                ).append("\"")
            }
            val body = bodyToString(request)
            if (!TextUtils.isEmpty(body)) {
                str.append(" -d \'").append(body).append("\'")
            }
            Log.d("API cURL", str.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Taking boy from request variable
     */
    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            ""
        }
    }

    /**
     * Show response data on debug mode
     */
    private fun exportResponse(responseBody: ResponseBody?) {
        if (responseBody == null) return
        try {
            responseBody.contentLength()
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer

            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8
            Log.d("API response", "----------------------------")
            Log.d("API response", buffer.clone().readString(charset))
            Log.d("API response", "----------------------------")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}