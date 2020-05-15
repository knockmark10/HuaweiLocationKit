package com.markoid.huaweilocationkit.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager

class ApiChecker(private val mContext: Context) {

    fun areLocationServicesEnabled(): Boolean {
        val locationManager =
            this.mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        val isNetworkAvailable =
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                ?: false
        return isGpsEnabled && isNetworkAvailable && this.isLocationPermissionGranted()
    }

    fun isLocationPermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        this.mContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    fun isHuaweiDevice(): Boolean =
        android.os.Build.MANUFACTURER.contains("huawei", true)


}