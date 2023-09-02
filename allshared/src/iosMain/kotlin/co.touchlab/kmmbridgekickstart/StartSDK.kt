package co.touchlab.kmmbridgekickstart

fun startSDK(analytics: Analytics): SDKHandle {
    val analyticsHandle = initAnalytics(analytics)
    return SDKHandle(
        breedRepository = CallbackBreedRepository(breedRepository = breedStartup(analyticsHandle)),
        appAnalytics = analyticsHandle.appAnalytics,
        breedAnalytics = analyticsHandle.breedAnalytics
    )
}