package example.com.sdi_mrdd;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class WellListActivity extends ActionBarActivity {

    private WellAdapter listAdapter;
    /* Object to parse json strings into well objects */
    private WellJsonParser wellJsonParser = WellJsonParser.getInstance();
    ListView wellListView;
    List<Well> wellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_list);

        wellListView = (ListView) findViewById(R.id.well_list_view);

        new LoadWells().execute("");
    }

    private class LoadWells extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();
        String server = "http://10.0.3.2:5000/getWells";
        HttpGet request = new HttpGet(server);
        String jsonString = "";
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
                            jsonString += scanner.next();
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
            Log.i("", "Result after getWells GET : " + result);
            wellList = wellJsonParser.parse(result);
            listAdapter = new WellAdapter (WellListActivity.this, R.layout.listview_well_row, wellList);
            wellListView.setAdapter(listAdapter);
            wellListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {
                    Well value = (Well)adapter.getItemAtPosition(position);

                    Intent intent = new Intent(WellListActivity.this, WellDashBoardActivity.class);
                    intent.putExtra("well", value);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected void onPreExecute() {}
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_well_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
