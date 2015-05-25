package example.com.sdi_mrdd.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.activities.AddCurveActivity;
import example.com.sdi_mrdd.activities.WellDashBoardActivity;
import example.com.sdi_mrdd.adapters.CurveAdapter;
import example.com.sdi_mrdd.adapters.WellAdapter;
import example.com.sdi_mrdd.asynctasks.AsyncTaskCompleteListener;
import example.com.sdi_mrdd.asynctasks.LatestCurveValuesTask;
import example.com.sdi_mrdd.database.DatabaseCommunicator;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.CurveJsonParser;
import example.com.sdi_mrdd.dataitems.CurveValueParser;
import example.com.sdi_mrdd.dataitems.Well;

/**
 * This class represents a Fragment to show when the Dashboard tab is selected
 * on the Well Dashboard page. Each Fragment has a list of curves to display, a list
 * of strings representing the curve titles, a list of curves that were added from the
 * Add Curve screen, and a database communicator to read stored curves from our SQLite database.
 *
 * Created by Kevin on 2/28/2015.
 */
public class WellDashBoardFragment extends Fragment implements AsyncTaskCompleteListener<String> {
    /* Result tag used to check the result of adding a curve */
    private static final int RESULT_OK = -1;

    /* Adapter to display Wells on the list view */
    private CurveAdapter listAdapter;

    /* Contains the names of curves added from the Add Curve screen */
    private ArrayList<String> curvesToAdd;

    /* Contains a String collection of the curve names to display */
    private ArrayList<String> curvesToDisplay;

    /* Array adapter to display the curves on the listview */
    private ArrayAdapter<String> addedCurves;

    /* The list of Curves belonging to this well dashboard */
    private List<Curve> curveList;

    /* The list view widget displayed on the dashboard fragment */
    private RecyclerView curvesListView;

    /* Database communicator to talk to our SQLite database */
    DatabaseCommunicator dbCommunicator;

    private CurveValueParser curveValueParser = CurveValueParser.getInstance();

    private boolean timerStarted;

    TimerTask doAsynchronousTask;

    private ArrayList<LatestCurveValuesTask> asyncTasks = new ArrayList<>();

    /**
     * Creates the Well Dashboard fragment view and sets up the fragment view's
     * components. Layout is set to fragment_well_dashboard.xml.
     * The database communicator is initialized and the curve list is retrieved
     * from the database using the communicator. The adapter is set and added to the ListView.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View     The View to display for this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_well_dashboard, container, false);

        setHasOptionsMenu(true);

        timerStarted = false;

        /* Initialize the database communicator */
        dbCommunicator = ((WellDashBoardActivity) getActivity()).getDbCommunicator();

        /* Retrieve this dashboard's curves from the database */
        curveList = dbCommunicator.getCurvesForDashboard(
                ((WellDashBoardActivity) getActivity()).getWellId());

        /* Loop through each curve and add to curvesToDisplay */
        curvesToDisplay = new ArrayList<String>();
        for (int i = 0; i < curveList.size(); i++) {
            curvesToDisplay.add(curveList.get(i).getName());
        }
        initializeAsyncTimer();

        curvesListView = (RecyclerView) rootView.findViewById(R.id.well_dashboard_list);
        curvesListView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        curvesListView.setItemAnimator(new DefaultItemAnimator());

        /* Initializes the array adapter to display on the page */
        listAdapter = new CurveAdapter(curveList, R.layout.well_dash_board_card, this.getActivity());
        curvesListView.setAdapter(listAdapter);
        /* Register each card to prompt context menu on long click */
        registerForContextMenu(curvesListView);

        return rootView;
    }

    /**
     * Sets this Fragment's menu to the one specified in menu_well_dash_board.xml
     *
     * @param menu
     * @param inflater
     */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_well_dash_board, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Sets the handlers for each item on the menu. If add curve button is clicked, start
     * the add curve intent.
     *
     * @param item      The menu item selected by the user
     * @return boolean  True for successful click.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_curve:
                Intent intent = new Intent(getActivity(), AddCurveActivity.class);
                intent.putExtra("wellId", ((WellDashBoardActivity) getActivity()).getWellId());
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Runs after the user adds a curve. AddCurveActivity sets up the result code
     * and currently passes back an arraylist of curve names to add to the fragment's
     * ArrayAdapter.
     *
     * TODO: Change this to pass back Curves instead of Strings
     * @param requestCode   Code sent by this activity when it started AddCurveActivity
     * @param resultCode    Code received from result activity
     * @param data          The intent which responded to our request (AddCurveActivity)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1  && resultCode==RESULT_OK) {
            /* Open the database communicator if it is closed */
            if (!dbCommunicator.isOpen()) {
                dbCommunicator.open();
            }
            /**
             * Get ArrayList of curves for this dashboard from the SQLite database.
             * This ArrayList contains the string
             * list representing the selected curves to add to the dashboard
             * Field will be null if value does not exist depending on which
             * intent started or resumed this activity.
             */
            curveList = dbCommunicator.getCurvesForDashboard(((WellDashBoardActivity) getActivity()).getWellId());
            initializeAsyncTimer();

            if (requestCode == 1 && resultCode == RESULT_OK) {
                listAdapter.clear();
                listAdapter.addAll(curveList);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info
                = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = listAdapter.getPosition();
        switch (item.getItemId()) {
            case R.id.context_delete:
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.confirm_curve_add_title)
                        .setMessage(R.string.confirm_curve_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete from SQLite database
                                dialog.dismiss();
                                dbCommunicator.deleteCurveFromDashboard(
                                        listAdapter.getCurveFromPosition(position).getId(),
                                        ((WellDashBoardActivity) getActivity()).getWellId());
                                listAdapter.remove(position);
                            }

                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        })
                        .show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timerStarted = false;
        doAsynchronousTask.cancel();
        for(int i = 0; i < asyncTasks.size(); i++) {
            if(asyncTasks.get(i).getStatus().equals(AsyncTask.Status.RUNNING))
            {
                asyncTasks.get(i).cancel(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeAsyncTimer();
    }

    public void initializeAsyncTimer() {
        if(!timerStarted) {
            timerStarted = true;
            final Handler handler = new Handler();
            Timer timer = new Timer();
            doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                handler.post(new Runnable() {
                    public void run() {
                    try {
                        for (int i = 0; i < curveList.size(); i++) {
                            /*new UpdateCurve(curveList.get(i)).execute();*/
                            LatestCurveValuesTask curveTask
                                    = new LatestCurveValuesTask(WellDashBoardFragment.this, curveList.get(i),
                                    ((WellDashBoardActivity) getActivity()).getWellId(),
                                    dbCommunicator);
                            asyncTasks.add(curveTask);
                            curveTask.execute();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                    }
                    }
                });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 30000); //execute in every 50000 ms
        }
    }

    @Override
    public void onTaskComplete(String result) {
        listAdapter.notifyDataSetChanged();
    }
}
