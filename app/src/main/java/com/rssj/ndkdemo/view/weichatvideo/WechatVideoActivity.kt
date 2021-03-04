package com.rssj.ndkdemo.view.weichatvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rssj.basecore.base.view.BaseActivity
import com.rssj.basecore.base.viewmodel.NoViewModel
import com.rssj.ndkdemo.databinding.ActivityMainBinding
import com.rssj.ndkdemo.databinding.ActivityWechatVideoBinding

class WechatVideoActivity : BaseActivity<NoViewModel, ActivityWechatVideoBinding>() {

    override val mViewModel: NoViewModel by viewModels()

    override fun getViewBinding() =  ActivityWechatVideoBinding.inflate(layoutInflater)

    override fun initDataAndView() {
        super.initDataAndView()
    }

}