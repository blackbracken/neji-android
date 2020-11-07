package black.bracken.neji.ui.setup

import androidx.datastore.DataStore
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import black.bracken.neji.NejiSecure

class SetupViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val nejiSecure: DataStore<NejiSecure>
) : ViewModel()