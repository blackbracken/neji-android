package black.bracken.neji.ui.addparts

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.model.firebase.Box
import black.bracken.neji.model.firebase.Region
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.launch

class AddPartsViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regions = firebaseRepository.regions().asLiveData()
    val partsTypes = firebaseRepository.partTypes().asLiveData()

    private val _boxes: MutableLiveData<List<Box>> = MutableLiveData()
    val boxes: LiveData<List<Box>> get() = _boxes

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?> get() = _imageUri

    fun setPartsImage(uri: Uri?) {
        _imageUri.value = uri
    }

    fun addParts(
        name: String,
        amount: Int,
        partsType: String,
        regionName: String,
        boxName: String,
        comment: String?
    ) {
        val region = regions.value?.find { it.name == regionName }
            ?: throw IllegalStateException("failed to find region by name")
        val box = boxes.value?.find { it.name == boxName }
            ?: throw IllegalStateException("failed to find box by name")

        viewModelScope.launch {
            firebaseRepository.addParts(
                name = name,
                imageUri = imageUri.value,
                amount = amount,
                partsType = partsType,
                region = region,
                box = box,
                comment = comment
            )
        }
    }

    fun fetchBoxesInRegion(region: Region) {
        viewModelScope.launch {
            _boxes.postValue(firebaseRepository.boxesInRegion(region))
        }
    }

}