package uz.kmax.tarixtest.data.tools.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdsManager {

    private lateinit var adRequest: AdRequest
    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading: Boolean = false

    private var onAdDismissListener: (() -> Unit)? = null
    private var onAdClickListener: (() -> Unit)? = null
    private var onAdNotReadyListener: (() -> Unit)? = null
    private var onAdLoadStatusListener: ((Boolean) -> Unit)? = null

    fun initialize(context: Context) {
        MobileAds.initialize(context) {
            Log.d("AdsManager", "MobileAds initialized.")
        }
        adRequest = AdRequest.Builder().build()
    }

    fun setOnAdDismissListener(listener: () -> Unit) {
        onAdDismissListener = listener
    }

    fun setOnAdClickListener(listener: () -> Unit) {
        onAdClickListener = listener
    }

    fun setOnAdNotReadyListener(listener: () -> Unit) {
        onAdNotReadyListener = listener
    }

    fun setOnAdLoadStatusListener(listener: (Boolean) -> Unit) {
        onAdLoadStatusListener = listener
    }

    fun loadInterstitialAd(context: Context, adUnit: String) {
        if (isAdLoading) {
            Log.w("AdsManager", "Ad is already loading.")
            return
        }

        isAdLoading = true
        InterstitialAd.load(
            context, adUnit, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    isAdLoading = false
                    onAdLoadStatusListener?.invoke(false)
                    Log.e("AdsManager", "Interstitial ad failed to load: ${adError.message}")
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    isAdLoading = false
                    onAdLoadStatusListener?.invoke(true)
                    setupInterstitialAdCallbacks()
                    Log.d("AdsManager", "Interstitial ad loaded successfully.")
                }
            }
        )
    }

    private fun setupInterstitialAdCallbacks() {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                onAdClickListener?.invoke()
                Log.d("AdsManager", "Interstitial ad clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                onAdDismissListener?.invoke()
                Log.d("AdsManager", "Interstitial ad dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdsManager", "Interstitial ad failed to show: ${adError.message}")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdsManager", "Interstitial ad is showing.")
                mInterstitialAd = null // Prevent reuse
            }
        }
    }

    fun showInterstitialAd(activity: Activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            onAdNotReadyListener?.invoke()
            Log.w("AdsManager", "Interstitial ad is not ready to be shown.")
        }
    }

    fun loadBannerAd(adView: AdView) {
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                Log.d("AdsManager", "Banner ad clicked.")
            }

            override fun onAdClosed() {
                Log.d("AdsManager", "Banner ad closed.")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdsManager", "Banner ad failed to load: ${adError.message}")
            }

            override fun onAdLoaded() {
                Log.d("AdsManager", "Banner ad loaded successfully.")
            }

            override fun onAdOpened() {
                Log.d("AdsManager", "Banner ad opened.")
            }
        }
    }

    fun isAdReady(): Boolean {
        return mInterstitialAd != null
    }
}