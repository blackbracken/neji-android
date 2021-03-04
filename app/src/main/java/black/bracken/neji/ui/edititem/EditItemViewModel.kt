package black.bracken.neji.ui.edititem

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.model.Box
import black.bracken.neji.repository.FirebaseRepository
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.launch
import java.io.File

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
        context: Context,
        name: String,
        amount: Int,
        itemTypeText: String?,
        boxText: String?,
        comment: String?
    ) {
        // TODO: handle error rightly
        val itemType = itemTypes.value?.find { it.toString() == itemTypeText } ?: return
        val box = boxes.value?.find { it.toString() == boxText } ?: return

        viewModelScope.launch {
            // TODO: handle error
            val item = firebaseRepository.addItem(
                name = name,
                amount = amount,
                itemType = itemType,
                box = box,
                image = imageUri.value?.let { compressImage(context, it) },
                comment = comment
            )
        }
    }

    fun updateBoxesByRegion(regionPosition: Int) {
        val region = regions.value?.getOrNull(regionPosition) ?: return

        viewModelScope.launch {
            _boxes.postValue(firebaseRepository.boxesInRegionOnce(region))
        }
    }

    private suspend fun compressImage(context: Context, uri: Uri): File {
        return Compressor.compress(context, uri.toFile(), viewModelScope.coroutineContext) {
            default(
                width = 240,
                height = 240
            )
        }
    }

}