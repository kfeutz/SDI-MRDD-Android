package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2/28/2015.
 */
public class Plot implements Parcelable {
    private List<Curve> curves;
    private int id;
    private String wellId;
    private String name;

    public Plot (int id, String name, String wellId) {
        this.curves = new ArrayList<Curve>();
        this.id = id;
        this.name = name;
        this.wellId = wellId;
    }

    public Plot (List<Curve> curves, int id, String name, String wellId) {
        this.curves = curves;
        this.id = id;
        this.name = name;
        this.wellId = wellId;
    }

    /**
     * Constructor that makes a parceable plot. This allows us to pass plots from activity
     * to activity.
     * @param in
     */
    private Plot(Parcel in) {
        List<Curve> curvesToAdd = new ArrayList<Curve>();
        in.readList(curvesToAdd, getClass().getClassLoader());
        this.setCurves(curvesToAdd);
        this.id = in.readInt();
        this.name = in.readString();
        this.wellId = in.readString();
    }

    public List<Curve> getCurves() {
        return curves;
    }

    public void setCurves(List<Curve> curves) {
        this.curves = curves;
    }

    public void setCurve(int index, Curve curve) {
        this.curves.set(index, curve);
    }

    public void addCurve(Curve curve) {
        this.curves.add(curve);
    }

    public void removeCurve(Curve curve) {
        this.curves.remove(curve);
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plot plot = (Plot) o;

        if (curves != null ? !curves.equals(plot.curves) : plot.curves != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return curves != null ? curves.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Must write to the Parcel in the same order we read from it in order to correctly
     * pass Plot objects from activity to activity
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(curves);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(wellId);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Plot> CREATOR = new Parcelable.Creator<Plot>() {
        public Plot createFromParcel(Parcel in) {
            return new Plot(in);
        }

        public Plot[] newArray(int size) {
            return new Plot[size];
        }
    };
}
