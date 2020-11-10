package black.bracken.neji.ui.top

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class TopViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle
) : ViewModel()