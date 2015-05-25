package example.com.sdi_mrdd.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import example.com.sdi_mrdd.R;
import example.com.sdi_mrdd.activities.WellDashBoardActivity;
import example.com.sdi_mrdd.asynctasks.LoadCurveDataTask;
import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.Plot;

/**
 * Created by Allen on 2/26/2015.
 */
public class CurveAdapter extends RecyclerView.Adapter<CurveAdapter.ViewHolder> {
    public List<Curve> curves = new ArrayList<Curve>();
    private int rowLayout;
    public Context mContext;
    /* Allows to remember the last item shown on screen */
    private int lastPosition = -1;

    /* Position of long click */
    private int position;

    public CurveAdapter(List<Curve> curves, int rowLayout, Context context) {
        this.curves = curves;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Curve curve = curves.get(i);
        final int position = viewHolder.getPosition();
        viewHolder.name.setText(curve.getName());
        if(curve.getCurveType().equals("time_curve")) {
            viewHolder.ivName.setText(curve.getIvName() + " (age)");
        }
        else {
            viewHolder.ivName.setText(curve.getIvName());
        }
        viewHolder.dvName.setText(curve.getDvName());
        viewHolder.ivData.setText(curve.getIvValue());
        viewHolder.dvData.setText(curve.getDvValue());
        viewHolder.curveId = curve.getId();

        /* Apply animation when view is bound */
        setAnimation(viewHolder.cardview, position);

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(viewHolder.getPosition());
                return false;
            }
        });
    }
    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return curves == null ? 0 : curves.size();
    }

    public void addAll(List<Curve> list){
        for(int i = 0; i < list.size(); i++) {
            curves.add(list.get(i));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView ivName;
        TextView ivData;
        TextView dvName;
        TextView dvData;
        TextView ivUnit;
        TextView dvUnit;
        CardView cardview;
        AdapterView.OnItemClickListener mItemClickListener;
        String curveId;

        public ViewHolder(View itemView) {
            super(itemView);
            cardview = (CardView) itemView.findViewById(R.id.card_view);
            name = (TextView) itemView.findViewById(R.id.curveTitle);
            ivName = (TextView) itemView.findViewById(R.id.curveIvName);
            dvName = (TextView) itemView.findViewById(R.id.curveDvName);
            ivData = (TextView) itemView.findViewById(R.id.curveIvData);
            dvData = (TextView) itemView.findViewById(R.id.curveDvData);
        }
    }
    public void clear() {
        curves.clear();
    }

    public void remove(int positionToRemove) {
        curves.remove(positionToRemove);
        this.notifyItemRemoved(positionToRemove);
    }

    public int getPosition() {
        return position;
    }

    public Curve getCurveFromPosition(int pos) {
        return this.curves.get(pos);
    }

    public void setPosition(int position) {
        this.position = position;
    }

   /* @Override
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
        if(curve.getCurveType().equals("time_curve")) {
            viewHolder.ivName.setText(curve.getIvName() + " (age)");
        }
        else {
            viewHolder.ivName.setText(curve.getIvName());
        }
        viewHolder.dvName.setText(curve.getDvName());
        viewHolder.ivData.setText(curve.getIvValue());
        viewHolder.dvData.setText(curve.getDvValue());
        return row;
    }*/
}
