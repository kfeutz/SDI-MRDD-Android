package example.com.sdi_mrdd.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.ToggleButton;

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import example.com.sdi_mrdd.asynctasks.AsyncTaskCompleteListener;
import example.com.sdi_mrdd.asynctasks.LoadCurveDataTask;
import example.com.sdi_mrdd.asynctasks.LoadCurvesForWellTask;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveJsonParser;
import example.com.sdi_mrdd.dataitems.CurvesForWellJsonParser;
import example.com.sdi_mrdd.database.DatabaseCommunicator;
import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.dataitems.Well;


public class AddCurveActivity extends ActionBarActivity implements AsyncTaskCompleteListener<String> {

    private ArrayAdapter<String> listAdapter;

    // Logcat tag
    private static final String TAG = "AddCurveView";
    private ListView curveListView;
    private Button btnAddCurves;
    /* Reference to Radio group */
    private RadioGroup ivRadioGroup;

    /* Reference to depth radio button */
    private RadioButton depthRadio;

    /* Reference to time radio button */
    private RadioButton timeRadio;

    private String wellId;
    private ArrayList<String> curveData;
    private DatabaseCommunicator dbCommunicator;
    /* Contains reference to the well the curves will be added to */
    private Well well;
    /* Object that parses json strings into curve objects */
    private CurvesForWellJsonParser curvesForWellJsonParser = CurvesForWellJsonParser.getInstance();
    private CurveJsonParser curveJsonParser = CurveJsonParser.getInstance();

    String jsonCurves;
    Map<String, String> curveMap = new HashMap<>();
    ArrayList<String> curveStringList = new ArrayList<String>();
    ArrayList<String> selectedCurveList = new ArrayList<String>();
    ArrayList<Curve> curveList = new ArrayList<>();

    /** progress dialog to show user that the curves are loading. */
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String curveToAddId = null;
        setContentView(R.layout.activity_add_curve);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Initialize the db communicator */
        dbCommunicator = new DatabaseCommunicator(getApplicationContext());
        dbCommunicator.open();

        wellId = getIntent().getExtras().getString("wellId");

        curveListView = (ListView) findViewById(R.id.add_curve_view);

        ivRadioGroup = (RadioGroup) findViewById(R.id.iv_radio_group);
        ivRadioGroup.check(R.id.radio_depth);
        depthRadio = (RadioButton) findViewById(R.id.radio_depth);
        timeRadio = (RadioButton) findViewById(R.id.radio_time);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Retrieving curves");
        dialog.show();
        new LoadCurvesForWellTask(this, this.wellId).execute();

        btnAddCurves =  (Button) findViewById(R.id.btn_add_curves);
        btnAddCurves.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray checked = AddCurveActivity.this.getListView().getCheckedItemPositions();
                if (checked.size() > 0) { // Check there is at least one item checked
                    //Ask the user if they want to add the curves
                    new AlertDialog.Builder(AddCurveActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.confirm_curve_add_title)
                            .setMessage(R.string.confirm_curve_msg)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String curveToAddId = null;
                                    String curveToAddName;
                                    String jsonLoadedCurve = null;
                                    String curveType = null;
                                    Curve curveToAdd;
                                    SparseBooleanArray checked = AddCurveActivity.this.getListView().getCheckedItemPositions();
                                    int size = checked.size(); // number of name-value pairs in the array
                                    for (int i = 0; i < size; i++) {
                                        int key = checked.keyAt(i);
                                        boolean value = checked.get(key);
                                        if (value && curveStringList != null) {
                                            curveToAddName = listAdapter.getItem(key);
                                            try {
                                                for (int j = 0; j < curveList.size(); j++) {
                                                    if(curveList.get(j).getName().equals(curveToAddName)) {
                                                        if(timeRadio.isChecked()){
                                                            /* Time curve selected, skip curve if its wellbore */
                                                            String curveTypeTemp = curveList.get(j).getCurveType();
                                                            if(curveList.get(j).getCurveType().equals("time_curve")) {
                                                                curveType = curveList.get(j).getCurveType();
                                                                curveToAddId = curveList.get(j).getId();
                                                            }
                                                        }
                                                        else {
                                                            /* Wellbore curve selected, skip curve if its time */
                                                            if(curveList.get(j).getCurveType().equals("wellbore_curve")) {
                                                                curveType = curveList.get(j).getCurveType();
                                                                curveToAddId = curveList.get(j).getId();
                                                            }
                                                        }

                                                    }
                                                }
                                                jsonLoadedCurve = new LoadCurveDataTask(curveToAddId,
                                                        AddCurveActivity.this.getWellId(), curveType).execute().get();
                                                curveToAdd = curveJsonParser.parse(jsonLoadedCurve, curveToAddId, curveToAddName, curveType);

                                                Curve addedCurve = dbCommunicator.createCurve(curveToAdd.getId(),
                                                        curveToAdd.getName(), curveToAdd.getIvName(),
                                                        curveToAdd.getDvName(), curveToAdd.getIvUnit(),
                                                        curveToAdd.getDvUnit(), wellId, curveType);
                                                dbCommunicator.addCurveToDashboard(addedCurve, wellId);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    Intent intent = new Intent(AddCurveActivity.this, WellDashBoardActivity.class);
                                /*
                                  *   Adds the ArrayList of selected strings to the intent which will be passed to
                                  *   the well dashboard. The list of strings represent curve names to be added to
                                  *   the dashboard
                                 */
                                    intent.putStringArrayListExtra("curveList", selectedCurveList);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    setResult(RESULT_OK, intent);

                                    AddCurveActivity.this.finish();
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
        for (int i = 0; i < curveList.size(); i++) {
            curveStringList.add(curveList.get(i).getName());
        }
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, curveStringList);
        curveListView.setAdapter(listAdapter);
        curveListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        curveListView.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_curve, menu);


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.add_curve_search)
                .getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), AddCurveActivity.class)));
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

    public ListView getListView() {
        return curveListView;
    }

    public String getWellId() {
        return this.wellId;
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

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // Enable vibrate
        } else {
            // Disable vibrate
        }
    }
}
