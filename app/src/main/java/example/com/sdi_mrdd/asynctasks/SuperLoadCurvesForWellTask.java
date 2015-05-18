package example.com.sdi_mrdd.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.Scanner;

import example.com.sdi_mrdd.dataitems.ApiUrl;

/**
 * Created by Kevin on 5/14/2015.
 */
public abstract class SuperLoadCurvesForWellTask extends AsyncTask<String, Void, String> {
    HttpClient client = new DefaultHttpClient();
    String server;
    HttpGet request;
    String jsonString = "";
    String wellId;
    AsyncTaskCompleteListener<String> activity;

    public SuperLoadCurvesForWellTask(AsyncTaskCompleteListener<String> activity, String wellId) {
        this.activity = activity;
        this.wellId = wellId;
        this.server = ApiUrl.BASEURL + "/getCurvesForWell?well="
                + this.wellId;
        this.request  = new HttpGet(server);
    }

    @Override
    protected String doInBackground(String... params) {
        return ("");
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
