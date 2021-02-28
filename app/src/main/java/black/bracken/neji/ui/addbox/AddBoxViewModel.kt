package black.bracken.neji.ui.addbox

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.ext.toUnit
import black.bracken.neji.model.Region
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddBoxViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _registrationResult = MutableSharedFlow<Unit?>(replay = 0)
    val registrationResult get() = _registrationResult.asSharedFlow()

    fun addBox(name: String, region: Region) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _registrationResult.emit(null)
                return@launch
            }

            if (firebaseRepository.boxesInRegion(region)?.none { it.name == name } == true) {
                _registrationResult.emit(firebaseRepository.addBox(name, region)?.toUnit())
            } else {
                _registrationResult.emit(null)
            }
        }
    }

}