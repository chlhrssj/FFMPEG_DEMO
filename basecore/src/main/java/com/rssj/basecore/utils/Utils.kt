package com.rssj.basecore.utils


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.HashMap
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.net.*


/**
 * Description:常用的一些工具类方法
 * Date：2019/7/15-11:37
 * Author: cwh
 */
object Utils {

    /**
     * 格式储存数据的大小，进行单位变化  B KB MB GB TB
     *
     * @param size 需要格式化存储的大小 单位B
     * @param num 默认格式化保留的小数点后尾数
     * @return 格式化后大小 B KB MB GB TB
     */
    fun formatMemoeySize(size: Double, num: Int = 2): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "B"
        }

        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(num, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(num, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(num, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(num, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    /**
     * 格式储存数据的大小，进行单位变化  B KB M GB TB
     *
     * @param size 需要格式化存储的大小 单位B
     * @param num 默认格式化保留的小数点后尾数
     * @return
     */
    fun formatMemorySize(size: String, num: Int = 2): String {
        return formatMemoeySize(size.toDouble(), num)
    }


    /**
     * 获取当前App version name
     *
     * @return
     */
    fun getAppVersionName(context: Context): String {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.applicationContext.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return packageInfo?.versionName ?: ""
    }

    /**
     * 获取当前App version code
     */
    fun getAppVersionCode(context: Context): Long {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.applicationContext.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode ?: 0
        } else {
            packageInfo?.versionCode?.toLong() ?: 0
        }
    }

    /**
     * 获取App名称
     */
    fun getAppName(context: Context): String? {
        var appName: String? = null
        try {
            val packageManager = context.packageManager
            val info = packageManager.getApplicationInfo(context.packageName, 0)
            info?.let {
                appName = packageManager.getApplicationLabel(it) as String?
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return appName
    }

    /**
     * 获取当前系统版本
     *
     */
    fun getSysVersionCode():String{
        return Build.VERSION.SDK_INT.toString()
    }




    /**
     * 格式化数字结果，除去多余的0
     * 如：0.20000 ->  0.2
     */
    fun formatNum(numStr: String?): String? {
        if (numStr == null) {
            return null
        }
        val index = numStr.split(".")
        if (index.size <= 1) {
            return numStr
        }
        val lastStrIndex = index[1].indexOfLast {
            it != '0'
        }
        return if (lastStrIndex == -1) {
            index[0]
        } else {
            index[0] + "." + index[1].substring(0, lastStrIndex + 1)
        }
    }

    /**
     * 格式化数字结果，保留六位小数
     * @param isAbout 是否四舍五入
     */
    fun formatNumToStr(numStr: String?, isAbout: Boolean = false): String? {
        return if (numStr.isNullOrEmpty()) {
            numStr
        } else {
            val decimalFormat = DecimalFormat("0.000000")
            //部分语言环境下，小数点会变为，，这里强制设置为“.”,以免数字转换出错
            val decimalFormatSymbols = DecimalFormatSymbols()
            decimalFormatSymbols.decimalSeparator = '.'
            decimalFormat.decimalFormatSymbols = decimalFormatSymbols
            if (isAbout) {
                decimalFormat.format(numStr!!.toDouble())
            } else {
                val bigDecimal = BigDecimal(numStr!!)
                decimalFormat.format(bigDecimal.setScale(6, BigDecimal.ROUND_DOWN))
            }

        }
    }

    /**
     * 格式化数字结果，保留六位，如果为零，出去小数后面零
     */
    fun formatNumToStrNoZero(numStr: String?): String? {
        return formatNum(
            formatNumToStr(
                numStr
            )
        )
    }

    /**
     * @param numStr 需要保留位数的数
     * @param num 保留位数
     * @param isAbout 是否四舍五入
     * 格式化数字，根据大小保留位数
     *
     */
    fun formatNumToStrWithNum(numStr: String?, num: Int, isAbout: Boolean = false): String? {
        if (numStr.isNullOrEmpty()) {
            return numStr
        }
        val decimalFormat = getDecimalFormat(num)
        val bigDecimal = BigDecimal(numStr!!)
        return if (!isAbout) {
            decimalFormat.format(bigDecimal.setScale(num, BigDecimal.ROUND_DOWN))
        } else {
            decimalFormat.format(numStr.toDouble())
        }
    }

    /**
     * @param num 返回小数点后0的个数
     * 返回保留多少位的 0.0000,
     */
    private fun getDecimalFormat(num: Int): DecimalFormat {
        val stingBuffer = StringBuffer()
        stingBuffer.append("0").append(".")
        for (i in 0 until num) {
            stingBuffer.append("0")
        }
        val decimalFormat = DecimalFormat(stingBuffer.toString())
        //部分语言环境下，小数点会变为，，这里强制设置为“.”,以免数字转换出错
        val decimalFormatSymbols = DecimalFormatSymbols()
        decimalFormatSymbols.decimalSeparator = '.'
        decimalFormat.decimalFormatSymbols = decimalFormatSymbols
        return decimalFormat
    }

    /**
     * 动态格式化小数点位数,直接截取
     *
     */
    fun formatNumToStr(numStr: String): String {
        if (TextUtils.isEmpty(numStr) || !numStr.contains(".")) {
            return numStr
        }
        return if (numStr.startsWith("0.")) {
            if (numStr.startsWith("0.0000")) {
                numStr.substring(0, Math.min(10, numStr.length))
            } else {
                numStr.substring(0, Math.min(6, numStr.length))
            }
        } else {
            val dot = numStr.indexOf(".")
            numStr.substring(0, Math.min(dot + 3, numStr.length))
        }
    }

    /**
     * 根据url获取url后面携带的key--value,
     * 可能url中的参数进行了编码，这里需要进行下解码
     */
    fun formatUrlParams(url: String?): Map<String, String>? {
        if (url.isNullOrEmpty()) {
            return null
        }
        val index = url.indexOf("?")
        if (index == -1) {
            return null
        }
        val paramsStr = url.substring(index + 1)
        if (paramsStr.isNullOrEmpty()) {
            return null
        }
        val keyValue = paramsStr.split("&")
        val kvMap = HashMap<String, String>()
        for (kv in keyValue) {
            val kvStr = kv.split("=")
            kvMap[URLDecoder.decode(kvStr[0], "utf-8")] = URLDecoder.decode(kvStr[1], "utf-8")
        }
        return kvMap
    }

    /**
     * string转换为TextView可以显示的html
     */
    fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    /**
     * 获取相册路径
     */
    fun getDCIMPath(): String? {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (dir.isDirectory) {
            var path = dir.absolutePath
            val list = dir.listFiles()
            for (i in list!!.indices) {
                val d = list[i]
                if (d.isDirectory && "Camera" == d.name) {
                    path = d.absolutePath
                }
            }
            return path
        }
        return null
    }

    /**
     * 弹出键盘
     */
    fun showKeyboard(v: View) {
        v.post {
            val imm = v.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * 隐藏键盘
     */
    fun hideKeyboard(v: View) {
        v.post {
            val imm = v.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
            }
        }

    }

    /**
     * 复制到粘贴板
     */
    fun copyBoard(context: Context, str: String) {
        val boardManager = context.getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager
        boardManager.setPrimaryClip(ClipData.newPlainText(null, str))
    }

    /**
     * 获取资源id 如：获取一个string中的id，从而获取string  context.getString(id)
     *
     * @param name 资源id名称
     * @param type  资源类型，如：string mipmap 等
     */
    fun getResId(context: Context, name: String, type: String): Int {
        return context.resources.getIdentifier(name, type, context.packageName)
    }

    /**
     * 是否可以跳转到指定activity
     */
    fun isIntentAvailable(intent: Intent, context: Context): Boolean {
        return context
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size > 0
    }

    /**
     * Launch the application's details settings.
     * 跳转到setting 界面 用于权限请求跳转设置界面
     */
    fun launchAppDetailsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        if (!isIntentAvailable(intent, context)) return
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


    /**
     * 获取手机ip
     */
    fun getLocalIP(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val ni = en.nextElement() as NetworkInterface
                val netAddress = ni.inetAddresses
                while (netAddress.hasMoreElements()) {
                    val inetAddress = netAddress.nextElement() as InetAddress
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



}