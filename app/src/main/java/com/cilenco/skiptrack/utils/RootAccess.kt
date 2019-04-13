package com.cilenco.skiptrack.utils

import org.jetbrains.anko.doAsync
import java.io.DataOutputStream
import java.io.IOException

/**
 * Created by Nikhil on 12/25/2015.
 *
 * A custom RootAccess object for usage in RadioControl (You may want to use topjohnwu's libus)
 */
object RootAccess {
    @JvmStatic
    fun runCommands(commands: Array<String>) {
        doAsync {
            val p: Process
            try {
                p = Runtime.getRuntime().exec("su")
                val os = DataOutputStream(p.outputStream)
                //Allows root commands to be entered line by line
                for (tmpCmd in commands) {
                    os.writeBytes(tmpCmd + "\n") //Sends commands to the terminal
                }
                os.writeBytes("exit\n")
                os.flush()
                os.close()

                p.waitFor()
            } catch (e: IOException) {
                e.printStackTrace()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

}