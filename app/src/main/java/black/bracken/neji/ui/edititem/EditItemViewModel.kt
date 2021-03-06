package black.bracken.neji.ui.edititem

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Box
import black.bracken.neji.model.Item
import black.bracken.neji.model.Region
import black.bracken.neji.repository.FirebaseRepository
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class EditItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regions = firebaseRepository.regions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val itemTypes = firebaseRepository.itemTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _boxes: MutableLiveData<List<Box>?> = MutableLiveData()
    val boxes: LiveData<List<Box>?> get() = _boxes

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val changedImage get() = MutableStateFlow(false)

    fun setItemImage(uri: Uri?) {
        viewModelScope.launch {
            _imageUri.value = uri
            changedImage.emit(true)
        }
    }

    fun editItem(
        context: Context,
        source: Item,
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
            firebaseRepository.editItem(
                item = source,
                name = name,
                amount = amount,
                itemType = itemType,
                image = imageUri.value
                    ?.takeIf { changedImage.value }
                    ?.also { compressImage(context, it) }
                    ?.toFile(),
                box = box,
                comment = comment
            )
        }
    }

    fun updateBoxesByRegionName(regionName: String) {
        updateBoxesByRegion(regions.value?.find { it.name == regionName } ?: return)
    }

    fun updateBoxesByRegionIndex(regionPosition: Int) {
        updateBoxesByRegion(regions.value?.getOrNull(regionPosition) ?: return)
    }

    private fun updateBoxesByRegion(region: Region) {
        viewModelScope.launch {
            _boxes.postValue(firebaseRepository.boxesInRegionOnce(region))
        }
    }

    // TODO: should be in usecase or repository layer
    private suspend fun compressImage(context: Context, uri: Uri): File {
        return Compressor.compress(context, uri.toFile(), viewModelScope.coroutineContext) {
            default(
                width = 240,
                height = 240
            )
        }
    }

}