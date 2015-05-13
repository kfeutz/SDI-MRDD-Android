package example.com.sdi_mrdd.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.dataitems.ApiUrl;
import example.com.sdi_mrdd.dataitems.Well;
import example.com.sdi_mrdd.adapters.WellAdapter;
import example.com.sdi_mrdd.dataitems.WellJsonParser;

/**
 * This class makes a REST call to retrieve all Wells from our backend service.
 * It contains a WellAdapter to display the list of Wells, a Json parser to parse
 * JSON strings into java Well objects, a ListView to reference the view to render the
 * well list, and a list of Well objects retrieved from the REST call
 */
public class WellListActivity extends ActionBarActivity {

    /* Adapter to display Wells on the list view */
    private WellAdapter listAdapter;

    /* Object to parse json strings into well objects */
    private WellJsonParser wellJsonParser = WellJsonParser.getInstance();

    /* The view reference in which to display the well list */
    private ListView wellListView;

    /* The list of Well objects resulting from the REST call */
    private List<Well> wellList;

    Context context;

    /**
     * Sets the content view, declares our list view, and calls for
     * the asynchronous REST GET request to retrieve the list of wells
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_list);
        wellList = new ArrayList<Well>();
        listAdapter = new WellAdapter (WellListActivity.this, R.layout.listview_well_row, wellList);
        wellListView = (ListView) this.findViewById(R.id.well_list_view);
        wellListView.setAdapter(listAdapter);

        context = this;

        /* Set onClick listeners for each row. Open that well's dashboard activity. */
        wellListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                Well value = (Well)adapter.getItemAtPosition(position);
                    /* Attach the well object as an intent extra. Can now access well data
                     * in WellDashBoardActivity */
                Intent intent = new Intent(WellListActivity.this, WellDashBoardActivity.class);
                intent.putExtra("well", value);
                startActivity(intent);
            }
        });

        new LoadWells().execute("");
    }

    /**
     * Asynchronous Task class that makes a REST GET request to the backend service.
     * Currently, we are testing on local host.
     *
     * Use 'http://10.0.3.2:5000/' for Genymotion emulators
     * Use 'http://10.0.2.2:5000/' for Android Studio emulators
     */
    private class LoadWells extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();
        String server = ApiUrl.BASEURL + "/getWells";
        HttpGet request = new HttpGet(server);
        String jsonString = "";

        /** progress dialog to show user that the wells are loading. */
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Retrieving wells");
            dialog.show();
        }

        /**
         * Executes the REST getWells request. Reads the data as a string and appends
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
         * and adds each well to the well adapter. The list view's adapter is set to the well
         * adapter and each item has an on click listener to direct the user to the respective
         * Well dashboard page.
         *
         * @param result    The return value of the function above
         *                  'doInBackground(String... params)'
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i("", "Result after getWells GET : " + result);
            wellList.addAll(wellJsonParser.parse(result));
            listAdapter.notifyDataSetChanged();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Renders the menu_well_list.xml display data for this menu
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_well_list, menu);
        return true;
    }

    /**
     * Adds listeners for the menu items.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
