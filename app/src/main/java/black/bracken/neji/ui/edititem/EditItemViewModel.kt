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
import black.bracken.neji.util.Failure
import black.bracken.neji.util.Loading
import black.bracken.neji.util.Resource
import black.bracken.neji.util.Success
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class EditItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _editResult = MutableSharedFlow<Item?>(replay = 0)
    val editResult get() = _editResult.asSharedFlow()

    val regions = firebaseRepository.regions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val itemTypes = firebaseRepository.itemTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _boxes = MutableStateFlow<Resource<List<Box>>>(Loading)
    val boxes get() = _boxes.asStateFlow()

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?> get() = _imageUri

    fun setItemImage(uri: Uri?) {
        viewModelScope.launch {
            _imageUri.value = uri
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
        val itemType = itemTypes.value
            ?.find { it.toString() == itemTypeText }
            ?: return
        val box = (boxes.value as? Success)?.value
            ?.find { it.toString() == boxText }
            ?: return

        viewModelScope.launch {
            val item = firebaseRepository.editItem(
                item = source,
                name = name,
                amount = amount,
                itemType = itemType,
                image = imageUri.value?.also { compressImage(context, it) }?.toFile(),
                box = box,
                comment = comment
            )

            _editResult.emit(item)
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
            val boxes = firebaseRepository.boxesInRegionOnce(region)

            _boxes.emit(boxes?.let { Success(it) } ?: Failure())
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