package co.touchlab.kampkit.android.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kmmbridgekickstart.db.Breed
import co.touchlab.kmmbridgekickstart.repository.BreedDataEvent
import co.touchlab.kmmbridgekickstart.repository.BreedDataRefreshState
import co.touchlab.kmmbridgekickstart.repository.BreedRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreedViewModel(
    private val breedRepository: BreedRepository,
) : ViewModel() {

    val dataState: StateFlow<BreedDataRefreshState> = breedRepository.dataState
    private val mutableBreedListState = MutableStateFlow<List<Breed>>(emptyList())
    val breedListState: StateFlow<List<Breed>> = mutableBreedListState

    private val mutableDataEventState = MutableStateFlow<BreedDataEvent>(BreedDataEvent.Initial)
    val dataEventState: StateFlow<BreedDataEvent> = mutableDataEventState

    init {
        observeBreeds()
    }

    private fun observeBreeds() {
        viewModelScope.launch {
            breedRepository.getBreeds().collect { breedList ->
                mutableBreedListState.update { breedList }
            }
        }

        viewModelScope.launch {
            breedRepository.dataEvents.collect{ event ->
                mutableDataEventState.update { event }
            }
        }
    }

    fun refreshBreeds(): Job {
        return viewModelScope.launch {
            breedRepository.refreshBreeds()
        }
    }

    fun updateBreedFavorite(breed: Breed): Job {
        return viewModelScope.launch {
            breedRepository.updateBreedFavorite(breed)
        }
    }
}