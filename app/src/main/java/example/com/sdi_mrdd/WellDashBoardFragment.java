package example.com.sdi_mrdd;

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

import example.com.sdi_mrdd.activities.AddCurveActivity;

/**
 * Created by Kevin on 2/28/2015.
 */
public class WellDashBoardFragment extends Fragment {
    private static final int RESULT_OK = -1;
    private String title;
    private String wellTitle;
    private ArrayList<String> curvesToAdd;
    private ArrayList<String> curvesToDisplay;
    private ArrayAdapter<String> addedCurves;
    private List<Curve> curveList;
    private ListView curvesList;
    private DatabaseCommunicator dbCommunicator;/* Initialize the db communicator */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_well_dashboard, container, false);

        setHasOptionsMenu(true);

        dbCommunicator = ((WellDashBoardActivity) getActivity()).getDbCommunicator();

        curveList = dbCommunicator.getCurvesForDashboard(
                ((WellDashBoardActivity) getActivity()).getWellId());
        /* Loop through each curve and add to curvesToDisplay */
        curvesToDisplay = new ArrayList<String>();
        for (int i = 0; i < curveList.size(); i++) {
            curvesToDisplay.add(curveList.get(i).getName());
        }
        addedCurves = new ArrayAdapter<String>(rootView.getContext(),
                android.R.layout.simple_list_item_1, curvesToDisplay);
        curvesList = (ListView) rootView.findViewById(R.id.well_dashboard_list);
        curvesList.setAdapter(addedCurves);

        return rootView;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_well_dash_board, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


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
