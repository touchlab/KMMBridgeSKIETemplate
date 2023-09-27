package co.touchlab.kmmbridgekickstart

fun startSDK(analytics: Analytics): SDKHandle {
    val analyticsHandle = initAnalytics(analytics)
    return SDKHandle(
        breedRepository = breedStartup(analyticsHandle),
        appAnalytics = analyticsHandle.appAnalytics,
        breedAnalytics = analyticsHandle.breedAnalytics
    )
}

fun sayHello() = "Hello from Kotlin!"