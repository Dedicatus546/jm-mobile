import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable

@Composable
fun <T> Comp1(
    items: MutableList<T>,
    columns: Int = 2,
    itemContent: @Composable LazyGridItemScope.(item: T) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns)
    ) {
        // 重点：这里要使用 items()
        items(items) { item ->
            itemContent(item)
        }
    }
}