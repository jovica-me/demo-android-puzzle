package me.jovica.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
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


@Composable
fun App(photosViewModel: MainViewModel = viewModel()) {

    val photos by photosViewModel.photosList.collectAsState()
    val selectedImage = remember { mutableStateOf(-1) }
    val currentImageOffset = remember { mutableStateOf(Offset.Zero) }
    val lazyGridState = rememberLazyGridState()

    val youWin by remember { derivedStateOf { photos.equals(me.jovica.myapplication.photos) } }
    val haptic = LocalHapticFeedback.current

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = lazyGridState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.gridDragAndDrop(
                lazyGridState = lazyGridState,
                setSelectedImage = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    selectedImage.value = it
                },
                setCurrentOffset = {
                    currentImageOffset.value = it
                },
                swapItems = { id1, id2 ->
                    photosViewModel.swapItems(id1, id2)
                }
            )

        ) {

            items(photos, key = { it.id }) { photo ->
                val selected by remember { derivedStateOf { selectedImage.value == photo.id } }
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (selected) 1.0f else 0.8f,
                    label = "alpha"
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            if (selected) {
                                translationX = -currentImageOffset.value.x
                                translationY = -currentImageOffset.value.y
                                alpha = animatedAlpha * 0.8f
                                scaleX = animatedAlpha
                                scaleY = animatedAlpha
                            }
                        }
                        .aspectRatio(1f)
                        .background(
                            Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Yellow,
                                    0.2f to Color.Red,
                                    1f to Color.Blue
                                )
                            )
                        )
                        .then(
                            if (selectedImage.value == photo.id) {
                                Modifier.zIndex(1f)
                            } else {
                                Modifier
                            }
                        )

                ) {
                    Image(
                        painter = rememberAsyncImagePainter(photo.url),
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


fun Modifier.gridDragAndDrop(
    lazyGridState: LazyGridState,
    setCurrentOffset: (Offset) -> Unit = {},
    swapItems: (id1: Int, id2: Int) -> Unit = { _, _ -> },
    setSelectedImage: (Int) -> Unit
) =
    pointerInput(Unit) {

        fun photoIdAndOffset(hitPoint: Offset): Int? {
            return lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
                itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
            }?.key as? Int
        }


        var selectedPhotoId: Int? = null
        var dropedPhotoId: Int? = null

        var selectedPhotoOffset: Offset = Offset.Zero

        detectDragGesturesAfterLongPress(

            onDragStart = { offset ->

                photoIdAndOffset(offset)?.let { photoId ->
                    selectedPhotoId = photoId
                    dropedPhotoId = photoId
                    setSelectedImage(selectedPhotoId!!)

                    selectedPhotoOffset = offset
                    Log.d("Drag", "Drag started at $offset with photoId $photoId")
                }

            },

            onDrag = { change, dragAmount ->
                setCurrentOffset(selectedPhotoOffset - change.position)
                if (selectedPhotoId != null) {
                    photoIdAndOffset(change.position)?.let { pointerPhotoId ->
                        if (dropedPhotoId != pointerPhotoId) {
                            dropedPhotoId = pointerPhotoId
                            Log.d("Drag", "Dragged to ${dragAmount} with photoId $selectedPhotoId")
                        }
                    }
                }
            },
            onDragEnd = {
                swapItems(selectedPhotoId!!, dropedPhotoId!!)
                selectedPhotoOffset = Offset.Zero
                setCurrentOffset(Offset.Zero)
                setSelectedImage(-1)
                selectedPhotoId = null
                Log.d("Drag", "Drag ended with photoId $dropedPhotoId")
            },
            onDragCancel = {
                selectedPhotoOffset = Offset.Zero
                setSelectedImage(-1)
                setCurrentOffset(Offset.Zero)
                selectedPhotoId = null
                Log.d("Drag", "Drag cancelled with photoId $dropedPhotoId")
            },
        )
    }
