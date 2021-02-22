package black.bracken.neji.ui.boxlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.Box
import black.bracken.neji.model.Region
import black.bracken.neji.repository.FirebaseRepository
import black.bracken.neji.util.Failure
import black.bracken.neji.util.Loading
import black.bracken.neji.util.Resource
import black.bracken.neji.util.Success
import kotlinx.coroutines.launch

class BoxListViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _boxAndItemCounts = MutableLiveData<Resource<Map<Box, Int>>>(Loading)
    val boxAndAmounts: LiveData<Resource<Map<Box, Int>>> get() = _boxAndItemCounts

    fun fetchBoxes(region: Region) {
        viewModelScope.launch {
            val result = firebaseRepository
                .boxesInRegion(region)
                ?.map { box -> box to (firebaseRepository.itemsInBox(box)?.size ?: 0) }
                ?.toMap()
                ?.let { boxAndAmounts -> Success(boxAndAmounts) }
                ?: Failure()

            _boxAndItemCounts.postValue(result)
        }
    }

}