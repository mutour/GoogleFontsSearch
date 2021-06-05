package com.doufu.google.fonts.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.doufu.google.fonts.search.ui.theme.GoogleFontsSearchTheme
import com.doufu.google.fonts.search.utils.DFLog

class MainActivity : ComponentActivity() {

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this.application).create(GoogleFontsModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleFontsSearchTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainUI(viewModel.familyNames, onSearchChange = {
                        DFLog.Default.d("search: $it")
                        viewModel.search(it)
                    }, onClick = {
                        viewModel.goto(this, it)
                    })
                }
            }
        }
        viewModel.search("")
    }
}

@Composable
fun FontInfo(fontInfo: FontInfo, onClick: (FontInfo) -> Unit) {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .clickable {
                    onClick(fontInfo)
                }
                .padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                drawCircle(
                    color = Color.Blue,
                    center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    radius = size.minDimension / 4
                )
            }
            val text = buildAnnotatedString {
                var prevIndex = 0
                fontInfo.searchMatchGroups?.forEach {
                    append(fontInfo.name.substring(prevIndex, it.range.first))

                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append(fontInfo.name.substring(it.range.first, it.range.last + 1))
                    }
                    prevIndex = it.range.last + 1
                }
                append(fontInfo.name.substring(prevIndex, fontInfo.name.length))
            }
            Text(text = text)
            Image(painter = painterResource(id = R.drawable.ic_baseline_arrow_right_alt_24), contentDescription = "go")

        }
        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color(0x22000000))
        )
    }
}

@Composable
fun MainUI(familyNames: List<FontInfo>, onSearchChange: (String) -> Unit = {}, onClick: (FontInfo) -> Unit = {}) {
    val searchText = remember { mutableStateOf("") }
    Column {
        TextField(value = searchText.value, modifier = Modifier.fillMaxWidth(), onValueChange = {
            searchText.value = it
            onSearchChange(it)
        }, leadingIcon = {
            Image(painter = painterResource(id = R.drawable.ic_search_48), contentDescription = "search")
        }, singleLine = true,
            placeholder = {
                Text(text = stringResource(id = R.string.search_placeholder))
            }
        )
//        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(familyNames) { name ->
                FontInfo(name, onClick)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoogleFontsSearchTheme {
        MainUI((0..100).map { FontInfo("font$it") })
    }
}