package black.bracken.neji.ui.itemlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Box
import black.bracken.neji.model.Item
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ItemListViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _items = MutableSharedFlow<List<Item>?>(replay = 0)
    val items = _items.asSharedFlow()

    fun fetchItems(box: Box) {
        viewModelScope.launch {
            _items.emit(firebaseRepository.itemsInBox(box))
        }
    }

}