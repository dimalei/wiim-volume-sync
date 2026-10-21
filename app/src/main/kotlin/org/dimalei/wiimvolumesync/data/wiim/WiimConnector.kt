package org.dimalei.wiimvolumesync.data.wiim

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.dimalei.wiimvolumesync.data.settings.AppSettings
import org.dimalei.wiimvolumesync.data.settings.GuiLogger.log
import org.dimalei.wiimvolumesync.data.settings.SettingsRepository
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

const val MAX_VOL = 99
const val MIN_VOL = 0

class WiimConnector(settingsRepository: SettingsRepository) {
    val tag: String = javaClass.simpleName
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var appSettings = AppSettings()
    lateinit var client: OkHttpClient
    private var currentVolume = -1

    init {
        scope.launch {
            settingsRepository.settingsFlow.collectLatest {
                appSettings = it
                client = buildPinnedClient()
            }
        }
    }

    fun changePlayerVolume(
        direction: Volume,
    ) {
        if (currentVolume < 0) {
            getPlayerVolume()
            return
        }
        scope.launch {
            val targetVol = currentVolume + appSettings.volumeStep * direction.factor
            if (targetVol in MIN_VOL..MAX_VOL) {
                setPlayerVolume(
                    volume = targetVol,
                    onSuccess = {
                        log(tag, "Volume set to $targetVol")
                        Log.i(tag, "Volume set to $targetVol")
                        currentVolume = targetVol
                    },
                    onError = {
                        log(tag, "Failed to set volume: $it")
                        Log.i(tag, "Failed to set volume: $it")
                    }
                )
            }
        }
    }

    private fun getPlayerVolume() {
        scope.launch {
            getPlayerStatus(
                {
                    currentVolume = it.getString("vol").toInt()
                    log(tag, "Current volume set $currentVolume")
                    Log.d(tag, "Current volume set $currentVolume")
                },
                {
                    log(tag, "Fetching volume failed: $it")
                    Log.e(tag, "Fetching volume failed: $it")
                })
        }
    }

    fun getPlayerStatus(
        onSuccess: (response: JSONObject) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(appSettings.wiimIpAddress)
            .addPathSegment("httpapi.asp")
            .addQueryParameter("command", "getPlayerStatus")
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        lateinit var responseJson: JSONObject

        try {
            val response = client.newCall(request).execute()
            val reader = BufferedReader(InputStreamReader(response.body.byteStream()))
            responseJson = JSONObject(reader.readText())
            onSuccess(responseJson)
        } catch (e: Exception) {
            onError(e)
            return
        }
    }

    fun getDeviceStatus(
        onSuccess: (response: JSONObject) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(appSettings.wiimIpAddress)
            .addPathSegment("httpapi.asp")
            .addQueryParameter("command", "getStatusEx")
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        lateinit var responseJson: JSONObject

        try {
            val response = client.newCall(request).execute()
            val reader = BufferedReader(InputStreamReader(response.body.byteStream()))
            responseJson = JSONObject(reader.readText())
            onSuccess(responseJson)
        } catch (e: Exception) {
            onError(e)
            return
        }
    }

    fun setPlayerVolume(
        volume: Int,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(appSettings.wiimIpAddress)
            .addPathSegment("httpapi.asp")
            .addQueryParameter("command", "setPlayerCmd:vol:$volume")
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response: Response
        try {
            response = client.newCall(request).execute()
            onSuccess(response.toString())
        } catch (e: Exception) {
            onError(e)
            return
        }
    }


    // Generated by ChatGPT
    private fun buildPinnedClient(
    ): OkHttpClient {
        val trustManager = @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                if (chain.isEmpty()) throw CertificateException("Empty cert chain")

                // leaf cert public key -> SHA-256 -> base64
                val pubKeyDer = chain[0].publicKey.encoded
                val sha256 = MessageDigest.getInstance("SHA-256").digest(pubKeyDer)
                val actualB64 = Base64.encodeToString(sha256, Base64.NO_WRAP)

                if (actualB64 != appSettings.pubKeyHash) {
                    throw CertificateException("Public key pin mismatch")
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)

        return OkHttpClient.Builder()
            // Since you're connecting by IP and the cert won't match IP SAN, skip hostname verify ONLY for this IP.
            .hostnameVerifier { host, _ -> host == appSettings.wiimIpAddress }
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()
    }

    enum class Volume(val factor: Int) {
        UP(1),
        DOWN(-1)
    }
}

