package com.vairagi.news.util

import kotlinx.coroutines.flow.Flow

sealed class ConnectivityObserver {
    object Available: ConnectivityObserver()
    object Unavailable: ConnectivityObserver()

}

