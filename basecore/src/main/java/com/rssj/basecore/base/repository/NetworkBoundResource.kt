package com.rssj.basecore.base.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.rssj.basecore.base.Result

/**
 * Description:
 * https://developer.android.google.cn/jetpack/docs/guide
 *
 *  主要功能用于协调获取数据的源，使其 数据单一可信来源（https://developer.android.google.cn/jetpack/docs/guide#truth）
 *  一般有数据库时，本地数据库作为单一可信数据来源
 *  在请求到网络数据后，将数据存储进数据库，通过liveData,在数据库发生改变时，将
 *  最新数据重数据库返回，再显示在UI上
 *
 *  参考实例：(https://github.com/android/architecture-components-samples/blob/88747993139224a
 *  4bb6dbe985adf652d557de621/GithubBrowserSample/app/src/main/java/com/android/example/github/
 *  repository/NetworkBoundResource.kt)
 * Date：2020/3/4-17:03
 * Author: cwh
 *
 * @param RequestType  从接口获取来的数据类型
 *
 * @param ResultType   需要显示在UI上的数据类型
 */

// ResultType: Type for the Resource data. 需要显示在UI上的数据类型
// RequestType: Type for the API response. 从接口获取来的数据类型

abstract class NetworkBoundResource<ResultType, RequestType> {

    // Called to save the result of the API response into the database
    /**
     * 将从api获取的数据保存到DB
     */
    @WorkerThread
    abstract fun saveDataFromServerToDB(item: RequestType)

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    /**
     * 是否需要从Server获取数据
     */
    @MainThread
    abstract fun shouldRequestFromServer(data: ResultType?): Boolean

    // Called to get the cached data from the database.
    /**
     * 数据库获取数据
     */
    @MainThread
    abstract fun loadFromDb(): LiveData<ResultType>

    // Called to create the API call.
    /**
     * 获取server数据
     */
    @MainThread
    abstract fun loadFromServer(): RequestType

    /**
     * 将数据转为liveData返回
     */
    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    abstract fun asLiveData(): LiveData<Result<ResultType>>
}




