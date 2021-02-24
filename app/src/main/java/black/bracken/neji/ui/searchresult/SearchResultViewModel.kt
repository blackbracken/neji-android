package black.bracken.neji.ui.searchresult

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Item
import black.bracken.neji.model.ItemSearchQuery
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class SearchResultViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private var _searchedItems: SharedFlow<List<Item>?> = MutableSharedFlow()
    val searchedItems get() = _searchedItems

    fun search(query: ItemSearchQuery) {
        _searchedItems = firebaseRepository._searchItems(query)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)
    }

}