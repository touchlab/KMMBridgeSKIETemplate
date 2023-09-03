package co.touchlab.kmmbridgekickstart.repository

import co.touchlab.kmmbridgekickstart.BreedAnalytics
import co.touchlab.kmmbridgekickstart.DatabaseHelper
import co.touchlab.kmmbridgekickstart.db.Breed
import co.touchlab.kmmbridgekickstart.ktor.DogApi
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlin.random.Random

class BreedRepository internal constructor(
    private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val dogApi: DogApi,
    private val clock: Clock,
    private val breedAnalytics: BreedAnalytics
) {

    private val mutableDataState: MutableStateFlow<BreedDataState> =
        MutableStateFlow(BreedDataState.Initial)

    val dataState: StateFlow<BreedDataState> = mutableDataState.asStateFlow()

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    fun getBreeds(): Flow<List<Breed>> = dbHelper.selectAllItems()

    suspend fun refreshBreedsIfStale() {
        if (isBreedListStale()) {
            refreshBreeds()
        } else {
            mutableDataState.value = BreedDataState.Cached(lastDataPull())
        }
    }

    suspend fun refreshBreeds() {
        mutableDataState.value = BreedDataState.Loading

        delay(3000) // The server is too fast...

        // Simulate issues...
        if (Random.nextBoolean()) {
            mutableDataState.value = BreedDataState.Error(BreedAnalytics.NotFetchedReason.RandomFail)
        } else {
            try {
                val breedResult = dogApi.getJsonFromApi()
                val breedList = breedResult.message.keys.sorted().toList()
                breedAnalytics.breedsFetchedFromNetwork(breedList.size)
                settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())

                if (breedList.isNotEmpty()) {
                    dbHelper.insertBreeds(breedList)
                }

                mutableDataState.value = BreedDataState.RefreshedSuccess
            } catch (e: Exception) {
                mutableDataState.value = BreedDataState.Error(BreedAnalytics.NotFetchedReason.NetworkError)
            }
        }
    }

    suspend fun updateBreedFavorite(breed: Breed) {
        dbHelper.updateFavorite(breed.id, !breed.favorite)
    }

    private fun isBreedListStale(): Boolean {
        val lastDownloadTimeMS = lastDataPull()
        val oneHourMS = 60 * 60 * 1000
        val stale = lastDownloadTimeMS + oneHourMS < clock.now().toEpochMilliseconds()
        if (!stale) {
            breedAnalytics.breedsNotFetchedFromNetwork(BreedAnalytics.NotFetchedReason.NotStale)
        }
        return stale
    }

    private fun lastDataPull() = settings.getLong(DB_TIMESTAMP_KEY, 0)
}

sealed class BreedDataState {
    data object Initial : BreedDataState()
    data object Loading : BreedDataState()
    data class Error(val reason: BreedAnalytics.NotFetchedReason) : BreedDataState()
    data class Cached(val lastRefresh: Long) : BreedDataState()
    data object RefreshedSuccess : BreedDataState()
}
