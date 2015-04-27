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
public class LatestCurveValuesTask extends AsyncTask<String, Void, String> {
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
        server = ApiUrl.BASEURL + "/v2/getCurveFromCurveId?well="
                + this.wellId + "&curve=" + this.curveId
                + "&start=" + STARTLDAPTIME + "&end=" + currentTimeLdap;
        request  = new HttpGet(server);
    }
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

    @Override
    protected void onPostExecute(String result) {
        Log.i("", "Result after getCurveFromCurveId GET : " + result);
        Map<String, String> newValueMap = curveValueParser.parseRecentValues(result);
        curveToUpdate.setIvValue(newValueMap.get("ivValue"));
        curveToUpdate.setDvValue(newValueMap.get("dvValue"));
        activity.onTaskComplete("");
    }
}
