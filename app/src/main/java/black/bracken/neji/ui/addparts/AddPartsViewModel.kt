package black.bracken.neji.ui.addparts

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

    fun fetchBoxesInRegion(region: Region) {
        viewModelScope.launch {
            _boxes.postValue(firebaseRepository.boxesInRegion(region))
        }
    }

}