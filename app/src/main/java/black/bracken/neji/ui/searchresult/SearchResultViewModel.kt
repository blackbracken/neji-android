package black.bracken.neji.ui.searchresult

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Item
import black.bracken.neji.model.ItemSearchQuery
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SearchResultViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private var _searchedItems: MutableSharedFlow<List<Item>?> = MutableSharedFlow(replay = 0)
    val searchedItems get() = _searchedItems

    fun search(query: ItemSearchQuery) {
        viewModelScope.launch {
            val result = firebaseRepository.searchItems(query)

            _searchedItems.emit(result)
        }
    }

}