package example.com.sdi_mrdd;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class WellDashBoardActivity extends ActionBarActivity {

    private String title;
    private String wellTitle;
    private ArrayList<String> curvesToAdd;
    private ArrayAdapter<String> addedCurves;
    private ListView curvesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_dash_board);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Get Title  from intent extras
         * Field will be null if value does not exist depending on which
         * intent started or resumed this activity.
         */
        wellTitle = getIntent().getStringExtra("title");
        title = wellTitle + " Dashboard";
        if (title != null) {
            setTitle(title);
        }

        curvesList = (ListView) findViewById(R.id.well_dashboard_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_well_dash_board, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_curve:
                Intent intent = new Intent(WellDashBoardActivity.this, AddCurveActivity.class);
                intent.putExtra("wellName", WellDashBoardActivity.this.getWellTitle());
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getWellTitle() {
        return wellTitle;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Get ArrayList from intent extras. This ArrayList contains the string
         * list representing the selected curves to add to the dashboard
         * Field will be null if value does not exist depending on which
         * intent started or resumed this activity.
         */
        if(requestCode==1  && resultCode==RESULT_OK) {
            curvesToAdd = data.getExtras().getStringArrayList("curveList");
            if (curvesToAdd != null) {
                /**
                 * Curve data has been passed to this activity.
                 * Display curve data here
                 */
                Toast.makeText(WellDashBoardActivity.this, "Number of curves to add: " + curvesToAdd.size(), Toast.LENGTH_LONG).show();
                addedCurves = new ArrayAdapter<String>(WellDashBoardActivity.this, android.R.layout.simple_list_item_1, curvesToAdd);
                curvesList.setAdapter(addedCurves);
            }
        }
    }
}
