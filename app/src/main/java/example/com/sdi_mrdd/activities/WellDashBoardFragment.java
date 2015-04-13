package example.com.sdi_mrdd.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.List;
import java.util.Scanner;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.activities.AddCurveActivity;
import example.com.sdi_mrdd.activities.WellDashBoardActivity;
import example.com.sdi_mrdd.adapters.CurveAdapter;
import example.com.sdi_mrdd.adapters.WellAdapter;
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
public class WellDashBoardFragment extends Fragment {
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
    private GridView curvesListView;

    /* Database communicator to talk to our SQLite database */
    private DatabaseCommunicator dbCommunicator;

    private CurveValueParser curveValueParser = CurveValueParser.getInstance();

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

        listAdapter = new CurveAdapter(rootView.getContext(), R.layout.well_dash_board_card);
        listAdapter.addAll(curveList);

        for (int i = 0; i < curveList.size(); i++) {
            new UpdateCurve(curveList.get(i)).execute();
        }

        curvesListView = (GridView) rootView.findViewById(R.id.well_dashboard_list);
        curvesListView.setAdapter(listAdapter);

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
            case R.id.refresh:
                for (int i = 0; i < curveList.size(); i++) {
                    new UpdateCurve(curveList.get(i)).execute();
                }
                return true;
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
            for (int i = 0; i < curveList.size(); i++) {
                new UpdateCurve(curveList.get(i)).execute();
            }
            if (requestCode == 1 && resultCode == RESULT_OK) {
                listAdapter.clear();
                listAdapter.addAll(curveList);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Asynchronous Task class that makes a REST GET request to the backend service.
     * Currently, we are testing on local host.
     *
     * Use 'http://10.0.3.2:5000/' for Genymotion emulators
     * Use 'http://10.0.2.2:5000/' for Android Studio emulators
     */
    private class UpdateCurve extends AsyncTask<String, Void, String> {
        HttpClient client = new DefaultHttpClient();
        String server = "http://54.67.103.185/getCurveFromCurveIdPresent";
        HttpGet request;
        String curveId;
        String jsonString = "";
        Curve theCurve;

        private UpdateCurve (Curve curve) {
            this.theCurve = curve;
            this.curveId = curve.getId();
            this.server += ("?curve=" + this.curveId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.request = new HttpGet(server);
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
            String ivValues = curveValueParser.parseIvValues(result);
            List<String> ivValueList = Arrays.asList(ivValues.split(","));
            /* Right now, just pull the most recent data */
            theCurve.setIvValue(ivValueList.get(ivValueList.size() - 1));
            listAdapter.notifyDataSetChanged();
            String dvValues = curveValueParser.parseDvValues(result);
            List<String> dvValueList = Arrays.asList(dvValues.split(","));
            theCurve.setDvValue(dvValueList.get(dvValueList.size() - 1));
            listAdapter.notifyDataSetChanged();
        }
    }
}
