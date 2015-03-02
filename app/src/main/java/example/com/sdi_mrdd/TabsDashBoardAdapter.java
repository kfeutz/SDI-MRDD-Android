package example.com.sdi_mrdd;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Kevin on 2/28/2015.
 */
public class TabsDashBoardAdapter extends FragmentPagerAdapter {

    public TabsDashBoardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Well Dashboard fragment activity
                return new WellDashBoardFragment();
            case 1:
                // Games fragment activity
                return new WellPlotsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
