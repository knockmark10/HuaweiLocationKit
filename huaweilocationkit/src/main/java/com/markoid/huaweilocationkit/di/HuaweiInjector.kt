package com.markoid.huaweilocationkit.di

import android.content.Context
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.SettingsClient
import com.markoid.huaweilocationkit.entities.SettingsOptions
import com.markoid.huaweilocationkit.managers.HuaweiManager
import com.markoid.huaweilocationkit.utils.ApiChecker

object Injector {

    fun providesHuaweiManager(context: Context, options: SettingsOptions): HuaweiManager =
        HuaweiManager(
            context,
            options,
            providesApiChecker(context),
            providesFusedClient(context),
            providesLocationSettings(context)
        )

    private fun providesApiChecker(context: Context): ApiChecker =
        ApiChecker(context)

    private fun providesFusedClient(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private fun providesLocationSettings(context: Context): SettingsClient =
        LocationServices.getSettingsClient(context)

}