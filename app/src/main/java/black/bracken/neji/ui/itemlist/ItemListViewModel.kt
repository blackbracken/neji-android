package black.bracken.neji.ui.itemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Box
import black.bracken.neji.model.Item
import black.bracken.neji.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _items = MutableSharedFlow<List<Item>?>(replay = 0)
    val items = _items.asSharedFlow()

    fun fetchItems(box: Box) {
        viewModelScope.launch {
            _items.emit(firebaseRepository.itemsInBox(box.id))
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            firebaseRepository.deleteItem(item.id)
        }
    }

}