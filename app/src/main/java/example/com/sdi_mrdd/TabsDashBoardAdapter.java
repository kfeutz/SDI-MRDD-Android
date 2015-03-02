package example.com.sdi_mrdd;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Used by WellDashboard to render the tabs. This class defines which
 * fragments to start based on which tab is selected.
 *
 * Created by Kevin on 2/28/2015.
 */
public class TabsDashBoardAdapter extends FragmentPagerAdapter {

    public TabsDashBoardAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Gets the Fragment to start based on the Tab selection
     *
     * @param position  The index of the selected tab
     * @return Fragment The fragment corresponding to the selected tab
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Well Dashboard fragment activity
                return new WellDashBoardFragment();
            case 1:
                // Well Plots fragment activity
                return new WellPlotsFragment();
        }

        return null;
    }

    /**
     * Gets the number of tabs in the TabBar
     *
     * @return  The number of Tabs
     */
    @Override
    public int getCount() {
        return 2;
    }
}
