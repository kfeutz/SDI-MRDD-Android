package example.com.sdi_mrdd;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Kevin on 2/16/2015.
 */
public abstract class Curve implements Parcelable {
    private String id;
    private String name;
    private int units = 0;

    public Curve (String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor that makes a parceable Curve. This allows us to pass Curves from activity
     * to activity.
     * @param in
     */
    public Curve(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Required for Parceable objects
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Must write to the Parcel in the same order we read from it in order to correctly
     * pass Curve objects from activity to activity
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curve)) return false;

        Curve curve = (Curve) o;

        if (units != curve.units) return false;
        if (!id.equals(curve.id)) return false;
        if (!name.equals(curve.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + units;
        return result;
    }
}
