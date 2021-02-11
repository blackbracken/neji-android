package black.bracken.neji.ui.edititem

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import black.bracken.neji.repository.FirebaseRepository
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditItemViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockkFirebase: FirebaseRepository
    private lateinit var viewModel: EditItemViewModel

    @Before
    fun setup() {
        mockkFirebase = mockk(relaxed = true)
        viewModel = EditItemViewModel(mockkFirebase)
    }

    @Test
    fun `setItemImage sets actual uri`() {
        val onChanged = spyk<(Uri?) -> Unit>({})
        viewModel.imageUri.observeForever(onChanged)

        val uri = mockk<Uri>()
        viewModel.setItemImage(uri)

        verifyOrder {
            onChanged(isNull())
            onChanged(uri)
        }

        viewModel.imageUri.removeObserver(onChanged)
    }

    @Test
    fun `setItemImage sets uri as null`() {
        val onChanged = spyk<(Uri?) -> Unit>({})
        viewModel.imageUri.observeForever(onChanged)

        viewModel.setItemImage(null)

        verifyOrder {
            onChanged(isNull())
            onChanged(isNull())
        }

        viewModel.imageUri.removeObserver(onChanged)
    }

}