package example.com.sdi_mrdd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.dataitems.Curve;

/**
 * Created by Allen on 2/26/2015.
 */
public class CurveAdapter extends ArrayAdapter<Curve> {
    private List<Curve> curves = new ArrayList<Curve>();

    static class CurveCardHolder {
        TextView name;
        TextView ivName;
        TextView ivData;
        TextView dvName;
        TextView dvData;
        TextView ivUnit;
        TextView dvUnit;
    }

    public CurveAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Curve obj) {
        curves.add(obj);
        super.add(obj);
    }


    public void addAll(ArrayList<Curve> list){
        for(int i = 0; i < list.size(); i++) {
            curves.add(list.get(i));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CurveCardHolder viewHolder;
        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.well_dash_board_card, parent, false);
            viewHolder = new CurveCardHolder();
            viewHolder.name = (TextView) row.findViewById(R.id.curveTitle);
            viewHolder.ivName = (TextView) row.findViewById(R.id.curveIvName);
            viewHolder.dvName = (TextView) row.findViewById(R.id.curveDvName);
            viewHolder.ivData = (TextView) row.findViewById(R.id.curveIvData);
            viewHolder.dvData = (TextView) row.findViewById(R.id.curveDvData);
            row.setTag(viewHolder);

        }
        else {
            viewHolder = (CurveCardHolder)row.getTag();
        }
        Curve curve = getItem(position);
        viewHolder.name.setText(curve.getName());
        viewHolder.ivName.setText(curve.getIvName());
        viewHolder.dvName.setText(curve.getDvName());
        viewHolder.ivData.setText(curve.getIvValue());
        viewHolder.dvData.setText(curve.getDvValue());
        return row;
    }
}
