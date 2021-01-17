package black.bracken.neji.ui.searchitem

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import black.bracken.neji.ext.toLiveData
import black.bracken.neji.model.document.Item
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.launch

class SearchItemViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _searchResult = MutableLiveData<Either<Exception, List<Item>>>()
    val searchResult get() = _searchResult.toLiveData()

    fun searchItems(
        itemName: String?,
        itemType: String?,
        regionName: String?,
        boxName: String?
    ) {
        val query = FirebaseRepository.SearchQuery(itemName, itemType, regionName, boxName)

        viewModelScope.launch {
            _searchResult.postValue(
                firebaseRepository.searchItems(query)
            )
        }
    }

}