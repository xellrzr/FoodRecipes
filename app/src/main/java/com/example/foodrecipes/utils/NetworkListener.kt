package com.example.foodrecipes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow

//Класс для оповещения изменения состояния сети
//Метод класса возвращает Flow, т.к метод будет запускаться в корутине.
class NetworkListener : ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable = MutableStateFlow(false)

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        //Регистрация для получения уведомления об изменениях в сети.
        //Вызовы будут, до тех пор, пока приложения не завершит работу или не будет вызвана функция unregisteredNetworkCallback
        //DefaultNetworkCallBack используется вне зависимости от типа сети. registerNetworkCallBack можно настроить под конкретный тип сети
        connectivityManager.registerDefaultNetworkCallback(this)

        var isConnected = false

        connectivityManager.allNetworks.forEach { network ->
            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            networkCapability?.let {
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    isConnected = true
                    return@forEach
                }
            }
        }
        isNetworkAvailable.value = isConnected
        return isNetworkAvailable
    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}