package black.bracken.neji.ui.searchresult

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Item
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchResultViewModel @ViewModelInject constructor() : ViewModel() {

    private val _searchedItems = MutableSharedFlow<List<Item>>(replay = 1)
    val searchedResults get() = _searchedItems.asSharedFlow()

    fun addAllSearchedResults(results: Array<out Item>) {
        viewModelScope.launch {
            _searchedItems.emit(results.toList())
        }
    }

}