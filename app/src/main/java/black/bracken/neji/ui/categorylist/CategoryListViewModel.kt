package black.bracken.neji.ui.categorylist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import black.bracken.neji.repository.FirebaseRepository

class CategoryListViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    val categories = firebaseRepository.itemCategories()
}