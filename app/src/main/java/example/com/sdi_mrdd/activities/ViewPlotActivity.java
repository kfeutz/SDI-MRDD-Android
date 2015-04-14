package example.com.sdi_mrdd.activities;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveValueParser;
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

    private CurvePoints curvePoints;

    private CurveValueParser curveValueParser = CurveValueParser.getInstance();

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

        setTitle(plotName);

        /*JSBtn =  (Button) findViewById(R.id.btn_testJS);
        JSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myWebView.loadUrl("javascript:setV(\""+
                    plotToDisplay.getCurves().get(0).getUnitFromRange(0, 20)+"\")");
            }
        });*/

        myWebView = (WebView) findViewById(R.id.webview);

        //Opens in-app instead of in browser
        myWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                //myWebView.loadUrl("javascript:InitChart(350,400,\""+curvePoints.getDvList().toArray()+"\",\""+curvePoints.getIvList().toArray()+"\")");
                myWebView.loadUrl("javascript:InitChart(350,400,[10,50,20,30,35,25],[10,20,30,40,50,60])");
                //myWebView.loadUrl("javascript:testData()");
            }
        });
        myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Enable javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.loadUrl("file:///android_asset/www/index.html");

        curvePoints = new CurvePoints(plotToDisplay.getCurves().get(0));
        curvePoints.execute();
    }

    public String getPlotURL() {
        return myWebView.getUrl();
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

    /**
     * Asynchronous Task class that makes a REST GET request to the backend service
     * to retrieve an array of doubles that represent the iv and dv values of a curve
     */
    private class CurvePoints extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();
        String server = "http://54.67.103.185/getCurveFromCurveIdPresent";
        HttpGet request;
        String curveId;
        String jsonString = "";
        Curve theCurve;
        List<Double> ivDoubleList;
        List<Double> dvDoubleList;

        private CurvePoints (Curve curve) {
            this.theCurve = curve;
            this.curveId = curve.getId();
            this.server += ("?curve=" + this.curveId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.request = new HttpGet(server);
        }


        /**
         * Executes the REST request. Reads the data as a string and appends
         * it into one large JSON string. It returns this value to onPostExecute(...)
         * result parameter
         *
         * @param params
         * @return String   The JSON string representation of an array of wells.
         */
        @Override
        protected String doInBackground(String... params) {
            Scanner scanner;
            HttpResponse response;
            HttpEntity entity;

            try {
                response = client.execute(request);
                entity = response.getEntity();

                if(entity != null) {
                    InputStream input = entity.getContent();
                    if (input != null) {
                        scanner = new Scanner(input);

                        while (scanner.hasNext()) {
                            jsonString += scanner.next() + " ";
                        }
                        input.close();
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        /**
         * Called after the REST call has completed. Parses the JSON string parameter result
         * and stores the iv and dv values into arrays
         *
         * @param result    The return value of the function above
         *                  'doInBackground(String... params)'
         */
        @Override
        protected void onPostExecute(String result) {
            String ivValues = curveValueParser.parseIvValues(result);
            List<String> ivValueList = Arrays.asList(ivValues.split(","));
            ivDoubleList = new ArrayList<>();
            /* Loop through the iv list and convert string values to doubles */
            for (int i = 0; i < ivValueList.size(); i++) {
                ivDoubleList.add(Double.parseDouble(ivValueList.get(i)));
            }
            String dvValues = curveValueParser.parseDvValues(result);
            List<String> dvValueList = Arrays.asList(dvValues.split(","));
             dvDoubleList = new ArrayList<>();
            /* Loop through the list and convert string values to doubles */
            for (int i = 0; i < ivValueList.size(); i++) {
                dvDoubleList.add(Double.parseDouble(dvValueList.get(i)));
            }

            /*myWebView.loadUrl("javascript:setData(\""+
                    dvDoubleList.toArray()+"\",\""+ivDoubleList.toArray()+"\")");*/
        }

        public List<Double> getDvList() {
            return dvDoubleList;
        }

        public List<Double> getIvList() {
            return ivDoubleList;
        }

        public String getDvIvString() {
            String value = "[";
            for (int i = 0; i < dvDoubleList.size(); i++) {
                value += "{'x': " + dvDoubleList.get(i) + ", 'y': " + ivDoubleList.get(i) + "},";
            }
            value += "]";
            return value;
        }
    }
}
