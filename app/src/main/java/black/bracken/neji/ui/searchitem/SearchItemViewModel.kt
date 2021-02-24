package black.bracken.neji.ui.searchitem

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.ItemSearchQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchItemViewModel @ViewModelInject constructor() : ViewModel() {

    private val _searchQuery = MutableSharedFlow<ItemSearchQuery>(replay = 0)
    val searchQuery get() = _searchQuery.asSharedFlow()

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