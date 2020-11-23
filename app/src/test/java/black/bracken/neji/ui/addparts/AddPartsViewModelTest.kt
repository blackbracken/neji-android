package black.bracken.neji.ui.addparts

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
class AddPartsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockkFirebase: FirebaseRepository
    private lateinit var viewModel: AddPartsViewModel

    @Before
    fun setup() {
        mockkFirebase = mockk(relaxed = true)
        viewModel = AddPartsViewModel(mockkFirebase)
    }

    @Test
    fun `setPartsImage sets actual uri`() {
        val onChanged = spyk<(Uri?) -> Unit>({})
        viewModel.imageUri.observeForever(onChanged)

        val uri = mockk<Uri>()
        viewModel.setPartsImage(uri)

        verifyOrder {
            onChanged(isNull())
            onChanged(uri)
        }

        viewModel.imageUri.removeObserver(onChanged)
    }

    @Test
    fun `setPartsImage sets uri as null`() {
        val onChanged = spyk<(Uri?) -> Unit>({})
        viewModel.imageUri.observeForever(onChanged)

        viewModel.setPartsImage(null)

        verifyOrder {
            onChanged(isNull())
            onChanged(isNull())
        }

        viewModel.imageUri.removeObserver(onChanged)
    }

}