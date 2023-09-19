package co.touchlab.kmmbridgekickstart

//import co.touchlab.stately.concurrency.AtomicReference

interface Analytics {
    fun sendEvent(eventName: String, eventArgs: Map<String, Any>)
}

fun initAnalytics(analytics: Analytics): AnalyticsHandle {
    // if (!AnalyticsHandler.analyticsAtom.compareAndSet(null, analytics)) {
    if (AnalyticsHandler.analyticsAtom != null) {
        throw IllegalStateException("Analytics can only be set once")
    }
    AnalyticsHandler.analyticsAtom = analytics
    return AnalyticsHandle(
        appAnalytics = AppAnalytics(),
        breedAnalytics = BreedAnalytics(),
        httpClientAnalytics = HttpClientAnalytics()
    )
}

data class AnalyticsHandle(
    val appAnalytics: AppAnalytics,
    val breedAnalytics: BreedAnalytics,
    val httpClientAnalytics: HttpClientAnalytics
)

internal fun sendEvent(name: String, vararg args: Pair<String, Any>) {
    // AnalyticsHandler.analyticsAtom.get()!!.sendEvent(name, args.toMap())
    AnalyticsHandler.analyticsAtom?.sendEvent(name, args.toMap())
}

internal object AnalyticsHandler {
    // val analyticsAtom: AtomicReference<Analytics?> = AtomicReference(null)
    var analyticsAtom: Analytics? = null
}