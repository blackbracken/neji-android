package black.bracken.neji.ui.categorylist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.model.ItemCategory
import black.bracken.neji.repository.FirebaseRepository
import kotlinx.coroutines.launch

class CategoryListViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    val categories = firebaseRepository.itemCategories()

    fun deleteCategory(category: ItemCategory) {
        viewModelScope.launch {
            val result = firebaseRepository.deleteItemCategory(category.id, category.name)

            println("delete category result: $result")
        }
    }
}