package me.jovica.myapplication

import android.content.ClipData
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.jovica.myapplication.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App(photosViewModel: MainViewModel = viewModel()) {

    val photos by photosViewModel.photosList.collectAsState()
    val selectedImage = remember { mutableIntStateOf(-1) }
    val currentImageOffset = remember { mutableStateOf(Offset.Zero) }
    val lazyGridState = rememberLazyGridState()

    val youWin by remember { derivedStateOf { photos == me.jovica.myapplication.photos } }
    val haptic = LocalHapticFeedback.current

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = lazyGridState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(photos, key = { it.id }) { photo ->
                var backgroundColor by remember { mutableStateOf(Color.Transparent) }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { startEvent ->
                                true
                            },
                            target = object : DragAndDropTarget {
                                override fun onStarted(event: DragAndDropEvent) {
                                    backgroundColor = Color.DarkGray.copy(alpha = 0.2f)
                                }

                                override fun onDrop(event: DragAndDropEvent): Boolean {
                                    val dragedIndex =
                                        event.toAndroidDragEvent().clipData.getItemAt(0).text
                                            .toString()
                                            .toInt()
                                    photosViewModel.swapItems(dragedIndex, photo.id)
                                    return true
                                }

                                override fun onEnded(event: DragAndDropEvent) {
                                    backgroundColor = Color.Transparent
                                }
                            }
                        )
                        .dragAndDropSource {
                            detectTapGestures(
                                onLongPress = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    startTransfer(
                                        DragAndDropTransferData(
                                            clipData = ClipData.newPlainText(
                                                "text/",
                                                "${photo.id}"
                                            ),
                                            flags = View.DRAG_FLAG_GLOBAL,
                                        )
                                    )
                                }
                            )
                        }
                        .background(backgroundColor)

                ) {
                    Image(
                        painter = painterResource(photo.url),
                        contentDescription = photo.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(text = "${photo.id}")
                }
            }
        }
        if (youWin) {
            Text(text = "You Win")
            Button(onClick = { photosViewModel.reset() }) {
                Text(text = "Reset")
            }
        }
    }

}
