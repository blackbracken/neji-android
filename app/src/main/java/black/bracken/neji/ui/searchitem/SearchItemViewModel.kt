package black.bracken.neji.ui.searchitem

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import black.bracken.neji.firebase.document.ItemEntity
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchItemViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _searchResult = MutableSharedFlow<Either<Exception, List<ItemEntity>>>(replay = 0)
    val searchResult get() = _searchResult.asSharedFlow()

    fun searchItems(
        itemName: String,
        itemType: String?,
        regionName: String?,
        boxName: String?
    ) {
        val query = FirebaseRepository.SearchQuery(itemName, itemType, regionName, boxName)

        viewModelScope.launch {
            _searchResult.emit(firebaseRepository.searchItems(query))
        }
    }

}