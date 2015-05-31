package example.com.sdi_mrdd.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.dataitems.ApiUrl;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveValueParser;
import example.com.sdi_mrdd.dataitems.WellboreCurve;

/**
 * Created by Kevin on 4/26/2015.
 */
public class LoadCurveDataTask extends AsyncTask<String, Void, String> {
    HttpClient client = new DefaultHttpClient();
    String server;
    HttpGet request;
    String jsonString = "";
    String curveId;
    String wellId;
    long currentTimeLdap;
    Curve curveToChange;

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /* Start value for REST call, typically user will specifiy this but their API is acting weird */
    private final long STARTLDAPTIME = 120737630793553000L;

    public LoadCurveDataTask(Curve curveToChange, String wellId) {
        this.curveId = curveToChange.getId();
        this.wellId = wellId;

        /* Converting current Unix epoch time to LDAP time format */
        this.currentTimeLdap = (System.currentTimeMillis() * 10000) + NANOSECONDSBETWEENEPOCHS;

        /* Make time curve call */
        if(curveToChange.getCurveType().equals("time_curve")) {
            server = ApiUrl.BASEURL + "/v2/getCurveFromCurveId?well="
                    + this.wellId + "&curve=" + this.curveId;
                    /*+ "&start=" + STARTLDAPTIME + "&end=" + currentTimeLdap;*/
        }
        /* Make wellbore curve call */
        else {
            server = ApiUrl.BASEURL + "/v2/getWellboreCurveFromCurveId?well="
                    + this.wellId + "&curve=" + this.curveId + "&wellbore="
                    + ((WellboreCurve) curveToChange).getWellboreId();
        }
        request  = new HttpGet(server);
    }
    @Override
    protected String doInBackground(String... params) {
        HttpResponse response;
        HttpEntity entity;
        try {
            response = client.execute(request);
            entity = response.getEntity();

            if(entity != null) {
                InputStream input = entity.getContent();
                if (input != null) {
                    String line = "";
                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(input));
                    while((line = bufferedReader.readLine()) != null) {
                        jsonString += line;
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

    @Override
    protected void onPostExecute(String result) {
        Log.i("", "Result after getCurveFromCurveId GET " + server + ": " + result);
    }
}

