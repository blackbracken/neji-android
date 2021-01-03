package black.bracken.neji.ui.additem

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import arrow.core.Either
import black.bracken.neji.ext.squeezeLeft
import black.bracken.neji.ext.squeezeRight
import black.bracken.neji.model.document.Box
import black.bracken.neji.model.document.Region
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val regionResults = firebaseRepository.regions()
    private val itemTypeResults = firebaseRepository.itemTypes()
    val regions = regionResults.squeezeRight().asLiveData()
    val itemTypes = itemTypeResults.squeezeRight().asLiveData()

    private val _errors = MutableSharedFlow<Exception>(0, 0)
    val errors: SharedFlow<Exception> = _errors

    private val _boxes: MutableLiveData<List<Box>> = MutableLiveData()
    val boxes: LiveData<List<Box>> get() = _boxes

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?> get() = _imageUri

    init {
        viewModelScope.launch {
            regionResults.squeezeLeft().collectLatest { _errors.tryEmit(it) }
            itemTypeResults.squeezeLeft().collectLatest { _errors.tryEmit(it) }
        }
    }

    fun setItemImage(uri: Uri?) {
        _imageUri.value = uri
    }

    fun addItem(
        name: String,
        amount: Int,
        itemType: String,
        boxName: String,
        comment: String?
    ) {
        val box = boxes.value?.find { it.name == boxName }
            ?: throw IllegalStateException("failed to find box by name")

        viewModelScope.launch {
            firebaseRepository.addItem(
                name = name,
                imageUri = imageUri.value,
                amount = amount,
                itemType = itemType,
                boxId = box.id,
                comment = comment
            )
        }
    }

    fun subscribeBoxesInRegion(regionId: String) {
        viewModelScope.launch {
            when(val result = firebaseRepository.boxesInRegion(regionId)) {
                is Either.Right -> _boxes.postValue(result.b)
                is Either.Left -> _errors.tryEmit(result.a)
            }
        }
    }

}