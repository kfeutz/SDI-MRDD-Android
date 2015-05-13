package example.com.sdi_mrdd.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    Map<String, String> curveMap = new HashMap<>();

    String jsonCurves;

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
        ivRadioGroup = (RadioGroup) findViewById(R.id.iv_radio_group);
        ivRadioGroup.check(R.id.radio_depth);
        depthRadio = (RadioButton) findViewById(R.id.radio_depth);
        timeRadio = (RadioButton) findViewById(R.id.radio_time);

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
                                    Curve curveToAdd;
                                    String loadedCurve;
                                    int size = checked.size(); // number of name-value pairs in the array
                                    for (int i = 0; i < size; i++) {
                                        int key = checked.keyAt(i);
                                        boolean value = checked.get(key);
                                        if (value && curveStringList != null) {
                                            curveToAddName = curveStringList.get(key);
                                            try {
                                                for (int j = 0; j < curveList.size(); j++) {
                                                    if(curveList.get(j).getName().equals(curveToAddName)) {
                                                        if(timeRadio.isChecked())
                                                        {
                                                            /* Time curve selected, skip curve if its wellbore */
                                                            if(curveList.get(j).getCurveType().equals("time_curve")) {
                                                                curveType = curveList.get(j).getCurveType();
                                                                curveToAddId = curveList.get(j).getId();
                                                            }
                                                        }
                                                        else
                                                        {
                                                            /* Wellbore curve selected, skip curve if its time */
                                                            if(curveList.get(j).getCurveType().equals("wellbore_curve")) {
                                                                curveType = curveList.get(j).getCurveType();
                                                                curveToAddId = curveList.get(j).getId();
                                                            }
                                                        }

                                                    }
                                                }
                                                loadedCurve = new LoadCurveDataTask(curveToAddId,
                                                        CreatePlotActivity.this.getWellId(), curveType).execute().get();
                                                curveToAdd = curveJsonParser.parse(loadedCurve, curveToAddId, curveToAddName, curveType);

                                                Curve addedCurve = dbCommunicator.createCurve(curveToAdd.getId(),
                                                        curveToAdd.getName(), curveToAdd.getIvName(),
                                                        curveToAdd.getDvName(), curveToAdd.getIvUnit(),
                                                        curveToAdd.getDvUnit(), wellId, curveToAdd.getCurveType());
                                                curvesToAddList.add(addedCurve);
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_depth:
                if (checked)

                    break;
            case R.id.radio_time:
                if (checked)

                    break;
        }
    }

    public void parseJsonCurveList(String jsonString) {
        curveList = curvesForWellJsonParser.parse(jsonString);
        for (int i = 0; i < curveList.size(); i++) {
            curveStringList.add(curveList.get(i).getName());
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
        return super.onCreateOptionsMenu(menu);
    }
}
