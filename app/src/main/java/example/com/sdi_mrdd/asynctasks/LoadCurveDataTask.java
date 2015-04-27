package example.com.sdi_mrdd.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.Scanner;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.dataitems.ApiUrl;

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

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /* Start value for REST call, typically user will specifiy this but their API is acting weird */
    private final long STARTLDAPTIME = 120737630793553000L;

    public LoadCurveDataTask(String curveId, String wellId) {
        this.curveId = curveId;
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
    }
}

