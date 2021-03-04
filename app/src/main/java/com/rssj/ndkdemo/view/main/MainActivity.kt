package com.rssj.ndkdemo.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rssj.basecore.base.ext.click
import com.rssj.basecore.base.ext.startKtxActivity
import com.rssj.basecore.base.view.BaseActivity
import com.rssj.basecore.base.viewmodel.NoViewModel
import com.rssj.ndkdemo.R
import com.rssj.ndkdemo.databinding.ActivityMainBinding
import com.rssj.ndkdemo.view.weichatvideo.WechatVideoActivity

class MainActivity : BaseActivity<NoViewModel, ActivityMainBinding>() {

    override val mViewModel: NoViewModel by viewModels()

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun initDataAndView() {
        super.initDataAndView()

        mBinding.run {
            btnWeichatVideo.click {
                this@MainActivity.startKtxActivity<WechatVideoActivity>()
            }
        }

    }

}