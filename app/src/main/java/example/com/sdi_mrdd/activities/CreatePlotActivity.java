package example.com.sdi_mrdd.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import example.com.sdi_mrdd.asynctasks.AsyncTaskCompleteListener;
import example.com.sdi_mrdd.asynctasks.LoadCurveDataTask;
import example.com.sdi_mrdd.asynctasks.LoadCurvesForWellTask;
import example.com.sdi_mrdd.dataitems.ApiUrl;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveJsonParser;
import example.com.sdi_mrdd.dataitems.CurvesForWellJsonParser;
import example.com.sdi_mrdd.database.DatabaseCommunicator;
import example.com.sdi_mrdd.dataitems.Plot;
import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.dataitems.WellboreCurve;

/**
 * This class represents an Activity that allows the user to
 * create a plot. Currently, this activity has a ListView to display curves to
 * add to the plot, an EditText to prompt the user for a plot name, and a button
 * for the user to create the plot. Curves are populated in the list after a
 * REST GET call. After the user confirms adding a plot, the plot is added to the
 * sqlite database and the user is returned to the plot list fragment.
 *
 * Created by Kevin on 3/1/2015.
 */
public class CreatePlotActivity  extends ActionBarActivity implements AsyncTaskCompleteListener<String> {

    /* Array adapter to display the titles of curves */
    private ArrayAdapter<String> listAdapter;

    /* List view to display the list of curves to add */
    private ListView curveListView;

    /* Reference to well title */
    private EditText inputTitle;

    /* Reference to Radio group */
    private RadioGroup ivRadioGroup;

    /* Reference to depth radio button */
    private RadioButton depthRadio;

    /* Reference to time radio button */
    private RadioButton timeRadio;

    /* Reference to the create plot button */
    private Button btnCreatePlot;

    /* Reference to the well in which the plot belongs */
    private String wellId;

    /* Reference to database to store new plots */
    private DatabaseCommunicator dbCommunicator;

    /* Object that parses json strings into curve objects */
    private CurvesForWellJsonParser curvesForWellJsonParser = CurvesForWellJsonParser.getInstance();

    private CurveJsonParser curveJsonParser = CurveJsonParser.getInstance();

    /* The actual curves to display */
    private List<Curve> curveList = new ArrayList<Curve>();

    /* List of selected curves to add to plot */
    private List<Curve> curvesToAddList = new ArrayList<Curve>();

    private Spinner ivSpinner;

    Map<String, Curve> timeCurveMap = new HashMap<>();
    Map<String, WellboreCurve> wellboreMeasuredDepthCurveMap = new HashMap<>();
    Map<String, WellboreCurve> wellboreVertDepthCurveMap = new HashMap<>();
    Map<String, WellboreCurve> wellboreVertSectionCurveMap = new HashMap<>();

    /* The curve titles to display */
    ArrayList<String> curveStringList = new ArrayList<String>();

    /** progress dialog to show user that the curves are loading. */
    private ProgressDialog dialog;


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

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Retrieving curves");
        dialog.show();
        new LoadCurvesForWellTask(this, this.wellId).execute();

        inputTitle = (EditText) findViewById(R.id.plot_name_entry);

