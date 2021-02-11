package black.bracken.neji.ui.edititem

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import black.bracken.neji.model.document.Box
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.launch

class EditItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regionsResult = firebaseRepository.regions().asLiveData()
    val itemTypesResult = firebaseRepository.itemTypes().asLiveData()

    private val _boxes: MutableLiveData<Either<Exception, List<Box>>> = MutableLiveData()
    val boxesResult: LiveData<Either<Exception, List<Box>>> get() = _boxes

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?> get() = _imageUri

    fun setItemImage(uri: Uri?) {
        _imageUri.value = uri
    }

    fun addItem(
        name: String,
        amount: Int,
        itemType: String,
        boxName: String,
        comment: String?
    ): Either<Exception, Unit> {
        val box = when (val result = boxesResult.value) {
            null -> return IllegalStateException("the region didn't be selected").left()
            is Either.Left -> return IllegalStateException("network error").left()
            is Either.Right -> result.b.find { it.name == boxName }
                ?: return NoSuchElementException("no box found").left()
        }

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

        return Unit.right()
    }

    fun subscribeBoxesInRegion(regionId: String) {
        viewModelScope.launch {
            _boxes.postValue(firebaseRepository.boxesInRegion(regionId))
        }
    }

}