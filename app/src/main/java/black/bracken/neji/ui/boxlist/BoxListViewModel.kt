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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class BoxListViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _boxes: MutableStateFlow<List<Box>> = MutableStateFlow(listOf())
    val boxes: StateFlow<List<Box>> get() = _boxes

    fun fetchBoxes(region: Region) {
        viewModelScope.launch {
            firebaseRepository.boxesInRegion(region)
                .filterNotNull()
                .collect { box -> _boxes.emit(box) }
        }
    }

    fun deleteBox(box: Box) {
        viewModelScope.launch {
            firebaseRepository.deleteBox(box.id)
        }
    }

    interface BoxListItemClickListener {
        fun onClick(box: Box)

        companion object {
            operator fun invoke(lambdaListener: (Box) -> Unit): BoxListItemClickListener =
                object : BoxListItemClickListener {
                    override fun onClick(box: Box) {
                        lambdaListener(box)
                    }
                }
        }
    }

}