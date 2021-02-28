package black.bracken.neji.ui.additem

import android.content.Context
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.R
import black.bracken.neji.ext.toUnit
import black.bracken.neji.model.Box
import black.bracken.neji.repository.FirebaseRepository
import black.bracken.neji.util.ValidatedResult
import black.bracken.neji.util.compressImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddItemViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _registrationResult = MutableSharedFlow<Unit?>(replay = 0)
    val registrationResult = _registrationResult.asSharedFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    fun setImageUri(uri: Uri?) {
        viewModelScope.launch {
            _imageUri.emit(uri)
        }
    }

    fun addItem(
        context: Context,
        name: String,
        itemTypeName: String,
        amount: Int,
        comment: String,
        box: Box
    ) {
        viewModelScope.launch {
            val itemType = firebaseRepository.itemTypesOnce()
                ?.find { it.name == itemTypeName }
                ?: run {
                    _registrationResult.emit(null)
                    return@launch
                }

            _registrationResult.emit(
                firebaseRepository.addItem(
                    name = name,
                    amount = amount,
                    itemType = itemType,
                    box = box,
                    image = _imageUri.value?.let { compressImage(context, it) },
                    comment = comment
                )?.toUnit()
            )
        }
    }

    fun validateName(context: Context, text: String?): ValidatedResult<String> {
        return when {
            text.isNullOrBlank() -> {
                ValidatedResult.Failure(context.getString(R.string.error_must_not_be_blank))
            }
            else -> {
                ValidatedResult.Success(text)
            }
        }
    }

    fun validateAmount(context: Context, text: String?): ValidatedResult<Int> {
        return when {
            text?.toIntOrNull() == null -> {
                ValidatedResult.Failure(context.getString(R.string.error_must_not_be_blank))
            }
            text.toInt() < 0 -> {
                ValidatedResult.Failure(context.getString(R.string.error_must_be_integer_and_at_least_zero))
            }
            else -> {
                ValidatedResult.Success(text.toInt())
            }
        }
    }

    fun validateItemTypeText(context: Context, text: String?): ValidatedResult<String> {
        return when {
            text.isNullOrBlank() -> {
                ValidatedResult.Failure(context.getString(R.string.error_must_not_be_blank))
            }
            else -> {
                ValidatedResult.Success(text)
            }
        }
    }

}