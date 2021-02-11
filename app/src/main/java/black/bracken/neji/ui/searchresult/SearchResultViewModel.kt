package black.bracken.neji.ui.searchresult

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.document.Item
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchResultViewModel @ViewModelInject constructor() : ViewModel() {

    private val _searchedResults = MutableSharedFlow<List<Item>>(replay = 0)
    val searchedResults get() = _searchedResults.asSharedFlow()

    fun addAllSearchedResults(results: Array<out Item>) {
        viewModelScope.launch {
            _searchedResults.emit(results.toList())
        }
    }

}