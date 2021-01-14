package black.bracken.neji.ui.searchitem

import androidx.annotation.MainThread
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import black.bracken.neji.repository.FirebaseRepository

class SearchItemViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _inputItemName = savedState.getLiveData<String?>(KEY_REGION_NAME, null)
    val inputItemName: LiveData<String?> get() = _inputItemName

    private val _inputItemType = savedState.getLiveData<String?>(KEY_ITEM_TYPE, null)
    val inputItemType: LiveData<String?> get() = _inputItemType

    private val _inputRegionName = savedState.getLiveData<String?>(KEY_REGION_NAME, null)
    val inputRegionName: LiveData<String?> get() = _inputRegionName

    private val _inputBoxName = savedState.getLiveData<String?>(KEY_BOX_NAME, null)
    val inputBoxName: LiveData<String?> get() = _inputBoxName

    fun searchItems() {
    }

    @MainThread
    fun setItemName(itemName: String) {
        _inputItemName.value = itemName
    }

    @MainThread
    fun setItemType(itemType: String) {
        _inputItemType.value = itemType
    }

    @MainThread
    fun setRegionName(regionName: String) {
        _inputRegionName.value = regionName
    }

    @MainThread
    fun setBoxName(boxName: String) {
        _inputBoxName.value = boxName
    }

    companion object {
        private const val KEY_ITEM_NAME = "itemName"
        private const val KEY_ITEM_TYPE = "itemType"
        private const val KEY_REGION_NAME = "region"
        private const val KEY_BOX_NAME = "box"
    }

}