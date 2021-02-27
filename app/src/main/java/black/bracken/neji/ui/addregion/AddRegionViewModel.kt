package black.bracken.neji.ui.addregion

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddRegionViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _registrationResult = MutableSharedFlow<Unit?>(replay = 0)
    val registrationResult get() = _registrationResult.asSharedFlow()

    fun addRegion(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _registrationResult.emit(null)
                return@launch
            }

            if (firebaseRepository.findRegionByName(name) == null) {
                firebaseRepository.addRegion(name)
                _registrationResult.emit(Unit)
            } else {
                _registrationResult.emit(null)
            }
        }
    }

}