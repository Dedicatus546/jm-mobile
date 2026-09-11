package com.par9uet.jm.ui.components

import android.content.ClipData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.dir.getDownloadCoverDataDir
import com.par9uet.jm.ui.provider.LocalImageLoader
import com.par9uet.jm.ui.provider.LocalRemoteSettingManager
import com.par9uet.jm.ui.provider.LocalToastManager
import com.par9uet.jm.utils.createComicCoverImageRequest
import kotlinx.coroutines.launch

import java.io.File

@Composable
fun ComicCoverImage(
    comic: Comic,
    showIdChip: Boolean = false,
) {
    val remoteSettingManager = LocalRemoteSettingManager.current
    val remoteSetting by remoteSettingManager.remoteSettingState.collectAsState()
    val model = "${remoteSetting.imgHost}/media/albums/${comic.id}_3x4.jpg"
    ComicCoverImage(
        id = comic.id,
        name = comic.name,
        model = model,
        showIdChip = showIdChip
    )
}

@Composable
fun ComicCoverImage(
    localComic: LocalComic,
    showIdChip: Boolean = false,
) {
    val context = LocalContext.current
    val model by remember {
        derivedStateOf {
            val dir = getDownloadCoverDataDir(context)
            File(dir, "${localComic.comicId}.webp").absolutePath
        }
    }
    ComicCoverImage(
        id = localComic.comicId,
        name = localComic.name,
        model = model,
        showIdChip = showIdChip
    )
}

@Composable
fun ComicCoverImage(
    id: Int,
    name: String,
    model: String,
    showIdChip: Boolean = false,
) {
    val imageLoader = LocalImageLoader.current
    val clipboard = LocalClipboard.current
    val toastManager = LocalToastManager.current
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            // model = "https://i0.hdslb.com/bfs/manga-static/c62668e300b5212fe5504f6fa9b4b5c630f8ebeb.jpg@310w.avif",
            model = createComicCoverImageRequest(
                context = LocalContext.current,
                url = model,
                comicId = id,
            ),
            imageLoader = imageLoader,
            contentDescription = "${name}的封面",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .fillMaxWidth(),
        )
        if (showIdChip) {
            val text = "JM${id}"
            AssistChip(
                border = null,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 10.dp, top = 10.dp),
                onClick = {
                    scope.launch {
                        val clipData = ClipData.newPlainText("本子号", text)
                        clipboard.setClipEntry(clipData.toClipEntry())
                        toastManager.show("复制成功")
                    }
                },
                label = {
                    Text(text)
                }
            )
        }
    }
}