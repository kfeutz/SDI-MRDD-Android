package example.com.sdi_mrdd.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.Random;

import example.com.sdi_mrdd.dataitems.Plot;
import example.com.sdi_mrdd.R;

/**
 * This class displays a graphical representation of a plot. Each ViewPlotActivity
 * has a Plot and a String plotName
 */
public class ViewPlotActivity extends ActionBarActivity {

    /* Hold the plot object to display */
    private Plot plotToDisplay;

    /* Name of the plot used for title of page */
    private String plotName;

    private WebView myWebView;

    private Button JSBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plot);

        /**
         * Get Plot object  from intent extras
         * This field is passed from the onClickListener in the
         * ListView in WellPlotsFragment
         */
        plotToDisplay = getIntent().getParcelableExtra("plot");
        plotName = plotToDisplay.getName();
        /* Example of how to get a random unit value from a curve by providing a range */
        int num1 = plotToDisplay.getCurves().get(0).getUnitFromRange(10, 100);
        int num2 = plotToDisplay.getCurves().get(0).getUnitFromRange(10, 100);
        int num3 = plotToDisplay.getCurves().get(0).getUnitFromRange(10, 100);
        int num4 = plotToDisplay.getCurves().get(0).getUnitFromRange(10, 100);
        int num5 = plotToDisplay.getCurves().get(0).getUnitFromRange(10, 100);
        setTitle(plotName);

        JSBtn =  (Button) findViewById(R.id.btn_testJS);
        JSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myWebView.loadUrl("javascript:setV(\""+plotToDisplay.getCurves().get(0).getUnitFromRange(0, 20)+"\")");
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

    /**
     * Specifies which menu to render for ViewPlotActivity
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_plot, menu);
        return true;
    }

    /**
     * Handles action bar clicks
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* Example for seeing which item was selected */
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
