package com.daduck.lazycolumnsoflazyrows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.daduck.lazycolumnsoflazyrows.DATA.ListOfList
import com.daduck.lazycolumnsoflazyrows.ui.theme.LazyColumnsOfLazyRowsTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumnsOfLazyRowsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    LazyColumnCompose()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LazyColumnCompose() {
    val fr = remember {
        FocusRequester()
    }
    val parentFocusState = rememberContainerFocusState(lastSelectedKey = ListOfList[0].first)
    TvLazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .focusProperties {
                enter = {
                    parentFocusState.lastInternalFocusRequester()
                }
            }
            .focusRequester(fr),
        pivotOffsets = PivotOffsets(0.5f, 0.2f)
    ) {
        this.items(
            items = ListOfList,
            key = { it.first },
            itemContent = { item ->
                val itemFocusOttProperties =
                    initChildFocusOttProperties(item.first, parentFocusState)

                LazyRowCompose(
                    data = item,
                    focusOttProperties = itemFocusOttProperties
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun LazyRowCompose(
    data: Pair<String, List<Pair<String, String>>>,
    focusOttProperties: FocusOttProperties
) {
    val parentFocusState =
        rememberContainerFocusState(lastSelectedKey = data.second.first().first)
    TvLazyRow(
        modifier = Modifier
            .focusProperties {
                enter = {
                    parentFocusState.lastInternalFocusRequester()
                }
            }
            .focusRequester(focusOttProperties.focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) focusOttProperties.onFocused?.invoke()
            },
    ) {
        items(
            data.second,
            key = { it.first },
        ) { model ->
            val itemFocusOttProperties =
                initChildFocusOttProperties(model.first, parentFocusState)

            Card(name = model.second, itemFocusOttProperties)
        }
    }
}

object DATA {
    val ListOfList: List<Pair<String, List<Pair<String, String>>>> = listOf(
        "1" to listOf("11" to "11", "12" to "12", "13" to "13"),
        "2" to listOf("21" to "21", "22" to "22", "23" to "23"),
        "3" to listOf("31" to "31", "32" to "32", "33" to "33"),
        "4" to listOf("41" to "41", "42" to "42", "43" to "43"),
        "5" to listOf("51" to "51", "52" to "52", "53" to "53"),
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Card(name: String, focusOttProperties: FocusOttProperties, modifier: Modifier = Modifier) {
    Surface(
        onClick = { // no need now
        },
        scale = ClickableSurfaceDefaults.scale(
            scale = 1f,
            focusedScale = 1.05f,
            pressedScale = 1.05f,
        ),
        shape = ClickableSurfaceDefaults.shape(
            shape = RectangleShape,
            focusedShape = RectangleShape
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border.None,
            focusedBorder = Border(
                border = BorderStroke(
                    2.dp,
                    SolidColor(MaterialTheme.colorScheme.primary)
                ),
                shape = RectangleShape
            )
        ),
        modifier = modifier
            .padding(top = 1.dp, bottom = 1.dp)
            .fillMaxWidth()
            .onFocusChanged { state ->
                if (state.isFocused) focusOttProperties.onFocused?.invoke()
            }
            .focusRequester(focusOttProperties.focusRequester)
    ) {
        Text(
            text = "Card No$name!",
            modifier = modifier
                .height(266.dp)
                .aspectRatio(3.0f / 2.0f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LazyColumnCompose()
}