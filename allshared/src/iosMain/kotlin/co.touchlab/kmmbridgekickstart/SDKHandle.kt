package co.touchlab.kmmbridgekickstart

data class SDKHandle(
    val breedRepository: CallbackBreedRepository,
    val appAnalytics: AppAnalytics,
    val breedAnalytics: BreedAnalytics
)