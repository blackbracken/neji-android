package black.bracken.neji.ui.regionlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Region
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class RegionListViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regionAndAmounts = firebaseRepository.regions()
        .mapLatest { regions ->
            regions
                ?.map { region ->
                    // TODO: improve performance (this curse N + 1 problem)
                    region to (firebaseRepository.boxesInRegionOnce(region)?.size ?: 0)
                }
                ?.toMap()
        }
        .asLiveData()

    fun deleteRegion(region: Region) {
        viewModelScope.launch {
            firebaseRepository.deleteRegion(region.id)
        }
    }

}