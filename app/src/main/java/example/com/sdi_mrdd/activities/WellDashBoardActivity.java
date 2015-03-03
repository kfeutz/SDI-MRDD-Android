package example.com.sdi_mrdd.activities;

import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import example.com.sdi_mrdd.database.DatabaseCommunicator;
import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.adapters.TabsDashBoardAdapter;
import example.com.sdi_mrdd.dataitems.Well;

/**
 * The object representing our view for the Well Dashboard. Contains a well title to display
 * in the action bar; a reference to the well that it belongs to; a tab adapter and tab labels;
 * a ViewPager for rendering the Fragments; and a DatabaseCommunicator to make SQLite calls
 */
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

        /* Loop while the database is busy before using it */
        while(dbCommunicator.isDbLockedByCurrentThread() || dbCommunicator.isDbLockedByOtherThreads()) {
            //db is locked, keep looping
        }

        /* Set up our tabs adapter and add to ViewPage */
        mAdapter = new TabsDashBoardAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.dashboardPager);
        viewPager.setAdapter(mAdapter);

        /* Set up our action bar to display the tabs */
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /* Add each tab to the bar */
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /* Enables screen swiping to switch from tab to tab */
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


        /* Get Title  from intent extras and set to page title */
        displayWell = getIntent().getParcelableExtra("well");
        wellTitle = displayWell.getName();
        setTitle(wellTitle);
    }

    /**
     * Closes the database connection when this activity is ended.
     */
    @Override
    protected void onStop() {
        super.onStop();
        dbCommunicator.close();
    }

    /**
     * Retrieves the well title belonging to this WellDashBoardActivity
     *
     * @return String   The well title belonging to this WellDashBoardActivity
     */
    public String getWellTitle() {
        return displayWell.getName();
    }

    /**
     * Retrieves the well id belonging to this WellDashBoardActivity
     *
     * @return String   The well id belonging to this WellDashBoardActivity
     */
    public String getWellId() {
        return displayWell.getWellId();
    }

    /**
     * Retrieves the DatabaseCommunicator belonging to this WellDashBoardActivity
     *
     * @return DatabaseCommunicator   The DatabaseCommunicator title belonging
     *                                to this WellDashBoardActivity
     */
    public DatabaseCommunicator getDbCommunicator() {
        return dbCommunicator;
    }

    /**
     *  Changes tab selection based on which tab is selected
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
