package uz.kmax.tarixtest.data.ads

import android.app.Activity
import android.content.Context
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

class YandexAdsManager(private var context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdsLoader : InterstitialAdLoader? = null
    private var adUnit = "R-M-15560731-1"
    private var lastAdShowTime: Long = 0
    private val adShowInterval: Long = 20000
    private var onYandexAdLoadListener: ((Boolean) -> Unit)? = null
    private var onYandexAdDismissListener: (() -> Unit)? = null
    private var onYandexAdClickListener: (() -> Unit)? = null

    fun setOnYandexAdLoadListener(listener: (Boolean) -> Unit) {
        onYandexAdLoadListener = listener
    }

    fun setOnYandexAdDismissListener(listener: () -> Unit) {
        onYandexAdDismissListener = listener
    }

    fun setOnYandexAdClickListener(listener: () -> Unit) {
        onYandexAdClickListener = listener
    }

    fun initYandexAds(){
        MobileAds.initialize(context){
            interstitialAdsLoader = InterstitialAdLoader(context).apply {
                setAdLoadListener(object : InterstitialAdLoadListener{
                    override fun onAdFailedToLoad(error: AdRequestError) {
                        onYandexAdLoadListener?.invoke(false)
                    }

                    override fun onAdLoaded(ads: InterstitialAd) {
                        interstitialAd = ads
                        onYandexAdLoadListener?.invoke(true)
                        adsCallsBack()
                    }

                })
            }

            loadInterstitialAds()
        }
    }

    private fun loadInterstitialAds() {
        val adRequestConfiguration = AdRequestConfiguration.Builder(adUnit).build()
        interstitialAdsLoader?.loadAd(adRequestConfiguration)
    }

    fun adsCallsBack(){
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener{
                override fun onAdClicked() {
                    onYandexAdClickListener?.invoke()
                }
                override fun onAdDismissed() {
                    destroyInterstitialAd()
                    onYandexAdDismissListener?.invoke()
                    lastAdShowTime = System.currentTimeMillis()
                    loadInterstitialAds()
                }

                override fun onAdFailedToShow(adError: AdError) {
                    destroyInterstitialAd()
                    loadInterstitialAds()
                }
                override fun onAdImpression(impressionData: ImpressionData?) {}
                override fun onAdShown() {}

            })
        }
    }

    fun showYandexAds(activity: Activity, onResultShowAds: ((boolean:Boolean) -> Unit)? = null){
        if (interstitialAd !=null){
            interstitialAd?.show(activity)
        }else{
            onResultShowAds?.invoke(false)
        }
    }

    fun yandexIsReady() : Boolean{
        val currentTime = System.currentTimeMillis()
        return interstitialAd != null && (currentTime - lastAdShowTime) >= adShowInterval
    }

    fun destroy(){
        interstitialAdsLoader?.setAdLoadListener(null)
        interstitialAdsLoader = null

        destroyInterstitialAd()
    }

    private fun destroyInterstitialAd() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

}