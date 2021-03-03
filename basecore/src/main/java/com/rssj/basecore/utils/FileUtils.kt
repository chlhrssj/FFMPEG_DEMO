package com.rssj.basecore.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import java.io.*


/**
 * Description:
 * Date：2019/7/15-14:49
 * Author: cwh
 */
object FileUtils {

    /**
     * 图片转换为JPG
     */
    private fun convertImg2JPG(sourceFile: File, targetFile: File) {
        val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
        try {
            val bos = BufferedOutputStream(FileOutputStream(targetFile))
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                bos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 获取文件夹大小 (在线程中执行)
     *
     * @param file File实例(文件夹)
     * @return long
     */
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    size += getFolderSize(fileList[i])

                } else {
                    size += fileList[i].length()

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //return size/1048576;
        return size
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    fun deleteFile(file: File) {
        if (file.isFile) {
            file.delete()
            return
        }
        if (file.isDirectory) {
            val childFile = file.listFiles()
            if (childFile == null || childFile.isEmpty()) {
                file.delete()
                return
            }
            for (f in childFile) {
                deleteFile(f)
            }
            file.delete()
        }
    }


    /**
     * uri to file
     */
    fun uri2File(uri: Uri, context: Context): File? {
        var path: String? = null
        if ("file" == uri.scheme) {
            path = uri.encodedPath
            if (path != null) {
                path = Uri.decode(path)
                val cr = context.contentResolver
                val buff = StringBuffer()
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'$path'").append(")")
                val cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA),
                    buff.toString(),
                    null,
                    null
                )
                var index = 0
                var dataIdx = 0
                cur!!.moveToFirst()
                while (!cur.isAfterLast) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                    index = cur.getInt(index)
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cur.getString(dataIdx)
                    cur.moveToNext()
                }
                cur.close()
                if (index == 0) {
                } else {
                    val u = Uri.parse("content://media/external/images/media/$index")
                    println("temp uri is :$u")
                }
            }
            if (path != null) {
                return File(path)
            }
        } else if ("content" == uri.scheme) {
            // 4.2.2以后
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(
                uri, proj,
                null, null, null
            )
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
            cursor.close()

            return File(path)
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null
    }


    //解决Android 7.0之后的Uri安全问题
    /**
     * get uri for file
     * 注意：在命名Provider时，
     * 要为authorities=${applicationId}.FileProvider 该方法才会起效
     *
     * 解决Android 7.0之后的Uri安全问题
     */
    fun getUriForFile(context: Context?, file: File?): Uri {
        if (context == null || file == null) {
            throw NullPointerException()
        }
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider
                .getUriForFile(
                    context.applicationContext,
                    "${context.applicationInfo.packageName}.FileProvider", file
                )
        } else {
            Uri.fromFile(file)
        }
        return uri
    }

    /**
     * File to uri.
     *  注意：在命名Provider时，
     * 要为authorities=${applicationId}.FileProvider 该方法才会起效
     * @param file The file.
     * @return uri
     */
    fun file2Uri(@NonNull file: File, context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = context.applicationInfo.packageName + ".FileProvider"
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * copy File
     */
    fun copyFile(sourceFile:File,targetFile:File):Boolean{
        if(!sourceFile.exists()){
            throw NoSuchFieldError("sourceFile Not Exist")
        }
        var result=true
        try {
            FileOutputStream(targetFile).use { outStream ->
                val buffer = ByteArray(1024)
                var len = 0
                FileInputStream(sourceFile).use { inStream ->
                    while ((inStream.read(buffer).also { len = it }) > 0) {
                        outStream.write(buffer, 0, len)
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            result=false
        }
        return result
    }

    /**
     * 通过MediaStore保存，适配AndroidQ，(Android Q 保存成功自动添加到相册数据库，无需再发送广告告诉系统插入相册)
     *
     * @param context      context
     * @param sourceFile   源文件
     * @param saveFileName 保存的文件名
     * @param saveDirName  picture子目录
     * @return 成功或者失败
     */
    fun saveImageWithAndroidQ(
        context: Context,
        sourceFile: File,
        saveFileName: String,
        saveDirName: String
    ): Boolean {

            val values = ContentValues()
            values.put(MediaStore.Images.Media.DESCRIPTION, "Scenic Image")
            values.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.Media.TITLE, "Image.png")
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$saveDirName")

            val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val resolver = context.contentResolver
            val insertUri = resolver.insert(external, values)
            var inputStream: BufferedInputStream? = null
            var os: OutputStream? = null
            var result = false
            try {
                inputStream = BufferedInputStream(FileInputStream(sourceFile))
                if (insertUri != null) {
                    os = resolver.openOutputStream(insertUri)
                }
                if (os != null) {
                    val buffer = ByteArray(1024 * 4)
                    var len: Int
                    while ((inputStream!!.read(buffer)).also { len = it } != -1) {
                        os!!.write(buffer, 0, len)
                    }
                    os!!.flush()
                }
                result = true
            } catch (e: IOException) {
                result = false
            } finally {
                try {
                    inputStream?.close()
                } catch (e: java.lang.Exception) {
                }

                try {
                    os?.close()
                } catch (e: java.lang.Exception) {
                }

            }
            return result
    }


}