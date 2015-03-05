package example.com.sdi_mrdd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.Random;

import example.com.sdi_mrdd.R;

/**
 * Created by Olin on 2/3/2015.
 */
public class WebViewActivity extends ActionBarActivity {

    WebView myWebView;
    private Button JSBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        JSBtn =  (Button) findViewById(R.id.btn_testJS);
        JSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Random r = new Random();
                int i = r.nextInt(20 - 1) + 1;
                myWebView.loadUrl("javascript:setV(\""+i+"\")");
            }
        });

        myWebView = (WebView) findViewById(R.id.webview);

        //Opens in-app instead of in browser
        myWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                myWebView.loadUrl("javascript:showData()");
            }
        });
        myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Enable javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.loadUrl("file:///android_asset/www/index.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_well_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}