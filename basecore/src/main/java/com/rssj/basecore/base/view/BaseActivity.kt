package com.rssj.basecore.base.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.rssj.basecore.base.BusEvent
import com.rssj.basecore.base.Event
import com.rssj.basecore.utils.ToastUtils
import com.rssj.basecore.base.observerEvent
import com.rssj.basecore.base.viewmodel.BaseViewModel
import com.rssj.basecore.base.widget.dialog.LoadingDialog
import com.rssj.basecore.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.ParameterizedType

/**
 * Description:
 * Date：2019/12/31 0031-14:58
 * Author: cwh
 */
abstract class BaseActivity<VM : BaseViewModel<*>, V : ViewBinding> : AppCompatActivity() , CoroutineScope by MainScope() {
    /**
     *  ViewModel实例，主要用于数据操作
     *  当不需要ViewModel时，直接写明
     *  类型为NoViewModel即可
     *
     *  建议用 ktx 的 by viewModels() 创建
     */
    protected abstract val mViewModel: VM

    /**
     * ViewBinding实例
     */
    private lateinit var mBinding: V

    /**
     * 加载进度对话框
     */
    protected var mLoadingDialog: LoadingDialog? = null

    /**
     * 是否开启EventBus事件监听
     */
    protected var regEvent: Boolean = false


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        initViewBinding()
        initParams()
        registerDefUIEvent()
        initDataAndView()
        initViewObserver()
        initEventBus()
    }


    /**
     * 需要在setContentView前面初始化得一些参数
     * 例如：设置Activity竖屏等
     */
    protected open fun initParams() {

    }

    /**
     * 初始化一些数据和View
     */
    protected open fun initDataAndView() {

    }

    /**
     * 注册需要根据LiveData中数据变化，而改变UI的Observer
     */
    protected open fun initViewObserver() {

    }

    /**
     * 注册EventBus
     */
    protected open fun initEventBus() {
        if (regEvent) {
            EventBus.getDefault().register(this)
        }
    }

    /**
     * 初始化ViewBinding
     */
    private fun initViewBinding() {
        mBinding = getViewBinding()
        setContentView(mBinding.root)
        lifecycle.addObserver(mViewModel)
    }

    /**
     * 观察BaseViewModel中常用的一些事件
     */
    protected open fun registerDefUIEvent() {

        with(mViewModel.mDefUIEvent) {

            mToastEvent.observerEvent(this@BaseActivity) {
                ToastUtils.showToast(this@BaseActivity.applicationContext, it)
            }

            onBackEvent.observerEvent(this@BaseActivity) {
                onBackPressed()
            }

            onFinishEvent.observerEvent(this@BaseActivity) {
                finish()
            }

            mStartActivity.observerEvent(this@BaseActivity) {
                startActivity(Intent(this@BaseActivity, it))
            }

            isShowLoadView.observerEvent(this@BaseActivity){
                if(it){
                    showLoadingDialog()
                }else{
                    dismissLoadingDialog()
                }
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BusEvent) {
        onEvent(event)
    }

    /**
     * 执行EventBus事件
     */
    open fun onEvent(event: BusEvent) {}

    /**
     * 展示加载中对话框
     */
    protected fun showLoadingDialog() {
        mLoadingDialog?.let {
            it.showDialog()
        }
    }

    /**
     * 隐藏加载中对话框
     */
    protected fun dismissLoadingDialog() {
        mLoadingDialog?.let {
            it.dismissDialog()
        }
    }

    private val REQUEST_PERMISSON_CODE = 1001

    /**
     * check Permission
     *
     *  如果需要申请权限，进行权限申请的请求
     */
    open fun checkPermission(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        var result = true
        val needRequestPermissions = mutableListOf<String>()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED) {
                result = false
                needRequestPermissions.add(it)
            }
        }
        if (needRequestPermissions.size > 0) {
            requestPermissions(needRequestPermissions.toTypedArray(), REQUEST_PERMISSON_CODE)
        }
        return result
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSON_CODE -> {
                val mNeedRequestPermissions = mutableListOf<String>()
                grantResults.forEachIndexed { index, result ->
                    if (result == PackageManager.PERMISSION_DENIED) {
                        mNeedRequestPermissions.add(permissions[index])
                    }
                }
                if (mNeedRequestPermissions.size > 0) {
                    //是否需要展示为什么需要请求权限的对话框
                    var resultRational = true
                    mNeedRequestPermissions.forEach {
                        if (!shouldShowRequestPermissionRationale(it)) {
                            resultRational = false
                            return@forEach
                        }
                    }
                    //展示为什么需要请求权限的对话框
                    if (resultRational) {
                        showRationaleDialog(mNeedRequestPermissions)
                        //打开设置界面，手动开启权限
                    } else {
                        showOpenSettingDialog(mNeedRequestPermissions)
                    }

                } else {
                    onPermissionGrant()
                }

            }
        }

    }

    /**
     * 需要解释为什么需要权限是的操作
     * 显示对话框
     */
    protected open fun showRationaleDialog(mNeedReqPermissions: MutableList<String>) {
    }

    /**
     * 显示对话框，跳转设置界面开启权限
     */
    protected open fun showOpenSettingDialog(mNeedReqPermissions: MutableList<String>) {
    }

    /**
     * 当请求权限全部通过时，执行该方法
     */
    protected open fun onPermissionGrant() {
    }


    override fun onDestroy() {
        super.onDestroy()
        if (regEvent) {
            EventBus.getDefault().unregister(this)
        }
        cancel(null)
    }

    /**
     * 初始化ViewBinding
     *
     * 如果布局文件为 activity_example.xml
     * 对应的 ViewBinding 为 ActivityExampleBinding
     * 初始化代码为 ActivityExampleBinding.inflate(layoutInflater)
     *
     */
    abstract fun getViewBinding(): V


}