package uz.kmax.tarixtest.data.tools.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkMonitor(private val connectManager: ConnectivityManager) : LiveData<Boolean>() {

    constructor(application: Application) : this(application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                postValue(false)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                postValue(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                super.onBlockedStatusChanged(network, blocked)
            }
        }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        val builder = NetworkRequest.Builder()
        connectManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectManager.unregisterNetworkCallback(networkCallback)
    }

    fun checkNetworkConnectivity() {
        val network = connectManager.activeNetwork
        if (network != null) {
            val requestBuilder = NetworkRequest.Builder().apply {
                addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            }.build()
            connectManager.registerNetworkCallback(requestBuilder, networkCallback)
        } else {
            postValue(false)
        }
    }
}