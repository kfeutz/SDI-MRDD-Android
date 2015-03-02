package example.com.sdi_mrdd;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Provides a way for plots to be rendered as rows in a ListView
 *
 * Created by Kevin on 3/1/2015.
 */
public class PlotAdapter extends ArrayAdapter<Plot> {
    /* The context to which this adapter belongs */
    private Context context;

    /* Resource id of the layout file we want to use for displaying each plot */
    private int layoutResourceId;

    /* The list of plots to display */
    private List<Plot> data = null;

    /**
     * Create a Plot Adapter for displaying on the Plot dashboard page.
     *
     * @param context           A reference to the activity in which we use PlotAdapter
     * @param layoutResourceId  Resource id of the layout file we want to use for displaying each
     *                          ListView item. It is in listview_plot_row.xml
     * @param data              List of Plot objects we would like to display on the page.
     */
    public PlotAdapter(Context context, int layoutResourceId, List<Plot> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
     * Will be called for each item in the ListView being rendered. Creates view with the properties set
     * as we want
     * @param position      Position of the item in the list view
     * @param convertView   The actual row being displayed
     * @param parent        Used to inflate each row
     * @return  row         The View to render
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlotHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PlotHolder();
            /* Get widget references by using R.id's defined in listview_plot_row */
            holder.txtTitle = (TextView)row.findViewById(R.id.plotTitle);
            holder.numberCurvesDisplay = (TextView)row.findViewById(R.id.activeCurves);

            row.setTag(holder);
        }
        else {
            holder = (PlotHolder)row.getTag();
        }

        Plot plot = data.get(position);
        /* Set display for each plot holder component */
        holder.txtTitle.setText(plot.getName());
        holder.numberCurvesDisplay.setText("Active Curves: " + plot.getCurves().size());

        return row;
    }

    /**
     * Static class that represents the android widget objects to include in each plot
     * row
     */
    static class PlotHolder
    {
        TextView txtTitle;
        TextView numberCurvesDisplay;
    }
}
