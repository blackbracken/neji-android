package black.bracken.neji.ui.boxlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import arrow.core.Either
import black.bracken.neji.model.document.Box
import black.bracken.neji.model.document.Region
import black.bracken.neji.repository.FirebaseRepository
import black.bracken.neji.util.Failure
import black.bracken.neji.util.Loading
import black.bracken.neji.util.Resource
import black.bracken.neji.util.Success
import kotlinx.coroutines.launch

class BoxListViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _boxAndItemCounts = MutableLiveData<Resource<Map<Box, Int>>>(Loading)
    val boxAndAmounts: LiveData<Resource<Map<Box, Int>>> get() = _boxAndItemCounts

    fun fetchBoxes(region: Region) {
        viewModelScope.launch {
            _boxAndItemCounts.postValue(
                when (val result = firebaseRepository.boxesInRegion(region.id)) {
                    is Either.Right -> {
                        Success(result.b
                            .map {
                                // FIXME: this is N+1
                                it to (firebaseRepository.itemsInBox(it.id).orNull()?.size ?: 0)
                            }
                            .toMap()
                        )
                    }
                    is Either.Left -> Failure(result.a)
                }
            )
        }
    }

}