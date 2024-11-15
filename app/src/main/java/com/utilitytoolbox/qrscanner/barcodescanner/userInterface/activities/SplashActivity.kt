package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.limurse.iap.IapConnector
import com.utilitytoolbox.qrscanner.barcodescanner.BaseActivity
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.SplashActivityBinding
import com.utilitytoolbox.qrscanner.barcodescanner.utils.onBatchScanningEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : BaseActivity() {
    private var isAdClosed: Boolean = false
    private var isInterAdLoaded: Boolean = false
    private var MAX_PROGRESS = 2000
    var valueAnimator: ValueAnimator? = null
    var iapConnector: IapConnector? = null
    lateinit var binding: SplashActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBatchScanningEnabled = false

            //initDefault()

        CoroutineScope(Dispatchers.Main).launch {
            for(i in 0.. 100){

                binding.progressBar.progress = i
                Log.d("dkfkdjfd87fdkj","progress-> $i")
                if(i==100){
                    launchMainActivity()
                }
                delay(10)
            }
        }


    }


    override fun onResume() {
        super.onResume()
        valueAnimator?.resume()
    }

    override fun onPause() {
        super.onPause()
        valueAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        valueAnimator?.removeAllListeners()
        valueAnimator?.removeAllUpdateListeners()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (isAdClosed) {
            binding.splashView.visibility = View.INVISIBLE
            launchMainActivity()
        }
    }

    fun launchMainActivity() {
        startActivity(Intent(this, MainActivityNew::class.java))
        finish()
    }

    private fun initDefault() {
        binding.progressBar.max =  (MAX_PROGRESS)
        binding.progressBar.progress = 0
        valueAnimator = ValueAnimator.ofInt(0, MAX_PROGRESS)?.apply {
            duration = MAX_PROGRESS.toLong()
            addUpdateListener { animation ->
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    launchMainActivity()
                }
            })
            start()
        }
    }
}