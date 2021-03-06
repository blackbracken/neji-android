package black.bracken.neji.ui.addbox

import android.content.Context
import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.ext.toUnit
import black.bracken.neji.model.Region
import black.bracken.neji.repository.FirebaseRepository
import black.bracken.neji.util.createQrCode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddBoxViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _registrationResult = MutableSharedFlow<Unit?>(replay = 0)
    val registrationResult get() = _registrationResult.asSharedFlow()

    private val _qrCode = MutableStateFlow<Bitmap?>(null)
    val qrCode get() = _qrCode.asStateFlow()

    fun addBox(name: String, qrCodeText: String?, region: Region) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _registrationResult.emit(null)
                return@launch
            }

            if (firebaseRepository.boxesInRegionOnce(region)?.none { it.name == name } == true) {
                _registrationResult.emit(
                    firebaseRepository.addBox(name, qrCodeText, region)?.toUnit()
                )
            } else {
                _registrationResult.emit(null)
            }
        }
    }

    fun genQrCode(context: Context, text: String) {
        viewModelScope.launch {
            _qrCode.emit(createQrCode(context, text))
        }
    }

}