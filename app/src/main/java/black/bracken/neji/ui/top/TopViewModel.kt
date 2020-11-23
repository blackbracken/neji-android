package black.bracken.neji.ui.top

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import black.bracken.neji.repository.FirebaseRepository

class TopViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regions = firebaseRepository.regions().asLiveData()

}