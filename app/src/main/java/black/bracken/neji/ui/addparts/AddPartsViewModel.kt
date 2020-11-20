package black.bracken.neji.ui.addparts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import black.bracken.neji.repository.FirebaseRepository

class AddPartsViewModel @ViewModelInject constructor(
    firebaseRepository: FirebaseRepository
) : ViewModel() {

    val partsTypes = firebaseRepository.partTypes().asLiveData()

}