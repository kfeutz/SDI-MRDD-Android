package example.com.sdi_mrdd.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
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
import java.util.HashMap;
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
public class ViewPlotActivity extends ActionBarActivity implements AsyncTaskCompleteListener<String> {

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
    private Button previousPointsBtn;

    private ProgressDialog dialog;

    private int curveMapPositionOnPlot;

    /* Size of each point interval, based on first api result */
    public int intervalSize;

    private boolean currentlyPrepending = false;
    private boolean currentlyAppending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plot);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        setPlotToDisplay();

        createWebview();

        addRefreshButton();
    }

    private void setPlotToDisplay() {
        /**
         * Get Plot object  from intent extras
         * This field is passed from the onClickListener in the
         * ListView in WellPlotsFragment
         */
        plotToDisplay = getIntent().getParcelableExtra("plot");
        plotName = plotToDisplay.getName();

        setTitle(plotName);
    }

    private void createWebview() {
        myWebView = (WebView) findViewById(R.id.webview);

        myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Enable javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.setWebViewClient(new WebViewClient(){
            /* Wait for html to load all javascript before calling getting points and initiating chart */
            public void onPageFinished(WebView view, String url){
                initialPlotLoad = true;
                refreshPointsBtn.setEnabled(false);
                initPlot();
            }
        });

        myWebView.loadUrl("file:///android_asset/www/index.html");
    }

    private void addRefreshButton() {
        refreshPointsBtn = (Button)findViewById(R.id.btn_curve_points);
        refreshPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDialog();
                refreshPointsBtn.setEnabled(false);
                previousPointsBtn.setEnabled(false);
                updateLargerIv();
            }
        });
        previousPointsBtn = (Button)findViewById(R.id.btn_previous_points);
        previousPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDialog();
                refreshPointsBtn.setEnabled(false);
                previousPointsBtn.setEnabled(false);
                updateSmallerIv();
            }
        });
        showDialog();

        initialPlotLoad = true;
        refreshPointsBtn.setEnabled(false);
    }

    public void showDialog() {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Retrieving data");
        dialog.show();
    }

    public void closeDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
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
        return this.plotToDisplay.getCurves().get(0).getDvValues().toString();
    }

    public String getIvString() {
        return this.plotToDisplay.getCurves().get(0).getIvValues().toString();
    }

    public String getDateString(ArrayList<String> dateStrings) {
        if (dateStrings.size() > 0) {
            return dateStrings.toString();
        }
        else {
            return "n";
        }
    }

    public void initPlot() {
        currentlyAppending = true;
        new CurvePointsTask(ViewPlotActivity.this, plotToDisplay).execute();
    }

    /**
     * Method that updates a plot by retrieving the larger iv values from its current position
     * on the plot
     */
    public void updateLargerIv() {
        if(!currentlyAppending && !currentlyPrepending) {
            currentlyAppending = true;
            Curve curveToUpdate = this.plotToDisplay.getCurves().get(0);
            int endIndex = curveToUpdate.getPlotEndIndexForMap();
            if (endIndex > 0 && curveToUpdate.getStartEndIvValues().size() > 0) {
                int endMapPosition = endIndex - 1;
                /* Map containing the latest (largest) set of iv values for the curve.
                 * Contains startIv and endIv used to retrieve next set of data */
                HashMap<String, String> currentDataPosition
                        = curveToUpdate.getStartEndIvValues().get(endMapPosition);
                new CurvePointsTask(this, plotToDisplay,
                        currentDataPosition.get("startIv"), currentDataPosition.get("endIv")).execute();
            } else {
                new CurvePointsTask(this, plotToDisplay).execute();
            }
        }
    }

    /**
     * Method that updates a plot by retrieving the larger iv values from its current position
     * on the plot
     */
    public void updateSmallerIv() {
        if(!currentlyAppending && !currentlyPrepending) {
            currentlyPrepending = true;
            Curve curveToUpdate = this.plotToDisplay.getCurves().get(0);
            int ivStartIndex = curveToUpdate.getPlotStartIndexForMap();
            if (ivStartIndex > 0) {
                int startMapPosition = ivStartIndex - 1;
                /* Map containing the latest (largest) set of iv values for the curve.
                 * Contains startIv and endIv used to retrieve next set of data */
                HashMap<String, String> currentDataPosition
                        = curveToUpdate.getStartEndIvValues().get(startMapPosition);
                if(currentDataPosition.get("startIv").length() > 0 &&
                        currentDataPosition.get("endIv").length() > 0) {
                    new CurvePointsTask(this, plotToDisplay,
                            currentDataPosition.get("startIv"), currentDataPosition.get("endIv")).execute();
                }
                else {
                    new CurvePointsTask(this, plotToDisplay).execute();
                }
            }
            else {
                closeDialog();
                Toast toast = Toast.makeText(this, "You've reached the minimum", Toast.LENGTH_LONG);
                toast.show();
                currentlyPrepending = false;
                refreshPointsBtn.setEnabled(true);
                previousPointsBtn.setEnabled(true);
            }
        }
    }

    @Override
    public void onTaskComplete(String jsonString) {
        boolean curveFullyUpdated;
        if(currentlyAppending) {
            curveFullyUpdated = CurveValueParser.getInstance()
                    .parseIvDvValues(plotToDisplay.getCurves().get(0), jsonString, false);
        }
        else {
            curveFullyUpdated = CurveValueParser.getInstance()
                    .parseIvDvValues(plotToDisplay.getCurves().get(0), jsonString, true);
        }
        Curve curveResult = plotToDisplay.getCurves().get(0);

        Log.i("ViewPlotActivity", "Curve Start and End pointers " + curveResult.getPlotStartIndexForMap()
                    + " " + curveResult.getPlotEndIndexForMap() + " with mapsize of " + curveResult.getStartEndIvValues().size());
        Log.i("ViewPlotActivity", "Update the points iv values : " + getIvString());
        Log.i("ViewPlotActivity", "Update the points dv values : " + getDvString());
        Log.i("ViewPlotActivity", "Update the points next start value : "
                + this.plotToDisplay.getCurves().get(0).getNextStartUnit());
        Log.i("ViewPlotActivity", "Update the points next end value : "
                + this.plotToDisplay.getCurves().get(0).getNextEndUnit());

        closeDialog();
        /* Init plot for initial load */
        if (initialPlotLoad) {
            initialWebViewPlot(curveResult);
        }
        /* Check if curve has gotten all points from server */
        else  if (curveFullyUpdated && !initialPlotLoad) {
            Toast toast = Toast.makeText(this, "This is the most recent data", Toast.LENGTH_LONG);
            toast.show();
        }
        else if (currentlyAppending) {
            updateWebViewPlotAppend(curveResult);
        }
        else {
            updateWebViewPlotPrepend(curveResult);
        }
        initialPlotLoad = false;
        currentlyAppending = false;
        currentlyPrepending = false;
        refreshPointsBtn.setEnabled(true);
        previousPointsBtn.setEnabled(true);
    }

    public void updateWebViewPlotPrepend(Curve curveToPlot) {
        String jsCall = null;
        ArrayList<String> utcIvValues = new ArrayList<String>();
        if (curveToPlot.getCurveType().equals("time_curve")) {
            ArrayList<String> ivCurves = curveToPlot.getIvValues();
            Long ivValue;
            Long dateInMillis;
            int ivListSize = ivCurves.size();
            for (int i = 0; i < ivListSize; i++) {
                ivValue = Long.parseLong(ivCurves.get(i));
                dateInMillis = (ivValue - 116444736000000000L) / 10000;
                utcIvValues.add(i, dateInMillis.toString());
            }
            jsCall = "javascript:updatePrependValues(" + getDvString() + ","
                    + getDateString(utcIvValues) + ", " + curveToPlot.getMAX_POINTS_FOR_PLOT() + ")";
        }
        else {
            jsCall = "javascript:updatePrependValues(" + getDvString() + ","
                    + getIvString() + ", " + curveToPlot.getMAX_POINTS_FOR_PLOT() + ")";
        }
        Log.i("ViewPlotActivity", "Javascript call to refresh plot: " + jsCall);

        myWebView.loadUrl(jsCall);
    }

    public void updateWebViewPlotAppend(Curve curveToPlot) {
        String jsCall = null;
        ArrayList<String> utcIvValues = new ArrayList<String>();
        if (curveToPlot.getCurveType().equals("time_curve")) {
            ArrayList<String> ivCurves = curveToPlot.getIvValues();
            Long ivValue;
            Long dateInMillis;
            int ivListSize = ivCurves.size();
            for (int i = 0; i < ivListSize; i++) {
                ivValue = Long.parseLong(ivCurves.get(i));
                dateInMillis = (ivValue - 116444736000000000L) / 10000;
                utcIvValues.add(i, dateInMillis.toString());
            }
            jsCall = "javascript:updateAppendValues(" + getDvString() + ","
                    + getDateString(utcIvValues) + ", " + curveToPlot.getMAX_POINTS_FOR_PLOT() + ")";
        }
        else {
            jsCall = "javascript:updateAppendValues(" + getDvString() + ","
                    + getIvString() + ", " + curveToPlot.getMAX_POINTS_FOR_PLOT() + ")";
        }
        Log.i("ViewPlotActivity", "Javascript call to refresh plot: " + jsCall);

        myWebView.loadUrl(jsCall);
    }

    public void initialWebViewPlot(Curve curveToPlot) {
        String jsCall = null;
        ArrayList<String> utcIvValues = new ArrayList<String>();
        if (curveToPlot.getCurveType().equals("time_curve")) {
            ArrayList<String> ivCurves = curveToPlot.getIvValues();
            Long ivValue;
            Long dateInMillis;
            int ivListSize = ivCurves.size();
            for (int i = 0; i < ivListSize; i++) {
                ivValue = Long.parseLong(ivCurves.get(i));
                dateInMillis = (ivValue - 116444736000000000L) / 10000;
                utcIvValues.add(i, dateInMillis.toString());
                Log.i("ViewPlotActivity", "Epoch " + i + " : " + dateInMillis.toString());
                Log.i("ViewPlotActivity", "LDAP " + i + " : " + ivValue.toString());
            }
            jsCall = "javascript:InitChart(325,400," + getDvString() + ","
                    + getDateString(utcIvValues) + ",\"" + this.plotToDisplay.getCurves().get(0).getDvName()
                    + "\",\"" + this.plotToDisplay.getCurves().get(0).getIvName()
                    + "\",\"" + this.plotToDisplay.getTitle() + "\")";
        }
        else {
            jsCall = "javascript:InitChart(325,400," + getDvString() + ","
                    + getIvString() + ",\"" + this.plotToDisplay.getCurves().get(0).getDvName()
                    + "\",\"" + this.plotToDisplay.getCurves().get(0).getIvName()
                    + "\",\"" + this.plotToDisplay.getTitle() + "\")";
        }
        Log.i("ViewPlotActivity", "dvs: " + getDvString());
        Log.i("ViewPlotActivity", jsCall);

        /* Assign interval between points based on initial size of arrays */
        intervalSize = curveToPlot.getDvValues().size();

        myWebView.loadUrl("javascript:clear_chart()");
        myWebView.loadUrl(jsCall);
    }

    //Class to be injected in Web page
    public class WebAppInterface {
        ViewPlotActivity activity;

        /** Instantiate the interface and set the context */
        WebAppInterface(ViewPlotActivity a) {
            activity = a;
        }

        /**
         * Show Toast Message
         * @param toast
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void appendPlot() {
            activity.updateLargerIv();
        }

        @JavascriptInterface
        public void prependPlot() {
            activity.updateSmallerIv();
        }
    }
}
