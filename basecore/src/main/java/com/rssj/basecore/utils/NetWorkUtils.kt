package com.rssj.basecore.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


/**
 * Description:
 * Date：2019/7/15-12:12
 * Author: cwh
 */
object NetWorkUtils {
    /**
     * check NetworkAvailable
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager ?: return false

        val info = manager.activeNetworkInfo
        return !(null == info || !info.isAvailable)
    }


    /**
     * check is3G
     * @param context
     * @return boolean
     */
    @SuppressLint("MissingPermission")
    fun is3G(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_MOBILE
    }

    /**
     * isWifi
     * @param context
     * @return boolean
     */
    @SuppressLint("MissingPermission")
    fun isWifi(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * is2G
     * @param context
     * @return boolean
     */
    @SuppressLint("MissingPermission")
    fun is2G(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null && (activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_EDGE
                || activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
            .subtype == TelephonyManager.NETWORK_TYPE_CDMA)
    }

    /**
     * is 4G
     */
    @SuppressLint("MissingPermission")
    fun is4G(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null
                && activeNetInfo.isAvailable
                && activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_LTE
    }

    /**
     * is wifi on
     */
    @SuppressLint("MissingPermission")
    fun isWifiEnabled(context: Context): Boolean {
        val mgrConn = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mgrTel = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return mgrConn.activeNetworkInfo != null && mgrConn
            .activeNetworkInfo!!.state == NetworkInfo.State.CONNECTED || mgrTel
            .networkType == TelephonyManager.NETWORK_TYPE_UMTS
    }

    /**
     * Open the settings of wireless.
     * 打开WiFi设置，去开启wifi
     */
    fun openWirelessSettings(context: Context) {
        context.applicationContext.startActivity(
            Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    /**
     * 通过ping来判断网络是否可用
     * 需要申请网络权限
     *
     * @param ip ping的ip地址，默认223.5.5.5
     */
    fun isNetWorkAvailableByPing(ip: String = "223.5.5.5"): Boolean {
        val result = execuCmd(
            arrayOf(String.format("ping -c 1 %s", ip)),
            isRooted = false, isNeedResultMsg = true
        )
        val ret = result.result == 0
        if (result.errorMsg != null) {
            Log.d("NetworkUtils", "isAvailableByPing() called" + result.errorMsg)
        }
        if (result.successMsg != null) {
            Log.d("NetworkUtils", "isAvailableByPing() called" + result.successMsg)
        }
        return ret
    }

    /**
     * 是否真正的有网络 在 M 以上版本才可以调用
     *
     * 需要申请网络权限
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailableOnM(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * 获取设备当前IP地址
     */
    @SuppressLint("MissingPermission")
    fun currentIP(context: Context): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            if (activeNetInfo.isConnected) {
                return when {
                    activeNetInfo.type == ConnectivityManager.TYPE_MOBILE -> getMobileIp()
                    activeNetInfo.type == ConnectivityManager.TYPE_WIFI -> this.getWifiIp(context)
                    else -> null
                }
            }
            return null
        } else {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            return if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> getMobileIp()
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> getWifiIp(
                        context
                    )
                    else -> null
                }
            } else {
                return null
            }
        }
        return null
    }

    /**
     * 获取Mobile情况下IP
     */
    private fun getMobileIp(): String? {
        try {
            //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取WIFI状况下IP地址
     */
    @SuppressLint("MissingPermission")
    private fun getWifiIp(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return intIP2StringIP(wifiInfo.ipAddress)
    }


    private fun intIP2StringIP(ip: Int): String {
        return ((ip and 0xFF)).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }


    private val LINE_SEP = System.getProperty("line.separator")


    /**
     * 执行linux命令行
     * @param commands 命令
     * @param isRooted 是否root
     * @param isNeedResultMsg 是否需要返回信息
     */
    fun execuCmd(
        commands: Array<String>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        var result = -1
        if (commands == null || commands.isEmpty()) {
            return CommandResult(result, "", "")
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh")
            os = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                if (command == null) continue
                os!!.write(command.toByteArray())
                os!!.writeBytes(LINE_SEP)
                os!!.flush()
            }
            os!!.writeBytes("exit$LINE_SEP")
            os!!.flush()
            result = process!!.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(
                    InputStreamReader(process.inputStream, "UTF-8")
                )
                errorResult = BufferedReader(
                    InputStreamReader(process.errorStream, "UTF-8")
                )
                var line: String?
                successResult?.let {
                    line = successResult!!.readLine()
                    if (line != null) {
                        successMsg.append(line)
                        line = successResult!!.readLine()
                        while (line != null) {
                            successMsg.append(LINE_SEP).append(line)
                            line = successResult.readLine()
                        }
                    }
                }
                errorResult?.let {
                    line = errorResult!!.readLine()
                    if (line != null) {
                        errorMsg.append(line)
                        line = errorResult!!.readLine()
                        while (line != null) {
                            errorMsg.append(LINE_SEP).append(line)
                            line = errorResult!!.readLine()
                        }
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (os != null) {
                    os!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                if (successResult != null) {
                    successResult!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                if (errorResult != null) {
                    errorResult!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            process?.destroy()
        }
        return CommandResult(
            result,
            successMsg?.toString() ?: "",
            errorMsg?.toString() ?: ""
        )
    }

    /**
     * The result of command.
     */
    class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {

        override fun toString(): String {
            return "result: " + result + "\n" +
                    "successMsg: " + successMsg + "\n" +
                    "errorMsg: " + errorMsg
        }
    }
}