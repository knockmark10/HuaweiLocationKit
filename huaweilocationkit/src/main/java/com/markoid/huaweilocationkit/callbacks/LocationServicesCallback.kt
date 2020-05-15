package com.markoid.huaweilocationkit.callbacks

import android.location.Location

interface LocationServicesCallback {
    fun onLocationServicesDisabled()
    fun onPermissionsAreMissing()
    fun onLocationHasChanged(location: Location)
    fun onLocationHasChangedError(exception: Exception)
}