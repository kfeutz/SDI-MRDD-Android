package example.com.sdi_mrdd;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_plot, menu);
        return true;
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
}
