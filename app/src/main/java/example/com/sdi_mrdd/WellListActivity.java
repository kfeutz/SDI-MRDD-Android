package example.com.sdi_mrdd;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class WellListActivity extends ActionBarActivity {

    private ArrayAdapter<String> listAdapter;
    ListView wellListView;
    ArrayList<String> wellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_list);

        wellListView = (ListView) findViewById(R.id.well_list_view);

        new LoadWells().execute("");
    }

    private class LoadWells extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();
        String server = "http://10.0.2.2:5000/getWells";
        HttpGet request = new HttpGet(server);
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpResponse response = client.execute(request);
                wellList = new ArrayList<String>();
                HttpEntity entity = response.getEntity();
                JSONArray wellArray = new JSONArray();

                if(entity != null) {
                    InputStream input = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String wells;
                    while((wells = reader.readLine()) != null){
                        wellArray = new JSONArray(wells);
                    }
                    for(int i = 0; i < wellArray.length(); i++)
                    {
                        wellList.add(wellArray.getString(i));
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            listAdapter = new ArrayAdapter<String>(WellListActivity.this, android.R.layout.simple_list_item_1, wellList);
            wellListView.setAdapter(listAdapter);
            wellListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {
                    String value = (String)adapter.getItemAtPosition(position);

                    Intent intent = new Intent(WellListActivity.this, WellDashBoardActivity.class);
                    intent.putExtra("title", value);
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
