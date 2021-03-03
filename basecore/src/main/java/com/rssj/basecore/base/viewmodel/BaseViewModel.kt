package com.rssj.basecore.base.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rssj.basecore.REQ_SUC
import com.rssj.basecore.base.Entity
import com.rssj.basecore.base.Event
import com.rssj.basecore.base.ExceptionHandle
import com.rssj.basecore.base.repository.IRepository
import com.rssj.basecore.utils.LogUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Description:BaseViewModel 主要的数据逻辑操作在ViewModel中执行
 * 通过LiveData 或者 DataBinding驱动数据绑定到UI
 *
 * 为避免观察livedata多个实例，livedata应为val
 * Transformations.map()，根据一个livedata实例生成另一个livedata实例，主要是观察
 * livedata的具体数据的变化，livedata的具体数据变化时，则其生成的实例的数据也发生变化
 *  Transformations.switchMap() 根据一个livedata实例生成另一个livedata实例，主要是观察
 *  livedata的变化和其值得变化，当这个livedata变化时，其生成的实例livedata的数据发生变化
 *
 *
 * Date：2019/12/31 0031-15:36
 * Author: cwh
 */
abstract class BaseViewModel<T : IRepository>(application: Application) :
    AndroidViewModel(application)
    , IBaseViewModel {
    //带加载状态的结果,可以在各个加载时的函数中
    //通过传不同的value来进行加载状态的修改
    //val data=MutableLiveData<Result<String>>()

    abstract val repo: T

    val mDefUIEvent = DefUIEvent()

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            block()
        }
    }


    /**
     * 指定在IO线程中调用
     */
    suspend fun <R> launchOnIO(block: suspend CoroutineScope.() -> R) {
        withContext(Dispatchers.IO) {
            block()
        }
    }

    /**
     * 异常处理
     * @param block 正常处理逻辑
     * @param error 出现错误时的处理逻辑
     * @param finally 不管出现异常与否的最终处理
     *
     */
    private suspend fun handleException(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(Throwable) -> Unit,
        finally: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                block()
            } catch (e: Exception) {
                LogUtils.e(
                    "ViewModel Error e.toString = " + e.toString() + "\n" + "exception:"
                            + e.message + "\n" + "cause: " + e.cause
                )
                if (e !is CancellationException) {
                    error(e)
                }
            } finally {
                finally()
            }
        }

    }

    /**
     * 异常处理
     * @param block 正常处理逻辑,返回请求的结果
     * @param success 正常请求成功获得结果后的处理
     * @param error 出现错误时的处理逻辑
     * @param finally 不管出现异常与否的最终处理
     *
     */
    private suspend fun <R> handleException(
        block: suspend CoroutineScope.() -> Entity<R>,
        success: suspend CoroutineScope.(Entity<R>) -> Unit,
        error: suspend CoroutineScope.(Throwable) -> Unit,
        finally: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                success(block())
            } catch (e: Exception) {
                LogUtils.e(
                    "ViewModel Error e.toString = " + e.toString() + "\n" + "exception:"
                            + e.message + "\n" + "cause: " + e.cause
                )
                if (e !is CancellationException) {
                    error(e)
                }
            } finally {
                finally()
            }
        }

    }

    /**
     * catch 了所有的在block中的Exception,不会出现抛出异常，
     * 所有Exception会在OnError中处理
     *
     * 对于Retrofit中使用时，在Service中定义的挂起函数在被调用时
     * 会自动挂起到子线程执行，我们不需要再切换到子线程
     *
     * @param isLaunchOnIO 是否在IO线程运行Block(默认在主线程运行)
     * @param onStart 开始前的处理
     * @param block 需要处理的逻辑(包括数据的请求和UI逻辑的处理，在isLaunchOnIo为false时，
     *              若为true,需要自己切换的主线程修改UI)
     * @param onError 出现Exception时的逻辑
     * @param onComplete 不管处理成功或出现异常后的处理逻辑
     *
     */
    fun launch(
        isLaunchOnIO: Boolean = false,
        onStart: () -> Unit = {},
        block: suspend CoroutineScope.() -> Unit,
        onError: (ExceptionHandle.ResponseThrowable) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        launchOnUI {
            handleException({
                onStart()
                if (isLaunchOnIO) {
                    launchOnIO { block() }
                } else {
                    block()
                }
            }, {
                onError(ExceptionHandle.handleException(it))
                mDefUIEvent.mToastEvent.value = Event(
                    "${ExceptionHandle.handleException(it).code} :" +
                            " ${ExceptionHandle.handleException(it).message}"
                )
            }, {
                onComplete()
            })

        }
    }


    /**
     * catch 了所有的在block和在success中中的Exception,不会出现抛出异常，
     * 所有Exception会在OnError中处理
     *
     * 对于Retrofit中使用时，在Service中定义的挂起函数在被调用时
     * 会自动挂起到子线程执行，我们不需要再切换到子线程
     *
     * @param isLaunchOnIO 是否在IO线程运行Block(默认在主线程运行)
     * @param onStart 开始前的处理
     * @param block 需要处理的逻辑
     * @param onSuccess 对于请求成功结果的处理，返回结果成功，对于成功结果的处理
     * @param onFail 对于请求成功，返回失败结果的处理
     * @param onError 出现Exception时的逻辑
     * @param onComplete 不管处理成功或出现异常后的处理逻辑
     *
     *
     */
    fun <R> launch(
        isLaunchOnIO: Boolean = false,
        onStart: () -> Unit = {},
        block: suspend CoroutineScope.() -> Entity<R>,
        onSuccess: (R) -> Unit,
        onFail: (String, Int) -> Unit = { msg, _ ->
            isShowLoadingDialog(false)
            mDefUIEvent.mToastEvent.value = Event(msg)
        },
        onError: (ExceptionHandle.ResponseThrowable) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        launchOnUI {
            handleException({
                //data.postValue(Result.loading(null))
                onStart()
                if (isLaunchOnIO) {
                    withContext(Dispatchers.IO) {
                        block()
                    }
                } else {
                    block()
                }
            }, { result ->
                if (result.code == REQ_SUC) {
                    //data.value= Result.success(result.data)
                    onSuccess(result.data)
                } else {
                    onFail(result.msg, result.code)
                    //data.value= Result.fail(result.msg,result.code)
                }
            }, {
                onError(ExceptionHandle.handleException(it))
                //data.value= Result.error(it.message,null)
                mDefUIEvent.mToastEvent.value = Event(
                    "${ExceptionHandle.handleException(it).code} :" +
                            " ${ExceptionHandle.handleException(it).message}"
                )
            }, {
                onComplete()
            })
        }

    }


    /**
     * 返回Flows数据，可以进行类似于RxJava的
     * 一系列操作符对数据进行操作
     *
     * 如：flow{
    emit(block())
    }.flowOn(Dispatchers.IO).map{}

     * 对于使用该方法，需要在协程作用域中使用，
     * (含有suspend函数)
     *
     *  在主线程的协程中使用即可，需要切到其他线程，通过flowOn即可
     *  launchOnUI{
     *      launchFlow{
     *
     *      }.flowOn(Dispatchers.IO).map{}
     *  }
     *
     */
    fun <R> launchFlow(block: suspend () -> R): Flow<R> {
        return flow {
            emit(block())
        }
    }

    /**
     * 是否展示加载对话框
     */
    protected fun isShowLoadingDialog(isShow: Boolean) {
        mDefUIEvent.isShowLoadView.value = Event(isShow)
    }


    /**
     * 一些常用的UI事件，通过LiveData来触发的封装
     *如：Toast 跳转Activity等
     *
     */
    inner class DefUIEvent {
        /**
         * Toast Event,设置value时，显示Toast
         */

        val mToastEvent = MutableLiveData<Event<String>>()


        /**
         * 是否显示正在加载的View  true:显示，false: 隐藏
         */

        val isShowLoadView = MutableLiveData<Event<Boolean>>()

        /**
         * 数据的加载状态，根据不同状态显示不同UI
         */
        val mLoadingState = MutableLiveData<Event<ViewState>>()

        /**
         * 触发返回事件
         */
        val onBackEvent = MutableLiveData<Event<Unit>>()

        /**
         * finish当前Activity
         */
        val onFinishEvent = MutableLiveData<Event<Unit>>()

        /**
         * 开启Activity(最常规的跳转Activity,不能携带参数和指定开始Activity mode)
         */
        val mStartActivity = MutableLiveData<Event<Class<*>>>()
    }

    /**
     * 数据的加载状态
     */
    enum class ViewState {

        LOADING,

        SUCCESS,

        FAILURE
    }

    override fun onAny() {
    }

    override fun onCreate() {
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPauese() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }
}