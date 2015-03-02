package example.com.sdi_mrdd;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class WellDashBoardActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    /* Title of the well */
    private String wellTitle;
    /* The well that the dashboard is displaying */
    private Well displayWell;
    /* Titles for the tabs */
    private String[] tabs = {"Dashboard", "Plots"};
    /* Custom adapter for the tabs */
    private TabsDashBoardAdapter mAdapter;
    /* view pager for the custom tabs */
    private ViewPager viewPager;
    /* Communicator to our database */
    private DatabaseCommunicator dbCommunicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_dash_board);

        /* Initialize Database communicator */
        dbCommunicator = new DatabaseCommunicator(this.getApplication());
        dbCommunicator.open();


        mAdapter = new TabsDashBoardAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.dashboardPager);

        final ActionBar actionBar = getSupportActionBar();
        viewPager.setAdapter(mAdapter);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        /**
         * Get Title  from intent extras
         * Field will be null if value does not exist depending on which
         * intent started or resumed this activity.
         */
        displayWell = getIntent().getParcelableExtra("well");
        wellTitle = displayWell.getName();
        setTitle(wellTitle);
    }

    public String getWellTitle() {
        return displayWell.getName();
    }

    public String getWellId() {
        return displayWell.getWellId();
    }

    public DatabaseCommunicator getDbCommunicator() {
        return dbCommunicator;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
