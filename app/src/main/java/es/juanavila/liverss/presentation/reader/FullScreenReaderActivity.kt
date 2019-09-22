package es.juanavila.liverss.presentation.reader

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NavUtils
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import es.juanavila.liverss.R

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullScreenReaderActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_full_screen_reader)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView = findViewById(R.id.webView)

        val extraURL = intent.getStringExtra("url")


        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.evaluateJavascript("document.querySelector('button.btn--acceptAll').click()") {}
                webView.evaluateJavascript("document.querySelector('button.didomi-dismiss-button').click()") {}
                webView.evaluateJavascript("document.querySelector('.banner_buttons--26GDw > div:nth-child(1) > button:nth-child(1)').click()") {}
            }
        }
        webView.loadUrl(extraURL)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
