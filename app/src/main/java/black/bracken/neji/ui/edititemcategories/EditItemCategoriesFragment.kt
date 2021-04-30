package black.bracken.neji.ui.edititemcategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import black.bracken.neji.R
import black.bracken.neji.ui.compose.NejiTheme

class EditItemCategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                EditItemCategories()
            }
        }
    }

}

@Composable
fun ItemCategoryItem() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_bookmark_24),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Text("Test item")
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditItemCategories() {
    NejiTheme {
        LazyColumn {
            items(10) {
                ItemCategoryItem()
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}