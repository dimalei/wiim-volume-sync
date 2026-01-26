package org.dimalei.wiimvolumesync

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.dimalei.wiimvolumesync.data.getPlayerStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetPlayerStatusTest {
    val ip = "192.168.1.53"
    val pinBase = "p2NKrN70qaSrxvfXASCT+A0/iKenUHL27yU1rNmCz64="

    @Test
    fun getPlayerStatusTest() {

        getPlayerStatus(
            ipAddress = ip,
            expectedPinBase64 = pinBase,
            onSuccess = {
                Log.i("test", it.toString())
                Assert.assertTrue(
                    it.has("project")
                )
            },
            onError = {
                print(it.toString())
                Assert.assertTrue(false)
            }
        )
    }

    @Test
    fun setVolumeTest() {
        getPlayerStatus(
            ipAddress = ip,
            expectedPinBase64 = pinBase,
            onSuccess = {
                Log.i("test", it.toString())
                Assert.assertTrue(true)
            },
            onError = {
                print(it.toString())
                Assert.assertTrue(false)
            }
        )
    }


}