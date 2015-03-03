package example.com.sdi_mrdd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.activities.AddCurveActivity;
import example.com.sdi_mrdd.activities.WellDashBoardActivity;
import example.com.sdi_mrdd.database.DatabaseCommunicator;
import example.com.sdi_mrdd.dataitems.Curve;

/**
 * This class represents a Fragment to show when the Dashboard tab is selected
 * on the Well Dashboard page. Each Fragment has a list of curves to display, a list
 * of strings representing the curve titles, a list of curves that were added from the
 * Add Curve screen, and a database communicator to read stored curves from our SQLite database.
 *
 * Created by Kevin on 2/28/2015.
 */
public class WellDashBoardFragment extends Fragment {
    /* Result tag used to check the result of adding a curve */
    private static final int RESULT_OK = -1;

    /* Contains the names of curves added from the Add Curve screen */
    private ArrayList<String> curvesToAdd;

    /* Contains a String collection of the curve names to display */
    private ArrayList<String> curvesToDisplay;

    /* Array adapter to display the curves on the listview */
    private ArrayAdapter<String> addedCurves;

    /* The list of Curves belonging to this well dashboard */
    private List<Curve> curveList;

    /* The list view widget displayed on the dashboard fragment */
    private ListView curvesListView;

    /* Database communicator to talk to our SQLite database */
    private DatabaseCommunicator dbCommunicator;

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

        /* Initializes the array adapter to display on the page
         * TODO: Change this to be ArrayAdapter<Curve> so we display Curves instead of strings */
        addedCurves = new ArrayAdapter<String>(rootView.getContext(),
                android.R.layout.simple_list_item_1, curvesToDisplay);
        curvesListView = (ListView) rootView.findViewById(R.id.well_dashboard_list);
        curvesListView.setAdapter(addedCurves);

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
        /**
         * Get ArrayList from intent extras. This ArrayList contains the string
         * list representing the selected curves to add to the dashboard
         * Field will be null if value does not exist depending on which
         * intent started or resumed this activity.
         */
        if(requestCode==1  && resultCode==RESULT_OK) {
            curvesToAdd = data.getStringArrayListExtra("curveList");
            if (curvesToAdd != null) {
                /**
                 * Curve data has been passed to this activity.
                 * Display curve data here
                 */
                addedCurves.addAll(curvesToAdd);
                addedCurves.notifyDataSetChanged();
            }
        }
    }
}
