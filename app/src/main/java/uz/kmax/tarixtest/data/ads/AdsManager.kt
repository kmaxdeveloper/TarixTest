package uz.kmax.tarixtest.data.ads

import android.app.Activity
import com.google.android.gms.ads.AdView
import com.yandex.mobile.ads.rewarded.Reward
import javax.inject.Inject

class AdsManager @Inject constructor(
    private val admobManager: AdmobManager,
    private val yandexManager: YandexAdsManager
) {
    private var statusYandexAds : Boolean = false
    private var statusAdmobAds : Boolean = false
    private var onAdDismissListener: (() -> Unit)? = null
    private var onAdClickListener: (() -> Unit)? = null
    private var onRewardedAdsDismissListener: ((reward: Int) -> Unit)? = null

    /** Interface */
    fun setOnAdDismissListener(listener: () -> Unit) {
        onAdDismissListener = listener
    }

    fun setOnAdClickListener(listener: () -> Unit) {
        onAdClickListener = listener
    }

    fun setOnRewardedAdsDismissListener(listener: (reward: Int) -> Unit){
        onRewardedAdsDismissListener = listener
    }

    fun init(onAdLoadListener: ((type : String) -> Unit)? = null){
        admobManager.initialize()
        yandexManager.initYandexAds()
        admobManager.setOnAdLoadListener {
            statusAdmobAds = it
            if (it){
                onAdLoadListener?.invoke("Admob Ads is loaded !")
            }
        }

        yandexManager.setOnYandexAdLoadListener {
            statusYandexAds = it
            if (it){
                onAdLoadListener?.invoke("Yandex Ads is loaded !")
            }
        }

        optionalFunc()
    }

    private fun optionalFunc(){
        yandexManager.setOnYandexAdDismissListener {
            onAdDismissListener?.invoke()
        }

        yandexManager.setOnYandexAdClickListener {
            onAdClickListener?.invoke()
        }

        admobManager.setOnAdClickListener {
            onAdClickListener?.invoke()
        }

        admobManager.setOnAdDismissListener {
            onAdDismissListener?.invoke()
        }
    }

    fun showAds(activity: Activity,onResultShowAds: ((boolean:Boolean) -> Unit)? = null){
        if (admobManager.admobIsReady()){
            admobManager.showInterstitialAd(activity){
                onResultShowAds?.invoke(it)
            }
        }else if (yandexManager.yandexIsReady()){
            yandexManager.showYandexAds(activity){
                onResultShowAds?.invoke(it)
            }
        }else{
            onResultShowAds?.invoke(false)
        }
    }

    fun loadBanners(adView: AdView){
        admobManager.loadBannerAd(adView)
    }

    fun initRewardedAds(){
        admobManager.initRewarded()

        admobManager.setOnRewardedAdsListener {
            when(it) {
                1->{
                    onRewardedAdsDismissListener?.invoke(3)
                }
                0->{}
                2->{}
                3->{}
                4->{}
            }
        }
    }

    fun showRewardedAds(activity: Activity,onResultShowAds: ((reward: Int,adsStatus: Boolean) -> Unit)? = null){
        admobManager.showRewardedAds(activity){reward, status->
            onResultShowAds?.invoke(reward,status)
        }
    }

    fun admobRewardedAdsReady(): Boolean{
        return admobManager.admobRewardedAdsIsReady()
    }
}