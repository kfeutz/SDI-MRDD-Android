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
    String server = ApiUrl.BASEURL + "/v2/getCurveFromCurveId";
    HttpGet request;
    String curveId;
    String wellId;
    String jsonString = "";
    Plot thePlot;
    List<Double> ivDoubleList;
    List<Double> dvDoubleList;
    private CurveValueParser curveValueParser = CurveValueParser.getInstance();
    long currentTimeLdap;

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /* Start value for REST call, typically user will specifiy this but their API is acting weird */
    private final long STARTLDAPTIME = 120737630793553000L;

    public CurvePointsTask (ViewPlotActivity activity) {
        this.thePlot = activity.getPlot();
        this.wellId = this.thePlot.getWellId();
        this.curveId = this.thePlot.getCurves().get(0).getId();
        /* Converting current Unix epoch time to LDAP time format */
        this.currentTimeLdap = (System.currentTimeMillis() * 10000) + NANOSECONDSBETWEENEPOCHS;
        this.server += ("?well=" + this.wellId + "&curve=" + this.curveId
            + "&start=" + STARTLDAPTIME + "&end=" + currentTimeLdap);
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

        /*String ivValues = curveValueParser.parseIvValues(result);
        List<String> ivValueList = Arrays.asList(ivValues.split(","));
        ivDoubleList = new ArrayList<>();

        for (int i = 0; i < ivValueList.size(); i++) {
            ivDoubleList.add(Double.parseDouble(ivValueList.get(i)));
        }
        String dvValues = curveValueParser.parseDvValues(result);
        List<String> dvValueList = Arrays.asList(dvValues.split(","));
        dvDoubleList = new ArrayList<>();

        for (int i = 0; i < ivValueList.size(); i++) {
            dvDoubleList.add(Double.parseDouble(dvValueList.get(i)));
        }*/
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