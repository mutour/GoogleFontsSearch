package com.doufu.google.fonts.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.doufu.google.fonts.search.ui.theme.GoogleFontsSearchTheme
import com.doufu.google.fonts.search.utils.DFLog
import com.doufu.google.fonts.search.utils.gotoBrowser

class WebActivity : ComponentActivity() {
    companion object {
        private val INTENT_KEY_WEB_URL = "INTENT_KEY_WEB_URL"
        fun show(activity: Activity, url: String) {
            activity.startActivity(Intent(activity, WebActivity::class.java).apply {
                this.putExtra(INTENT_KEY_WEB_URL, url)
            })
        }
    }

    private val url = mutableStateOf("https://www.google.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            ProvideWindowInsets {
            GoogleFontsSearchTheme {
                Surface(color = MaterialTheme.colors.background) {
                    mainUI(url.value)
                }
            }
//            }
        }
        url.value = intent.getStringExtra(INTENT_KEY_WEB_URL) ?: url.value
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    @Composable
    fun mainUI(url: String) {
        val rememberProgress = remember { mutableStateOf(0.1f) }
        val showProgress = remember { mutableStateOf(false) }
        Column(modifier = Modifier) {
            if (showProgress.value) {
                LinearProgressIndicator(
                    progress = rememberProgress.value,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            WebViewView(modifier = Modifier.weight(1.0f),
                url = url, onProgressChanged = {
                    rememberProgress.value = it / 100.0f
                    showProgress.value = it != 100
                })
            BottomAppBar(
                modifier = Modifier.height(50.dp),
                backgroundColor = Color.White,
//                contentColor = contentColorFor(MaterialTheme.colors.backgroundColor),
//                cutoutShape: Shape? = null,
                elevation = AppBarDefaults.BottomAppBarElevation,
                contentPadding = AppBarDefaults.ContentPadding
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                    val (btnLeft, btnRight, btnBrowser) = createRefs()
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_baseline_chevron_left_24),
//                        contentDescription = stringResource(id = R.string.app_name),
//                        modifier = Modifier
//                            .width(48.dp)
//                            .height(48.dp)
//                            .constrainAs(btnLeft) {
//                                start.linkTo(parent.start, margin = 10.dp)
//                                end.linkTo(btnRight.start)
//                            }
//                            .clickable(onClick = {
//
//                            })
//                    )
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_baseline_chevron_right_24),
//                        contentDescription = stringResource(id = R.string.app_name),
//                        modifier = Modifier
//                            .width(48.dp)
//                            .height(48.dp)
//                            .constrainAs(btnRight) {
//                                start.linkTo(btnLeft.end)
//                            }
//                            .clickable(onClick = {
//
//                            })
//                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_open_in_browser_24),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .constrainAs(btnBrowser) {
                                end.linkTo(parent.end, margin = 10.dp)
                            }
                            .clickable(onClick = {
                                this@WebActivity.gotoBrowser(this@WebActivity.url.value)
                            })
                    )
                }
            }
        }

    }


    @Composable
    fun WebViewView(modifier: Modifier = Modifier, url: String = "https://www.google.com", onProgressChanged: (Int) -> Unit = {}) {
        AndroidView(factory = { context ->
            val webView = WebView(context)
            // configure webview
            webView.loadUrl(url)
            webView.settings.apply {
                setSupportZoom(true)
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    DFLog.Default.d("shouldOverrideUrlLoading url: $url")
                    url?.run {
                        view?.loadUrl(this)
                    }
                    return true
                }
            }
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    DFLog.Default.d("web progress: ${newProgress}/100")
                    onProgressChanged(newProgress)
                }
            }
            webView.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack()
                    true
                } else false
            }
            webView
        }, modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .fillMaxHeight()
            .then(modifier),
            update = { webView ->
                DFLog.Default.d("load url: $url")
                webView.loadUrl(url)
            })
    }
}