package org.dimalei.wiimvolumesync.data.netflixbutton

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.dimalei.wiimvolumesync.data.settings.GuiLogger.log

object SimpleHttpRequester {
    val tag: String = javaClass.simpleName
    private val scope = CoroutineScope(Dispatchers.IO)
    var client = OkHttpClient()


    fun send(url: String?) {
        scope.launch {
            sendGet(
                url = url ?: "",
                onSuccess = {
                    log(tag, "successful fetch: $it")
                    Log.i(tag, "successful fetch: $it")
                },
                onError = {
                    log(tag, "failed to fetch: $it")
                    Log.e(tag, "failed to fetch: $it")
                }
            )
        }
    }

    private fun sendGet(
        url: String,
        onSuccess: (String) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        try {
            val request = Request.Builder()
                .url(url)
                .build()
            val response: Response = client.newCall(request).execute()
            onSuccess(response.toString())
        } catch (e: Exception) {
            onError(e)
            return
        }
    }

}