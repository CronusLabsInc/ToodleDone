package com.wclausen.toodledone

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar


class MainActivity : AppCompatActivity() {

    val LOG_TAG = "TOODLEDONE"

    lateinit var habitsWebView: WebView
    lateinit var tasksWebView: WebView
    lateinit var content: LinearLayout
    lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggleFullscreen()

        habitsWebView = findViewById(R.id.habitsWebView)
        habitsWebView.settings.javaScriptEnabled = true
        habitsWebView.webViewClient = ToodledoWebViewClient()

        tasksWebView = findViewById(R.id.tasksWebView)
        tasksWebView.settings.javaScriptEnabled = true
        tasksWebView.webViewClient = ToodledoWebViewClient()

        content = findViewById(R.id.content)
        loading = findViewById(R.id.loading)

        showLoading()

        habitsWebView.loadUrl("https://habits.toodledo.com")
        tasksWebView.loadUrl("https://www.toodledo.com/tasks/index.php")
    }

    override fun onResume() {
        super.onResume()
        toggleFullscreen()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Refresh")
        menu?.add("Fullscreen")
        menu?.add("Back (Habits)")
        menu?.add("Back (Tasks)")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.title == "Refresh") {
            refreshUrls()
            Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show()
        } else if (item?.title == "Fullscreen") {
            toggleFullscreen()
        } else if (item?.title == "Back (Habits)") {
            goBack(habitsWebView)
        } else if (item?.title == "Back (Tasks)") {
            goBack(tasksWebView)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeHeaders(view: WebView?) {
        view?.evaluateJavascript("var elem = document.getElementById('topnav-in'); elem.parentNode.removeChild(elem);", {})
    }

    private fun goBack(webView: WebView) {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            Toast.makeText(this, "No history to navigate back through", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFullscreen() {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions

        // Navigation bar hiding
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        // Status bar hiding
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        window.decorView.setSystemUiVisibility(newUiOptions)
    }

    private fun refreshUrls() {
        habitsWebView.loadUrl("https://habits.toodledo.com")
        tasksWebView.loadUrl("https://www.toodledo.com/tasks/index.php")
    }

    private fun showContent() {
        loading.visibility = View.GONE
        content.visibility = View.VISIBLE
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
        content.visibility = View.GONE
    }

    inner class ToodledoWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, webResourceRequest: WebResourceRequest): Boolean {
            val url = webResourceRequest.url
            Log.d(LOG_TAG, url.host)
            if (url.host == "habits.toodledo.com" || url.host == "www.toodledo.com") {
                // This is my web site, so do not override; let my WebView load the page
                return false
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent(Intent.ACTION_VIEW, url).apply {
                startActivity(this)
            }
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            removeHeaders(view)
            showContent()
        }

    }
}


