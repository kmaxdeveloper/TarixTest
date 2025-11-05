package uz.kmax.tarixtest.data.ads

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import uz.kmax.tarixtest.R

class AdmobNativeAdsManager {
    private var currentNativeAd: NativeAd? = null
    private var adCard : CardView? = null
    private var adView : NativeAdView? = null

    fun init(setAdCard: CardView,setAdView: NativeAdView){
        adCard = setAdCard
        adView = setAdView
    }

    fun loadNativeAd(context : Context) {
        val adLoader = AdLoader.Builder(context, "ca-app-pub-4664801446868642/8645098197")
            .forNativeAd { ad: NativeAd ->
                currentNativeAd?.destroy()
                currentNativeAd = ad

                if (adCard != null && adView != null) {
                    displayNativeAd(adCard!!, adView!!, ad)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdMob", "Native ad failed to load: ${adError.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * Yuklangan reklamani XML layoutingizga bog'laydi (populate)
     */
    fun displayNativeAd(adCard: CardView, adView: NativeAdView, nativeAd: NativeAd) {

        // 1. XML'dagi View'larni topish
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)

        // 2. Reklama ma'lumotlarini View'larga o'rnatish
        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }

        // 3. NativeAd obyektini NativeAdView'ga ro'yxatdan o'tkazish
        adView.setNativeAd(nativeAd)

        // 4. Reklama kartasini ko'rinadigan qilish
        adCard.visibility = View.VISIBLE
    }
}