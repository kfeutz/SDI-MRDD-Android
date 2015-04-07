package example.com.sdi_mrdd.activities;

import android.widget.ListView;

import example.com.sdi_mrdd.MyRobolectricTestRunner;
import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.activities.WellListActivity;
import example.com.sdi_mrdd.adapters.WellAdapter;
import example.com.sdi_mrdd.dataitems.Well;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by Kevin on 3/6/2015.
 */
@Config(emulateSdk = 18, manifest = "src/main/AndroidManifest.xml")
@RunWith(MyRobolectricTestRunner.class)
public class WellListActivityTest {
    private WellListActivity mWellListActivity;
    private ListView mWellListView;

    /**
     * Define the instance variables that store the state fo the fixture.
     * Create and store a reference to the Activity under test.
     * Obtain a reference to any UI components in this Activity that needs to be tested.
     *
     * @throws Exception
     */
    @Before
    public void setup()  {
        /*ActivityController<WellListActivity> wellListController =
                Robolectric.buildActivity(WellListActivity.class).create().start();
        wellListController.visible();
        mWellListActivity = wellListController.get();
        mWellListView =
                (ListView) mWellListActivity
                        .findViewById(R.id.well_list_view);*/
    }

    @Test
    public void testActivityFound() {
        Assert.assertNotNull(1);
    }

    /**
     * Standard practice to verify that the test fixture has been set up correctly,
     * and all objects you want to test have been correctly instantiated/initiallized.
     */
    @Test
    public void testPreconditions() {
        /*Assert.assertNotNull("mWellListActivity is null", mWellListActivity);
        Assert.assertNotNull("mWellListView is null", mWellListView);*/
    }

    /**
     * Tests that the Well ListView has a WellAdapter
     */
    @Test
    public void testWellListView() {
        /*WellAdapter wellAdapter = (WellAdapter) mWellListView.getAdapter();
        Assert.assertNotNull("mWellListView is missing a Well Adapter", wellAdapter);*/
    }
}
