package org.dimalei.wiimvolumesync.data.settings

object GuiLogger {
    private val loggerOut: MutableList<(String) -> Unit> = mutableListOf()

    fun registerLoggerOut(out: (String) -> Unit) {
        loggerOut.add(out)
    }

    fun removeLoggerOut(out: (String) -> Unit) {
        loggerOut.remove(out)
    }

    fun log(tag: String, message: String) {
        val msg = "$tag: $message"
        loggerOut.toList().forEach { it(msg) }
    }


}