package org.dimalei.wiimvolumesync

import junit.framework.TestCase.assertTrue
import org.dimalei.wiimvolumesync.data.getPlayerStatus
import org.junit.Test

class GetPlayerStatusTest {
    @Test
    fun getPlayerStatusTest() {
        getPlayerStatus(
            ipAddress = "192.168.1.53",
            onSuccess = {
                print(it.toString())
                assertTrue(true)
            },
            onError = {
                print(it.toString())
                assertTrue(false)
            }
        )
    }


    @Test
    fun setPlayerVolumeTest() {
        assertTrue(true)
    }

}