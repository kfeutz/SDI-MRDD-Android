package example.com.sdi_mrdd;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This class displays a graphical representation of a plot. Each ViewPlotActivity
 * has a Plot and a String plotName
 */
public class ViewPlotActivity extends ActionBarActivity {

    /* Hold the plot object to display */
    private Plot plotToDisplay;

    /* Name of the plot used for title of page */
    private String plotName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plot);

        /**
         * Get Plot object  from intent extras
         * This field is passed from the onClickListener in the
         * ListView in WellPlotsFragment
         */
        plotToDisplay = getIntent().getParcelableExtra("plot");
        plotName = plotToDisplay.getName();
        setTitle(plotName);
    }

    /**
     * Specifies which menu to render for ViewPlotActivity
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_plot, menu);
        return true;
    }

    /**
     * Handles action bar clicks
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* Example for seeing which item was selected */
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
