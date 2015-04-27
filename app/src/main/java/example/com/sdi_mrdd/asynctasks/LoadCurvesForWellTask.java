package example.com.sdi_mrdd.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

import example.com.sdi_mrdd.dataitems.ApiUrl;

/**
 * Created by Kevin on 4/26/2015.
 */
public class LoadCurvesForWellTask extends AsyncTask<String, Void, String> {
    HttpClient client = new DefaultHttpClient();
    String server;
    HttpGet request;
    String jsonString = "";
    String wellId;
    AsyncTaskCompleteListener<String> activity;
    /** progress dialog to show user that the curves are loading. */
    private ProgressDialog dialog;

    public LoadCurvesForWellTask(AsyncTaskCompleteListener<String> activity, String wellId) {
        this.activity = activity;
        this.wellId = wellId;
        this.server = ApiUrl.BASEURL + "/getCurvesForWell?well="
                + this.wellId;
        this.request  = new HttpGet(server);
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
        Log.i("", "Result after getCurvesForWell GET : " + result);
        activity.onTaskComplete(result);
    }
}
