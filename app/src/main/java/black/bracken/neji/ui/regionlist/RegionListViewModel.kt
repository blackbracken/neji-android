package black.bracken.neji.ui.regionlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.flow.mapLatest

class RegionListViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    val regionAndAmountsResult = firebaseRepository.regions()
        .mapLatest { result ->
            when (result) {
                is Either.Right -> result.b
                    .map { region ->
                        region to (firebaseRepository.itemsInBox(region.id).orNull()?.size ?: 0)
                    }
                    .toMap()
                    .right()
                is Either.Left -> result.a.left()
            }
        }
        .asLiveData()

}