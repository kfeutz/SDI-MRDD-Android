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
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import example.com.sdi_mrdd.activities.CreatePlotActivity;

/**
 * Created by Kevin on 2/28/2015.
 */
public class WellPlotsFragment extends Fragment {
    private static final int RESULT_OK = -1;
    /* List Adapter to display Plots */
    private PlotAdapter plotAdapter;
    /* List view to display on the Fragment */
    private ListView plotListView;
    /* List of plots */
    private List<Plot> plots;
    /* Communicator to our database */
    private DatabaseCommunicator dbCommunicator;/* Initialize the db communicator */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_well_plots, container, false);

        setHasOptionsMenu(true);

        dbCommunicator = ((WellDashBoardActivity) getActivity()).getDbCommunicator();

        plots = dbCommunicator.getPlotsForWell(((WellDashBoardActivity) getActivity()).getWellId());

        plotAdapter = new PlotAdapter(rootView.getContext(), R.layout.listview_plot_row, plots);
        plotListView = (ListView) rootView.findViewById(R.id.well_plots_list);
        plotListView.setAdapter(plotAdapter);

        /**
         * When the user selects a plot from the list view, start
         * the
         */
        plotListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                Plot value = (Plot)adapter.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), ViewPlotActivity.class);
                intent.putExtra("plot", value);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_well_plots, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        plots = dbCommunicator.getPlotsForWell(((WellDashBoardActivity) getActivity()).getWellId());
        if(requestCode==1  && resultCode==RESULT_OK) {
            plotAdapter.clear();
            plotAdapter.addAll(plots);
            plotAdapter.notifyDataSetChanged();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.create_plot:
                Intent intent = new Intent(getActivity(), CreatePlotActivity.class);
                intent.putExtra("wellId", ((WellDashBoardActivity) getActivity()).getWellId());
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
