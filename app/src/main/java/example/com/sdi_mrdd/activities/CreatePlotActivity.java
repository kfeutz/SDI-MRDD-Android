package example.com.sdi_mrdd.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveJsonParser;
import example.com.sdi_mrdd.database.DatabaseCommunicator;
import example.com.sdi_mrdd.dataitems.Plot;
import example.com.sdi_mrdd.R;

/**
 * Created by Kevin on 3/1/2015.
 */
public class CreatePlotActivity extends ActionBarActivity {
    /* Array adapter to display the titles of curves */
    private ArrayAdapter<String> listAdapter;

    /* List view to display the list of curves to add */
    private ListView curveListView;

    /* Reference to well title */
    private EditText inputTitle;

    /* Reference to the create plot button */
    private Button btnCreatePlot;

    /* Reference to the well in which the plot belongs */
    private String wellId;

    /* Reference to database to store new plots */
    private DatabaseCommunicator dbCommunicator;

    /* Object that parses json strings into curve objects */
    private CurveJsonParser curveJsonParser = CurveJsonParser.getInstance();

    /* The actual curves to display */
    private List<Curve> curveList = new ArrayList<Curve>();

    /* The curve titles to display */
    ArrayList<String> curveStringList = new ArrayList<String>();


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plot);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Initialize the db communicator */
        dbCommunicator = new DatabaseCommunicator(this.getApplication());
        dbCommunicator.open();
        while(dbCommunicator.isDbLockedByCurrentThread() || dbCommunicator.isDbLockedByOtherThreads()) {
            //db is locked, keep looping
        }

        wellId = getIntent().getExtras().getString("wellId");

        curveListView = (ListView) findViewById(R.id.create_plot_view);

        new LoadCurves().execute("");

        inputTitle = (EditText) findViewById(R.id.plot_name_entry);
        btnCreatePlot =  (Button) findViewById(R.id.btn_create_plot);
        btnCreatePlot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Ask the user if they want to add the curves
                new AlertDialog.Builder(CreatePlotActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.confirm_curve_add_title)
                        .setMessage(R.string.confirm_curve_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checked = CreatePlotActivity.this.getListView().getCheckedItemPositions();
                                List<Curve> curvesToAdd = new ArrayList<Curve>();
                                Plot plotToAdd;
                                int size = checked.size(); // number of name-value pairs in the array
                                for (int i = 0; i < size; i++) {
                                    int key = checked.keyAt(i);
                                    boolean value = checked.get(key);
                                    if (value && curveStringList != null) {
                                        /* Create the curve, create the plot, then add the curve to the plot */
                                        curvesToAdd.add(dbCommunicator.createCurve(curveList.get(key).getId(),
                                                curveList.get(key).getName(), wellId));
                                    }
                                }
                                plotToAdd = dbCommunicator.createPlot(inputTitle.getText().toString(),
                                        wellId);
                                /* Write all curves to the plot in our database */
                                for (int i = 0; i < curvesToAdd.size(); i++) {
                                    dbCommunicator.addCurveToPlot(plotToAdd, curvesToAdd.get(i));
                                }

                                Intent intent = new Intent(CreatePlotActivity.this, WellDashBoardActivity.class);
                                /*
                                  *   Adds the ArrayList of selected strings to the intent which will be passed to
                                  *   the well dashboard. The list of strings represent curve names to be added to
                                  *   the dashboard
                                 */
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                setResult(RESULT_OK, intent);

                                CreatePlotActivity.this.finish();
                                startActivity(intent);
                                //Stop the activity
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    private class LoadCurves extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();
        String server = "http://10.0.2.2:5000/getCurvesForWell?well="
                + CreatePlotActivity.this.getWellId();
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
            Log.i("", "Result after getCurvesForWell GET : " + result);
            curveList = curveJsonParser.parse(result);
            for (int i = 0; i < curveList.size(); i++) {
                curveStringList.add(curveList.get(i).getName());
            }
            listAdapter = new ArrayAdapter<String>(CreatePlotActivity.this,
                    android.R.layout.simple_list_item_multiple_choice, curveStringList);
            curveListView.setAdapter(listAdapter);
            curveListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            curveListView.setTextFilterEnabled(true);
        }

        @Override
        protected void onPreExecute() {}
    }

    public ListView getListView() {
        return curveListView;
    }

    public String getWellId() {
        return this.wellId;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_plot, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
