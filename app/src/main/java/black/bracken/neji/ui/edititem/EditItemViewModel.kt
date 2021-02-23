package black.bracken.neji.ui.edititem

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.model.Box
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.launch

class EditItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regions = firebaseRepository.regions().asLiveData()
    val itemTypes = firebaseRepository.itemTypes().asLiveData()

    private val _boxes: MutableLiveData<List<Box>?> = MutableLiveData()
    val boxes: LiveData<List<Box>?> get() = _boxes

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?> get() = _imageUri

    fun setItemImage(uri: Uri?) {
        _imageUri.value = uri
    }

    fun addItem(
        name: String,
        amount: Int,
        itemTypeSelection: Int?,
        boxSelection: Int?,
        comment: String?
    ) {
        // TODO: handle error rightly
        val itemType = itemTypes.value?.getOrNull(itemTypeSelection ?: -1) ?: return
        val box = boxes.value?.getOrNull(boxSelection ?: -1) ?: return

        viewModelScope.launch {
            // TODO: handle error rightly
            val item = firebaseRepository.addItem(
                name = name,
                amount = amount,
                itemType = itemType,
                box = box,
                imageUri = imageUri.value,
                comment = comment
            )
        }
    }

    fun updateBoxesByRegion(regionPosition: Int) {
        val region = regions.value?.getOrNull(regionPosition) ?: return

        viewModelScope.launch {
            _boxes.postValue(firebaseRepository.boxesInRegion(region))
        }
    }
}