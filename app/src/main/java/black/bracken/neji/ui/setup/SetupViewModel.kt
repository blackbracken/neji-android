package black.bracken.neji.ui.setup

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SetupViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle
) : ViewModel() {
}