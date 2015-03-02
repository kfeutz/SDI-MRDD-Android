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
 * Created by Kevin on 2/28/2015.
 */
public class WellAdapter extends ArrayAdapter<Well> {
    Context context;
    int layoutResourceId;
    List<Well> data = null;

    /**
     * Create a Well Adapter for displaying on the Well List page.
     * @param context   A reference to the activity in which we use WellAdapter
     * @param layoutResourceId  Resource id of the layout file we want to use for displaying each ListView
     *                          item. It is in listview_well_row.xml
     * @param data              List of Well objects we would like to display on the page.
     */
    public WellAdapter(Context context, int layoutResourceId, List<Well> data) {
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
        WellHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WellHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.wellTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (WellHolder)row.getTag();
        }

        Well well = data.get(position);
        holder.txtTitle.setText(well.getName());

        return row;
    }

    static class WellHolder
    {
        TextView txtTitle;
    }
}
