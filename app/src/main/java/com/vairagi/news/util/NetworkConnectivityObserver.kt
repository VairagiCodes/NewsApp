package com.vairagi.news.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class NetworkConnectivityObserver(private val context: Context): LiveData<ConnectivityObserver>() {
    val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val validNetworkConnections : ArrayList<Network> = ArrayList()
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    fun getConnectivityMangerCallBack() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            val hasNetworkConnection = networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)?:false
            if (hasNetworkConnection){

                // network connection available
                determineInternetAccess(network)
            }

        }



        override fun onLost(network: Network) {
            super.onLost(network)

            validNetworkConnections.remove(network)
            announceStatus()

        }



        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)

            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){

                determineInternetAccess(network)

            } else {
                validNetworkConnections.remove(network)
            }
            announceStatus()

        }
    }


    fun announceStatus(){
        if (validNetworkConnections.isNotEmpty()){
            postValue(ConnectivityObserver.Available)
        } else {
            postValue(ConnectivityObserver.Unavailable)
        }


    }

    override fun onActive() {
        super.onActive()

        connectivityManagerCallback = getConnectivityMangerCallBack()
        val networkRequest = NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)

    }

    override fun onInactive() {
        super.onInactive()

        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

    private fun determineInternetAccess(network: Network) {
        CoroutineScope(Dispatchers.IO).launch{
            if (InternetAvailability.check()){
                withContext(Dispatchers.Main){
                    validNetworkConnections.add(network)
                    announceStatus()
                }
            }
        }
    }

}

object InternetAvailability {

    fun check() : Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8",53))
            socket.close()
            true
        } catch ( e: Exception){
            e.printStackTrace()
            false
        }
    }

}