package example.com.sdi_mrdd.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Trace;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;

import example.com.sdi_mrdd.dataitems.ApiUrl;

/**
 * Created by Kevin on 4/26/2015.
 */
public class LoadCurvesForWellTask extends SuperLoadCurvesForWellTask {

    public LoadCurvesForWellTask(AsyncTaskCompleteListener<String> activity, String wellId) {
        super(activity, wellId);

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
        Log.i("", "Result after getCurvesForWell GET : " + result);
        activity.onTaskComplete(result);
    }
}
