package example.com.sdi_mrdd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Olin on 2/3/2015.
 */
public class WebViewActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView myWebView = (WebView) findViewById(R.id.webview);

        //Opens in-app instead of in browser
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Enable javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.loadUrl("http://www.gameinformer.com/");
        //myWebView.loadUrl("file:///android_asset/www/index.html");
        //myWebView.loadUrl("javascript:showData()");

        //For minimum API 19 and up
        /*myWebView.evaluateJavascript("showData()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //store / process result received from executing Javascript.
            }
        }); */
    }
}