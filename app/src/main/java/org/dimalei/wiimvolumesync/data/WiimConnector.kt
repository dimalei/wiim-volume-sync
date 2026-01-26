package org.dimalei.wiimvolumesync.data

import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun getPlayerStatus(
    ipAddress: String,
    onSuccess: (response: JSONObject) -> Unit,
    onError: (Exception) -> Unit
) {
    val url = "https://$ipAddress/httpapi.asp?command=getPlayerStatus"

    with(URL(url).openConnection() as HttpsURLConnection) {
        requestMethod = "GET"
        lateinit var response: JSONObject
        try {
            connect()
            val reader = BufferedReader(InputStreamReader(inputStream))
            response = JSONObject(reader.readText())
        } catch (e: Exception) {
            onError(e)
            return
        }
        onSuccess(response)
    }
}

fun setPlayerVolume(
    ipAddress: String,
    volume: Int,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val url = "https://$ipAddress/httpapi.asp?command=setPlayerCmd:vol:$volume"

    with(URL(url).openConnection() as HttpsURLConnection) {
        requestMethod = "GET"
        try {
            connect()
            if (responseCode == 200) {
                onSuccess()
            } else {
                throw IOException("Set Volume failed: $responseMessage")
            }
        } catch (e: Exception) {
            onError(e)
            return
        }
    }
}




