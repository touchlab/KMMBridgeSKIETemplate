package co.touchlab.kmmbridgekickstart.repository

import co.touchlab.kmmbridgekickstart.BreedAnalytics
import co.touchlab.kmmbridgekickstart.DatabaseHelper
import co.touchlab.kmmbridgekickstart.db.Breed
import co.touchlab.kmmbridgekickstart.ktor.DogApi
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val mutableDataState = MutableStateFlow(findLocalDataState())
    val dataState = mutableDataState.asStateFlow()

    private val mutableDataEvent =
        MutableSharedFlow<BreedDataEvent>(replay = 1).apply { tryEmit(BreedDataEvent.Initial) }
    val dataEvents = mutableDataEvent.asSharedFlow()

    private fun findLocalDataState(): BreedDataRefreshState {
        val last = lastDataPull()
        val localDataState = if (last == 0L) {
            BreedDataState.Empty
        } else {
            BreedDataState.Cached(last)
        }
        return localDataState
    }

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    fun getBreeds(): Flow<List<Breed>> = dbHelper.selectAllItems()

    suspend fun refreshBreedsIfStale() {
        if (isBreedListStale()) {
            refreshBreeds()
        }
    }

    suspend fun refreshBreeds() {
        mutableDataEvent.emit(BreedDataEvent.Loading)
        mutableDataState.value = BreedDataEvent.Loading

        delay(3000) // The server is too fast...

        // Simulate issues...
        val resultEvent: BreedDataEvent = if (Random.nextBoolean()) {
            BreedDataEvent.Error(BreedAnalytics.NotFetchedReason.RandomFail)
        } else {
            try {
                val breedResult = dogApi.getJsonFromApi()
                val breedList = breedResult.message.keys.sorted().toList()
                breedAnalytics.breedsFetchedFromNetwork(breedList.size)
                settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())

                if (breedList.isNotEmpty()) {
                    dbHelper.insertBreeds(breedList)
                }

                BreedDataEvent.RefreshedSuccess
            } catch (e: Exception) {
                BreedDataEvent.Error(BreedAnalytics.NotFetchedReason.NetworkError)
            }
        }

        mutableDataEvent.emit(resultEvent)
        mutableDataState.value = findLocalDataState()
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

sealed interface BreedDataRefreshState

sealed class BreedDataState {
    data object Empty : BreedDataState(), BreedDataRefreshState
    data class Cached(val lastRefresh: Long) : BreedDataState(), BreedDataRefreshState
}

sealed class BreedDataEvent {
    data object Initial : BreedDataEvent()
    data object Loading : BreedDataEvent(), BreedDataRefreshState
    data class Error(val reason: BreedAnalytics.NotFetchedReason) : BreedDataEvent()
    data object RefreshedSuccess : BreedDataEvent()
}
