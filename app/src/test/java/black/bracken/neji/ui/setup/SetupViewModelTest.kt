package black.bracken.neji.ui.setup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import black.bracken.neji.repository.Auth
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockkAuth: Auth
    private lateinit var viewModel: SetupViewModel

    @Before
    fun setup() {
        mockkAuth = mockk(relaxed = true)
        viewModel = SetupViewModel(mockk(), mockk(), mockkAuth)
    }

    @Test
    fun `verifyFirebase success`() {
        val onChanged = spyk<(SetupViewModel.SignInState) -> Unit>({})
        viewModel.signInState.observeForever(onChanged)

        coEvery {
            mockkAuth.signInAndCacheIfSucceed(any(), any(), any(), any(), any())
        } returns mockk()

        runBlocking {
            viewModel.verifyFirebase("", "", "", "", "")
        }

        verifyOrder {
            onChanged(ofType(SetupViewModel.SignInState.Loading::class))
            onChanged(ofType(SetupViewModel.SignInState.Done::class))
        }
    }

}