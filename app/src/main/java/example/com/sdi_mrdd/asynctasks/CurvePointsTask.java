package example.com.sdi_mrdd.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import example.com.sdi_mrdd.activities.ViewPlotActivity;
import example.com.sdi_mrdd.dataitems.ApiUrl;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveValueParser;
import example.com.sdi_mrdd.dataitems.Plot;

/**
 * Created by Kevin on 4/26/2015.
 */
public class CurvePointsTask extends AsyncTask<String, Void, String> {
    HttpClient client = new DefaultHttpClient();
    String server;
    HttpGet request;
    String curveId;
    String wellId;
    String jsonString = "";
    Plot thePlot;
    List<Double> ivDoubleList;
    List<Double> dvDoubleList;
    private CurveValueParser curveValueParser = CurveValueParser.getInstance();
    long currentTimeLdap;
    Curve curveToChange;
    AsyncTaskCompleteListener<Curve> activity;

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /* Start value for REST call, typically user will specifiy this but their API is acting weird */
    private final long STARTLDAPTIME = 120737630793553000L;

    public CurvePointsTask (AsyncTaskCompleteListener<Curve> activity, Plot thePlot) {
        this.activity = activity;
        this.wellId = thePlot.getWellId();
        this.curveToChange = thePlot.getCurves().get(0);
        this.curveId = curveToChange.getId();
        /* Converting current Unix epoch time to LDAP time format */
        this.currentTimeLdap = (System.currentTimeMillis() * 10000) + NANOSECONDSBETWEENEPOCHS;
        if(curveToChange.getNextStartUnit() != 0 && curveToChange.getNextEndUnit() != 0) {
            server = ApiUrl.BASEURL + "/v2/getCurveFromCurveId?well="
                    + this.wellId + "&curve=" + this.curveId
                    + "&start=" + curveToChange.getNextStartUnit() + "&end=" + curveToChange.getNextEndUnit();
        }
        else {
            server = ApiUrl.BASEURL + "/v2/getCurveFromCurveId?well="
                    + this.wellId + "&curve=" + this.curveId;
        }
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
        Log.i("ViewPlotActivity", "GET call result: " + result);
        Curve newCurve = CurveValueParser.getInstance().parseIvDvValues(curveToChange, result);
        this.activity.onTaskComplete(newCurve);
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

    public String getDvString() {
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
        for (int i = 0; i < ivDoubleList.size(); i++) {
            value += ivDoubleList.get(i);
            if (i != ivDoubleList.size() - 1) {
                value += ", ";
            }
        }
        value += "]";
        return value;
    }
}