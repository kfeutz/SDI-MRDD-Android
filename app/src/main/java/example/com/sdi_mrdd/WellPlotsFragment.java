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
   * This class represents a Fragment to show when the Plot tab is selected on the Well dashboard
   * page. Each Fragment has a plot adapter to render each plot, a ListView to reference the
   * widget where the data will be rendered, a list of plots to display, and a database communicator
   * to retrieve plots for a well.
   *
   * Created by Kevin on 2/28/2015.
   */
  public class WellPlotsFragment extends Fragment {
      /* Result tag used to check the result of creating a plot */
      private static final int RESULT_OK = -1;

      /* List Adapter to display Plots */
      private PlotAdapter plotAdapter;

      /* List view to display on the Fragment */
      private ListView plotListView;

      /* List of plots */
      private List<Plot> plots;

      /* Communicator to our database */
      private DatabaseCommunicator dbCommunicator;

     /**
      * Creates the view for the Plot fragment. Initializes the database communicator and
      * retrieves the plots for this Well from the database. Adds the plots to the adapter
      * and sets the ListView adapter to the plot adapter
      *
      * @param inflater
      * @param container
      * @param savedInstanceState
      */
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
          plotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapter, View v, int position,
                                      long arg3) {
                  Plot value = (Plot) adapter.getItemAtPosition(position);

                  Intent intent = new Intent(getActivity(), ViewPlotActivity.class);
                  intent.putExtra("plot", value);
                  startActivity(intent);
              }
          });

          return rootView;
      }

     /**
      * Renders the menu layout in menu_well_plots.xml for this fragment
      */
      public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
          inflater.inflate(R.menu.menu_well_plots, menu);
          super.onCreateOptionsMenu(menu, inflater);
      }

    /**
     * Runs after the user creates a plot. CreatePlotActivity sets up the result code. Once
     * CreatePlotActivity has ended, the SQLite database has a reference to the newly created
     * plot. The list of plots is cleared and the list is retrieved from the database to
     * represent the updated list.
     *
     * @param requestCode   Code sent by this activity when it started CreatePlotActivity
     * @param resultCode    Code received from result activity
     * @param data          The intent which responded to this fragment's request
     *                      (CreatePlotActivity)
     */
      @Override
      public void onActivityResult(int requestCode, int resultCode,
                                   Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          /* Open the database communicator if it is closed */
          if (!dbCommunicator.isOpen()) {
              dbCommunicator.open();
          }
          /**
           * Get ArrayList from intent extras. This ArrayList contains the string
           * list representing the selected curves to add to the dashboard
           * Field will be null if value does not exist depending on which
           * intent started or resumed this activity.
           */
          plots = dbCommunicator.getPlotsForWell(((WellDashBoardActivity) getActivity()).getWellId());
          if (requestCode == 1 && resultCode == RESULT_OK) {
              plotAdapter.clear();
              plotAdapter.addAll(plots);
              plotAdapter.notifyDataSetChanged();
          }
      }

     /**
      * Sets the item listener for Create Plot button. Starts the create plot activity and waits
      * for its result.
      *
      * @param item  The menu item selected by the user
      * @return
      */
      public boolean onOptionsItemSelected(MenuItem item) {
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