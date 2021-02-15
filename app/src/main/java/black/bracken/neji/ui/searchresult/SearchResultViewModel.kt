package black.bracken.neji.ui.searchresult

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.firebase.document.ItemEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchResultViewModel @ViewModelInject constructor() : ViewModel() {

    private val _searchedResults = MutableSharedFlow<List<ItemEntity>>(replay = 1)
    val searchedResults get() = _searchedResults.asSharedFlow()

    fun addAllSearchedResults(results: Array<out ItemEntity>) {
        viewModelScope.launch {
            _searchedResults.emit(results.toList())
        }
    }

}