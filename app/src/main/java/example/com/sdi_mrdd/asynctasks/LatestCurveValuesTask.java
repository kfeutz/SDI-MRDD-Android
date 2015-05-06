package example.com.sdi_mrdd.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import example.com.sdi_mrdd.dataitems.ApiUrl;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveValueParser;

/**
 * Created by Kevin on 4/26/2015.
 */
public class LatestCurveValuesTask extends AsyncTask<String, Void, Map> {
    HttpClient client = new DefaultHttpClient();
    String server;
    HttpGet request;
    String jsonString = "";
    String curveId;
    String wellId;
    long currentTimeLdap;
    Curve curveToUpdate;
    private CurveValueParser curveValueParser = CurveValueParser.getInstance();
    AsyncTaskCompleteListener<String> activity;

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /* Start value for REST call, typically user will specifiy this but their API is acting weird */
    private final long STARTLDAPTIME = 120737630793553000L;

    public LatestCurveValuesTask(AsyncTaskCompleteListener<String> activity,
                                 Curve curveToUpdate, String wellId) {
        this.activity = activity;
        this.curveToUpdate = curveToUpdate;
        this.curveId = curveToUpdate.getId();
        this.wellId = wellId;
        /* Converting current Unix epoch time to LDAP time format */
        this.currentTimeLdap = (System.currentTimeMillis() * 10000) + NANOSECONDSBETWEENEPOCHS;
        /* Make time curve call */
        if(curveToUpdate.getCurveType().equals("time_curve")) {
            server = ApiUrl.BASEURL + "/v2/getCurveFromCurveId?well="
                    + this.wellId + "&curve=" + this.curveId;
        }
        /* Make wellbore curve call */
        else {
            server = ApiUrl.BASEURL + "/v2/getWellboreCurveFromCurveId?well="
                    + this.wellId + "&curve=" + this.curveId;
        }

        request  = new HttpGet(server);
    }
    @Override
    protected Map doInBackground(String... params) {
        Scanner scanner;
        HttpResponse response;
        HttpEntity entity;
        Map<String, String> recentCurveValues;
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
        recentCurveValues = curveValueParser.getLastIvValue(jsonString);
        /* If dv value exists in the map, we have the most recent point */
        if(recentCurveValues.get("dvValue") != null) {
            return recentCurveValues;
        }
        /* Make another request to get latest curve values */
        else {
            return requestLatestValueMap(recentCurveValues.get("ivValue"));
        }
    }
    protected Map requestLatestValueMap(String endIvValue) {
        Scanner scanner;
        HttpResponse response;
        HttpEntity entity;
        Map<String, String> recentCurveValues;
        String latestJsonString= "";
        try {
            server += "&start=" + endIvValue;
            request  = new HttpGet(server);
            response = client.execute(request);
            entity = response.getEntity();

            if(entity != null) {
                InputStream input = entity.getContent();
                if (input != null) {
                    scanner = new Scanner(input);

                    while (scanner.hasNext()) {
                        latestJsonString += scanner.next() + " ";
                    }
                    input.close();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        recentCurveValues = curveValueParser.getLastIvValue(latestJsonString);
        return recentCurveValues;
    }
    @Override
    protected void onPostExecute(Map result) {
        Log.i("", "Result after getCurveFromCurveId GET : " + result);
        Map<String, String> newValueMap = result;
        curveToUpdate.setIvValue(newValueMap.get("ivValue"));
        curveToUpdate.setDvValue(newValueMap.get("dvValue"));
        activity.onTaskComplete("");
    }
}
