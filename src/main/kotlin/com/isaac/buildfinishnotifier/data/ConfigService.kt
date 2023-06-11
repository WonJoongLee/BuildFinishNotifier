package com.isaac.buildfinishnotifier.data

interface ConfigService {
    fun setShouldPlayDefaultSound(shouldPlayDefaultSound: Boolean)
    fun shouldPlayDefaultSound(): Boolean

    companion object {
        fun getInstance(): ConfigService {
            return ConfigServiceImpl()
        }
    }
}