package ru.nyxsed.postscan.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.vk.id.VKID

class ConnectionChecker(
    private val context: Context,
) {
    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun isTokenValid(): Boolean {
        val currentToken = VKID.Companion.instance.accessToken
        return currentToken?.token != null && currentToken.expireTime > System.currentTimeMillis()
    }
}