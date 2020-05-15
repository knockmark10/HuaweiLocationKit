package com.markoid.huaweilocationkit.managers

import android.content.Context
import com.huawei.hms.location.*
import com.markoid.huaweilocationkit.callbacks.LocationServicesCallback
import com.markoid.huaweilocationkit.di.Injector.providesHuaweiManager
import com.markoid.huaweilocationkit.entities.SettingsOptions
import com.markoid.huaweilocationkit.utils.ApiChecker

//Reference: https://developer.huawei.com/consumer/en/codelab/HMSLocationKit/index.html#5
class HuaweiManager(
    private val mContext: Context,
    private val mOptions: SettingsOptions,
    private val mApiChecker: ApiChecker,
    private val mFusedClient: FusedLocationProviderClient,
    private val mSettings: SettingsClient
) : LocationCallback() {

    private var mListener: LocationServicesCallback? = null

    private val mLocationRequest by lazy {
        LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = mOptions.updateInterval
            fastestInterval = mOptions.fastestInterval
        }
    }

    /**
     * Register listener to communicate.
     */
    fun registerListener(listener: LocationServicesCallback) {
        this.mListener = listener
    }

    /**
     * Request location updates to track user's location.
     */
    fun startLocationUpdates() {
        if (validate()) {
            val locationSettingsRequest = buildLocationSettingsRequest()
            this.mSettings.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener { setupLocation() }
                .addOnFailureListener { this.mListener?.onLocationServicesDisabled() }
        }
    }

    /**
     * Stop receiving location updates (turn off gps use).
     */
    fun stopLocationUpdates() {
        this.mFusedClient.removeLocationUpdates(this)
    }

    /**
     * Request user's last known location
     */
    fun getLastLocation() {
        if (validate()) {
            val locationClient = LocationServices.getFusedLocationProviderClient(this.mContext)
            locationClient.lastLocation
                .addOnSuccessListener { mListener?.onLocationHasChanged(it) }
                .addOnFailureListener { mListener?.onLocationHasChangedError(it) }
        }
    }

    private fun buildLocationSettingsRequest(): LocationSettingsRequest {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(this.mLocationRequest)
        return builder.build()
    }

    private fun setupLocation() {
        this.mFusedClient.requestLocationUpdates(this.mLocationRequest, this, this.mOptions.looper)
    }

    /**
     * Validate if services are up and running, and permissions have been granted.
     */
    private fun validate(): Boolean = with(this.mApiChecker) {
        when {
            !this.areLocationServicesEnabled() -> notify { it.onLocationServicesDisabled() }
            !this.isLocationPermissionGranted() -> notify { it.onPermissionsAreMissing() }
            else -> true
        }
    }

    private fun notify(output: (listener: LocationServicesCallback) -> Unit): Boolean {
        this.mListener?.let(output)
        return false
    }

    override fun onLocationResult(result: LocationResult?) {
        super.onLocationResult(result)
        result?.lastLocation?.let { this.mListener?.onLocationHasChanged(it) }
    }

    object Builder {

        private var updateInterval: Long = (15 * 1000).toLong()

        private var fastestInterval: Long = (10 * 1000).toLong()

        private var useLooper: Boolean = false

        fun setUpdateInterval(milliseconds: Long): Builder {
            this.updateInterval = milliseconds
            return this
        }

        fun setFastestInterval(milliseconds: Long): Builder {
            this.fastestInterval = milliseconds
            return this
        }

        fun useLooper(useLooper: Boolean): Builder {
            this.useLooper = useLooper
            return this
        }

        fun build(context: Context): HuaweiManager =
            providesHuaweiManager(context, encapsulateOptions())

        private fun encapsulateOptions(): SettingsOptions =
            SettingsOptions(this.updateInterval, this.fastestInterval, this.useLooper)

    }

}