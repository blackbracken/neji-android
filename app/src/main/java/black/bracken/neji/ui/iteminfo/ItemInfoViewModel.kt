package black.bracken.neji.ui.iteminfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.ext.toLiveData
import black.bracken.neji.model.Item
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.launch

class ItemInfoViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _item: MutableLiveData<Item> = MutableLiveData()
    val item get() = _item.toLiveData()

    fun addItem(item: Item) {
        _item.value = item
    }

    fun setAmount(amount: Int) {
        viewModelScope.launch {
            val updatedItem =
                firebaseRepository.updateItemAmount(item.value ?: return@launch, amount)

            if (updatedItem != null) {
                _item.postValue(updatedItem)
            } else {
                // TODO: error handling
            }
        }
    }

}