        ivSpinner = (Spinner) findViewById(R.id.iv_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"Time", "Measured Depth",
                "True Vertical Depth", "Vertical Section"});
        ivSpinner.setAdapter(spinnerAdapter);

        btnCreatePlot =  (Button) findViewById(R.id.btn_create_plot);
        btnCreatePlot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray checked = CreatePlotActivity.this.getListView().getCheckedItemPositions();
                if (checked.size() > 0) { // Making sure there is at least 1 curve to plot
                /* Ask the user if they want to add the curves to the plot */
                    new AlertDialog.Builder(CreatePlotActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.confirm_curve_add_title)
                            .setMessage(R.string.confirm_curve_msg)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SparseBooleanArray checked = CreatePlotActivity.this.getListView().getCheckedItemPositions();
                                    Plot plotToAdd;
                                    String curveToAddId = null;
                                    String curveToAddName;
                                    String curveType = null;
                                    Curve curveToAdd = null;
                                    Curve addedCurve;
                                    String jsonLoadedCurve;
                                    int size = checked.size(); // number of name-value pairs in the array
                                    for (int i = 0; i < size; i++) {
                                        int key = checked.keyAt(i);
                                        boolean value = checked.get(key);
                                        if (value && curveStringList != null) {
                                            curveToAddName = listAdapter.getItem(key);
                                            try {
                                                addedCurve = null;
                                                String ivSelection = ivSpinner.getSelectedItem().toString();
                                                /* Time curve selected */
                                                if (ivSelection.equals("Time")) {
                                                    curveToAdd = timeCurveMap.get(curveToAddName);
                                                    /* Retrieve data for the curve (ie. iv and dv names) */
                                                    jsonLoadedCurve = new LoadCurveDataTask(curveToAdd, wellId).execute().get();
                                                    curveJsonParser.parseIvDvNames(curveToAdd, jsonLoadedCurve, curveToAdd.getId(),
                                                            curveToAddName, curveToAdd.getCurveType());
                                                    addedCurve = dbCommunicator.createTimeCurve(curveToAdd.getId(),
                                                            curveToAdd.getName(), curveToAdd.getIvName(),
                                                            curveToAdd.getDvName(), curveToAdd.getIvUnit(),
                                                            curveToAdd.getDvUnit(), wellId, curveToAdd.getCurveType());
                                                }
                                                else if (ivSelection.equals("Measured Depth")) {
                                                    WellboreCurve wellboreCurveToAdd = wellboreMeasuredDepthCurveMap.get(curveToAddName);
                                                    /* Retrieve data for the curve (ie. iv and dv names) */
                                                    jsonLoadedCurve = new LoadCurveDataTask(wellboreCurveToAdd, wellId).execute().get();
                                                    curveJsonParser.parseIvDvNames(wellboreCurveToAdd, jsonLoadedCurve, wellboreCurveToAdd.getId(),
                                                            curveToAddName, wellboreCurveToAdd.getCurveType());
                                                    addedCurve = dbCommunicator.createWellboreCurve(wellboreCurveToAdd.getId(),
                                                            wellboreCurveToAdd.getName(), wellboreCurveToAdd.getIvName(),
                                                            wellboreCurveToAdd.getDvName(), wellboreCurveToAdd.getIvUnit(),
                                                            wellboreCurveToAdd.getDvUnit(), wellId, wellboreCurveToAdd.getCurveType(),
                                                            wellboreCurveToAdd.getWellboreId(), wellboreCurveToAdd.getWellboreType());
                                                }
                                                else if (ivSelection.equals("True Vertical Depth")) {
                                                    WellboreCurve wellboreCurveToAdd = wellboreVertDepthCurveMap.get(curveToAddName);
                                                    /* Retrieve data for the curve (ie. iv and dv names) */
                                                    jsonLoadedCurve = new LoadCurveDataTask(wellboreCurveToAdd, wellId).execute().get();
                                                    curveJsonParser.parseIvDvNames(wellboreCurveToAdd, jsonLoadedCurve, wellboreCurveToAdd.getId(),
                                                            curveToAddName, wellboreCurveToAdd.getCurveType());
                                                    addedCurve = dbCommunicator.createWellboreCurve(wellboreCurveToAdd.getId(),
                                                            wellboreCurveToAdd.getName(), wellboreCurveToAdd.getIvName(),
                                                            wellboreCurveToAdd.getDvName(), wellboreCurveToAdd.getIvUnit(),
                                                            wellboreCurveToAdd.getDvUnit(), wellId, wellboreCurveToAdd.getCurveType(),
                                                            wellboreCurveToAdd.getWellboreId(), wellboreCurveToAdd.getWellboreType());
                                                }
                                                else {
                                                    WellboreCurve wellboreCurveToAdd = wellboreVertSectionCurveMap.get(curveToAddName);
                                                    /* Retrieve data for the curve (ie. iv and dv names) */
                                                    jsonLoadedCurve = new LoadCurveDataTask(wellboreCurveToAdd, wellId).execute().get();
                                                    curveJsonParser.parseIvDvNames(wellboreCurveToAdd, jsonLoadedCurve, wellboreCurveToAdd.getId(),
                                                            curveToAddName, wellboreCurveToAdd.getCurveType());
                                                    addedCurve = dbCommunicator.createWellboreCurve(wellboreCurveToAdd.getId(),
                                                            wellboreCurveToAdd.getName(), wellboreCurveToAdd.getIvName(),
                                                            wellboreCurveToAdd.getDvName(), wellboreCurveToAdd.getIvUnit(),
                                                            wellboreCurveToAdd.getDvUnit(), wellId, wellboreCurveToAdd.getCurveType(),
                                                            wellboreCurveToAdd.getWellboreId(), wellboreCurveToAdd.getWellboreType());
                                                }
                                                if(addedCurve != null) {
                                                    curvesToAddList.add(addedCurve);
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    plotToAdd = dbCommunicator.createPlot(inputTitle.getText().toString(),
                                            wellId);

                                    for (int i = 0; i < curvesToAddList.size(); i++) {
                                        dbCommunicator.addCurveToPlot(plotToAdd, curvesToAddList.get(i));
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
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        parseJsonCurveList(result);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void parseJsonCurveList(String jsonString) {
        curveList = curvesForWellJsonParser.parse(jsonString);
        int curveListSize = curveList.size();
        String curveName;
        String curveType;
        for (int i = 0; i < curveListSize; i++) {
            Curve curveForMap = curveList.get(i);
            curveName = curveForMap.getName();
            curveType = curveForMap.getCurveType();

            if(curveType.equals("time_curve")) {
                timeCurveMap.put(curveName, curveForMap);
                curveStringList.add(curveName);
            }
            else {
                WellboreCurve wellboreCurveForMap = (WellboreCurve) curveList.get(i);
                /* Assign the approprate name mappings for each wellbore type */
                if(wellboreCurveForMap.getWellboreType().equals("Measured Depth")) {
                    wellboreMeasuredDepthCurveMap.put(curveName, wellboreCurveForMap);
                }
                else if (wellboreCurveForMap.getWellboreType().equals("True Vertical Depth")) {
                    wellboreVertDepthCurveMap.put(curveName, wellboreCurveForMap);
                }
                else {
                    wellboreVertSectionCurveMap.put(curveName, wellboreCurveForMap);
                }
            }

        }
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, curveStringList);
        curveListView.setAdapter(listAdapter);
        curveListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        curveListView.setTextFilterEnabled(true);
    }

    public ListView getListView() {
        return curveListView;
    }

    public String getWellId() {
        return this.wellId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* TODO: add checks for when menu items are added in later dev */
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_plot, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.curve_search)
                .getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Collapseable widget

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                listAdapter.getFilter().filter(newText);
                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                listAdapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return super.onCreateOptionsMenu(menu);
    }
}
