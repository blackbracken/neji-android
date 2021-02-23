package black.bracken.neji.ui.iteminfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import black.bracken.neji.ext.toLiveData
import black.bracken.neji.model.Item

class ItemInfoViewModel @ViewModelInject constructor() : ViewModel() {

    private val _item: MutableLiveData<Item> = MutableLiveData()
    val item get() = _item.toLiveData()

    fun addItem(item: Item) {
        _item.value = item
    }

}