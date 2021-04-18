package black.bracken.neji.ui.searchitem

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Box
import black.bracken.neji.model.ItemSearchQuery
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _searchQuery = MutableSharedFlow<ItemSearchQuery>(replay = 0)
    val searchQuery get() = _searchQuery.asSharedFlow()

    val itemTypes = firebaseRepository.itemTypes()
    val regions = firebaseRepository.regions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private var collectBoxJob: Job? = null
    private val _boxesAtSelectedRegion = MutableSharedFlow<List<Box>?>(replay = 0)
    val boxesAtSelectedRegion get() = _boxesAtSelectedRegion.asSharedFlow()

    fun selectRegion(regionIndex: Int) {
        collectBoxJob?.cancel()

        viewModelScope.launch {
            regions
                .value
                ?.getOrNull(regionIndex)
                ?.also { region ->
                    collectBoxJob = launch {
                        firebaseRepository.boxesInRegion(region).collect { boxes ->
                            _boxesAtSelectedRegion.emit(boxes)
                        }
                    }
                }
                ?: run {
                    collectBoxJob = null
                    _boxesAtSelectedRegion.emit(null)
                }
        }
    }

    fun emitQuery(
        itemName: String,
        itemType: String?,
        regionName: String?,
        boxName: String?
    ) {
        viewModelScope.launch {
            _searchQuery.emit(ItemSearchQuery(itemName, itemType, regionName, boxName))
        }
    }

}