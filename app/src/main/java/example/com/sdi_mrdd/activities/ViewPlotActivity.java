package example.com.sdi_mrdd.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import example.com.sdi_mrdd.asynctasks.AsyncTaskCompleteListener;
import example.com.sdi_mrdd.asynctasks.CurvePointsTask;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveValueParser;
import example.com.sdi_mrdd.dataitems.Plot;
import example.com.sdi_mrdd.R;

/**
 * This class displays a graphical representation of a plot. Each ViewPlotActivity
 * has a Plot and a String plotName
 */
public class ViewPlotActivity extends ActionBarActivity implements AsyncTaskCompleteListener<Boolean> {

    /* Hold the plot object to display */
    public Plot plotToDisplay;

    /* Name of the plot used for title of page */
    private String plotName;

    private WebView myWebView;

    private Button JSBtn;

    private CurveValueParser curveValueParser = CurveValueParser.getInstance();

    private TextView plotNotification;

    public boolean initialPlotLoad;

    private Button refreshPointsBtn;

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

        myWebView = (WebView) findViewById(R.id.webview);

        //Opens in-app instead of in browser
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                //myWebView.loadUrl("javascript:InitChart(350,400,"+ curvePoints.getDvString()+","
                  //      +curvePoints.getIvString()+",\""+curvePoints.getCurve().getDvName()+"\",\""+curvePoints.getCurve().getIvName()+"\")");
            }
        });
        myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Enable javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.loadUrl("file:///android_asset/www/index.html");

        refreshPointsBtn = (Button)findViewById(R.id.btn_curve_points);
        refreshPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                refreshPointsBtn.setEnabled(false);
                new CurvePointsTask(ViewPlotActivity.this, plotToDisplay).execute();
            }
        });
        refreshPointsBtn.setEnabled(false);
        new CurvePointsTask(ViewPlotActivity.this, plotToDisplay).execute();
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
     * Gets the plot belonging to this activity
     *
     * @return Plot     The plot belonging to this activity
     */
    public Plot getPlot() {
        return this.plotToDisplay;
    }
    public String getDvString() {
        List<Double> dvDoubleList = this.plotToDisplay.getCurves().get(0).getDvValues();
        String value = "[";
        for (int i = 0; i < dvDoubleList.size(); i++) {
            value += dvDoubleList.get(i);
            if (i != dvDoubleList.size() - 1) {
                value += ", ";
            }
        }
        value += "]";
        return value;
    }

    public String getIvString() {
        String value = "[";
        List<String> ivDoubleList = this.plotToDisplay.getCurves().get(0).getIvValues();
        for (int i = 0; i < ivDoubleList.size(); i++) {
            value += ivDoubleList.get(i);
            if (i != ivDoubleList.size() - 1) {
                value += ", ";
            }
        }
        value += "]";
        return value;
    }

    public String getDateString(ArrayList<String> dateStrings) {
        if (dateStrings.size() > 0) {
            String value = "[";
            for (int i = 0; i < dateStrings.size(); i++) {
                value += dateStrings.get(i);
                if (i != dateStrings.size() - 1) {
                    value += ", ";
                }
                /*Log.i("ViewPlotActivity", "dateString i " + dateStrings.get(i));*/
            }
            value += "]";
            return value;
        }
        else {
            return "n";
        }
    }

    @Override
    public void onTaskComplete(Boolean curveFullyUpdated) {
        String jsCall = null;
        Curve curveResult = plotToDisplay.getCurves().get(0);
        Log.i("ViewPlotActivity", "Update the points iv values : " + getIvString());
        Log.i("ViewPlotActivity", "Update the points dv values : " + getDvString());
        Log.i("ViewPlotActivity", "Update the points next start value : " + this.plotToDisplay.getCurves().get(0).getNextStartUnit());
        Log.i("ViewPlotActivity", "Update the points next end value : " + this.plotToDisplay.getCurves().get(0).getNextEndUnit());
        Log.i("ViewPlotActivity", "Javascript call to refresh plot: " + "javascript:InitChart(350,400,"+ getDvString()+","
                + getIvString()+",\""+ this.plotToDisplay.getCurves().get(0).getDvName()+"\",\""+this.plotToDisplay.getCurves().get(0).getIvName()+"\")");

        /* Only update plot if curve is not fully updated */
        ArrayList<String> utcIvValues = new ArrayList<String>();
        if (curveResult.getCurveType().equals("time_curve")) {
            ArrayList<String> ivCurves = curveResult.getIvValues();
            Long ivValue;
            Long dateInMillis;
            for (int i = 0; i < ivCurves.size(); i++) {
                ivValue = Long.parseLong(ivCurves.get(i));
                dateInMillis = (ivValue - 116444736000000000L) / 10000;
                utcIvValues.add(i, dateInMillis.toString());
            }
            jsCall = "javascript:InitChart(350,400," + getDvString() + ","
                    + getDateString(utcIvValues) + ",\"" + this.plotToDisplay.getCurves().get(0).getDvName() + "\",\"" + this.plotToDisplay.getCurves().get(0).getIvName() + "\")";
        } else {
            jsCall = "javascript:InitChart(350,400," + getDvString() + ","
                    + getIvString() + ",\"" + this.plotToDisplay.getCurves().get(0).getDvName() + "\",\"" + this.plotToDisplay.getCurves().get(0).getIvName() + "\")";
        }

        myWebView.loadUrl("javascript:clear_chart()");
        myWebView.loadUrl(jsCall);

        /* Check if curve has gotten all points from server */
        if (curveFullyUpdated) {
            Toast toast = Toast.makeText(this, "This is the most recent data", Toast.LENGTH_LONG);
            toast.show();
            refreshPointsBtn.setEnabled(true);
        }
        else {
            refreshPointsBtn.setEnabled(true);
        }
    }
}
