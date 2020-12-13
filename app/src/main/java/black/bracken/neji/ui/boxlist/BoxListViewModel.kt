package black.bracken.neji.ui.boxlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.model.firebase.Box
import black.bracken.neji.model.firebase.Region
import black.bracken.neji.repository.FirebaseRepository
import black.bracken.neji.util.Loading
import black.bracken.neji.util.Resource
import black.bracken.neji.util.Success
import kotlinx.coroutines.launch

class BoxListViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _boxes = MutableLiveData<Resource<List<Box>>>(Loading)
    val boxes: LiveData<Resource<List<Box>>> get() = _boxes

    fun fetchBoxes(region: Region) {
        viewModelScope.launch {
            _boxes.postValue(Success(firebaseRepository.boxesInRegion(region)))
        }
    }

}