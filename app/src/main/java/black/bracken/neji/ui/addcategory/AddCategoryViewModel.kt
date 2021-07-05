package black.bracken.neji.ui.addcategory

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AddCategoryViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val _addingResult = MutableSharedFlow<Boolean>()
    val addingResult: Flow<Boolean> = _addingResult

    fun addCategory(name: String) {
        viewModelScope.launch {
            val result = firebaseRepository.addItemCategory(name)

            _addingResult.emit(result != null)
        }
    }

}