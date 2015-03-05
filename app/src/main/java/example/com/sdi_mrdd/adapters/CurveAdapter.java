package example.com.sdi_mrdd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        TextView units;
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
            viewHolder.units = (TextView) row.findViewById(R.id.curveData);
            row.setTag(viewHolder);

        }
        else {
            viewHolder = (CurveCardHolder)row.getTag();
        }
        Curve curve = getItem(position);
        viewHolder.name.setText(curve.getName());
        viewHolder.units.setText(Double.toString(curve.getUnits()));
        return row;
    }
}
