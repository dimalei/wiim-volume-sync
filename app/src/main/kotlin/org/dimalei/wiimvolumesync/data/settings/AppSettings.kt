package org.dimalei.wiimvolumesync.data.settings

data class AppSettings(
    val wiimIpAddress: String = "",
    val volumeStep: Int = 2,
    val pubKeyHash: String = "",
    val netflixButtonUrl: String = "",
)