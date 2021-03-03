package com.rssj.basecore.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.rssj.basecore.base.BusEvent
import com.rssj.basecore.utils.ToastUtils
import com.rssj.basecore.base.observerEvent
import com.rssj.basecore.base.viewmodel.BaseViewModel
import com.rssj.basecore.base.widget.dialog.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.ParameterizedType

/**
 * Description:
 * Date：2019/12/31 0031-14:57
 * Author: cwh
 */
abstract class BaseFragment<VM : BaseViewModel<*>, V : ViewBinding> : Fragment() , CoroutineScope by MainScope() {

    /**
     * ViewModel实例，主要用于数据操作
     * 当不需要ViewModel是，ViewModel类型为NoViewModel即
     * 可(具有BaseViewModel的功能)
     *
     * 建议用 ktx 的 by viewModels() 创建
     *
     * by viewModels() 创建属于该Fragment的ViewModel
     *
     * by viewModels ({requireParentFragment()}) 创建属于父 Fragment的ViewModel
     *
     * by activityViewModels()  创建属于 attach的Activity的ViewModel
     *
     */
    protected abstract val mViewModel: VM

    /**
     * ViewBinding实例
     */
    private var _binding: V? = null
    val mBinding get() = _binding!!

    /**
     * 是否开启EventBus事件监听
     */
    protected var regEvent: Boolean = false

    lateinit var mActivity: Activity

    //加载进度对话框
    protected var mLoadingDialog: LoadingDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initBinding(inflater, container, savedInstanceState)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(mViewModel)
        registerDefUIEvent()
        initDataAndView()
        initViewObserver()
        initEventBus()
    }


    /**
     * 初始化一些Data  and View
     */
    protected open fun initDataAndView() {

    }

    /**
     * 注册一些在LiveData数据变化时，需要改变UI的Observer
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
     * 观察BaseViewModel中常用的一些事件
     */
    protected open fun registerDefUIEvent() {

        with(mViewModel.mDefUIEvent) {

            mToastEvent.observerEvent(this@BaseFragment) { msg ->
                activity?.let {
                    ToastUtils.showToast(it.applicationContext, msg)
                }
            }

            onBackEvent.observerEvent(this@BaseFragment) {
                activity?.let {
                    it.onBackPressed()
                }
            }

            onFinishEvent.observerEvent(this@BaseFragment) {
                activity?.let {
                    it.finish()
                }
            }

            mStartActivity.observerEvent(this@BaseFragment) { cls ->
                activity?.let {
                    startActivity(Intent(it, cls))
                }

            }

            isShowLoadView.observerEvent(this@BaseFragment){
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
            if (ContextCompat.checkSelfPermission(
                    mActivity,
                    it
                ) == PackageManager.PERMISSION_DENIED
            ) {
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (regEvent) {
            EventBus.getDefault().unregister(this)
        }
        cancel(null)
    }

    /**
     * 初始化viewbinding
     *
     * 如果布局文件为 fragment_example.xml
     * 对应的 ViewBinding 为 FragmentExampleBinding
     * 初始化代码为 FragmentExampleBinding.inflate(inflater, container, false)
     *
     */
    abstract fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): V


}
