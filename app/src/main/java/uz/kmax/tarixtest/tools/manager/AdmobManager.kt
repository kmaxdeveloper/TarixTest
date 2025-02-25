package uz.kmax.tarixtest.tools.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdmobManager(private var context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var adRequest: AdRequest = AdRequest.Builder().build()
    private var isAdLoading: Boolean = false
    private var lastAdShowTime: Long = 0
    private val adShowInterval: Long = 30000

    private var onAdDismissListener: (() -> Unit)? = null
    private var onAdClickListener: (() -> Unit)? = null
    private var onAdLoadListener: ((Boolean) -> Unit)? = null

    fun initialize(adUnitId: String) {
        MobileAds.initialize(context) {}
        loadInterstitialAd(adUnitId)
    }

    fun setOnAdDismissListener(listener: () -> Unit) {
        onAdDismissListener = listener
    }

    fun setOnAdClickListener(listener: () -> Unit) {
        onAdClickListener = listener
    }

    fun setOnAdLoadListener(listener: (Boolean) -> Unit) {
        onAdLoadListener = listener
    }

    private fun loadInterstitialAd(adUnitId: String) {
        if (isAdLoading) { return }
        isAdLoading = true
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    setupAdCallbacks(adUnitId)
                    onAdLoadListener?.invoke(true)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    onAdLoadListener?.invoke(false)
                }
            }
        )
    }

    private fun setupAdCallbacks(adUnitId: String) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                onAdClickListener?.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                lastAdShowTime = System.currentTimeMillis()
                loadInterstitialAd(adUnitId)
                onAdDismissListener?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                loadInterstitialAd(adUnitId)
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
                loadInterstitialAd(adUnitId)
            }
        }
    }

    fun showInterstitialAd(activity: Activity,onResultShowAds: ((boolean:Boolean) -> Unit)? = null) {
        val currentTime = System.currentTimeMillis()
        if (interstitialAd != null && (currentTime - lastAdShowTime) > adShowInterval) {
            interstitialAd?.show(activity)
        } else {
            onResultShowAds?.invoke(false)
        }
    }

    fun loadBannerAd(adView: AdView,onResultShowAds: ((adsClicked:Boolean) -> Unit)? = null) {
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                onResultShowAds?.invoke(true)
            }
            override fun onAdClosed() {}
            override fun onAdFailedToLoad(adError: LoadAdError) {}
            override fun onAdLoaded() {}
            override fun onAdOpened() {}
        }
    }

    fun isAdReady(): Boolean {
        return interstitialAd != null
    }

}