package black.bracken.neji.ui.searchitem

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Item
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchItemViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _searchedItems = MutableSharedFlow<List<Item>?>(replay = 0)
    val searchedItems get() = _searchedItems.asSharedFlow()

    fun searchItems(
        itemName: String,
        itemType: String?,
        regionName: String?,
        boxName: String?
    ) {
        val query = FirebaseRepository.SearchQuery(itemName, itemType, regionName, boxName)

        viewModelScope.launch {
            _searchedItems.emit(firebaseRepository.searchItems(query))
        }
    }

}