package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

/**
 * This class represents Curves as defined by SDI. Curves contain a unique
 * string id, a string name, and an amount in units.
 *
 * TODO: Add call to get units of curve from server.
 * TODO: Add method to randomly generate numbers for testing.
 *
 * Created by Kevin on 2/16/2015.
 */
public abstract class Curve implements Parcelable {

    /* The unique id of the curve defined by SDI */
    private String id;

    /* The name of the curve defined by SDI */
    private String name;

    /* The number of units. Set default to 0 */
    private double units = 0;

    /**
     * Constructor to create a Curve
     *
     * @param id    The unique id belonging to the Curve
     * @param name  The name of the new curve
     */
    public Curve (String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor that makes a parceable Curve. This allows us to pass Curves from activity
     * to activity.
     *
     * @param in
     */
    public Curve(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    /**
     * Retrieves a random number to represent the units of this curve based on the passed range.
     *
     * @param low   The lower bound of the range in which to generate the number
     * @param high  The upper bound of the range in which to generate the number
     */
    public int getUnitFromRange(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    /**
     * Retrieves the name of a Curve
     *
     * @return  String  The name of this Curve
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of a Curve
     *
     * @param name  The name to set to this Curve
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the units belonging to a curve
     *
     * TODO: Change this to an asynchronous server call
     *
     * @return  int     The number of units belong to this Curve
     */
    public double getUnits() {
        return units;
    }

    /**
     * Sets the units belonging to a curve
     *
     * @param units     The number of units to set to this Curve
     */
    public void setUnits(double units) {
        this.units = units;
    }

    /**
     * Retrieves the id from a Curve
     *
     * @return  String      The id belong to this Curve
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id belonging to a curve
     *
     * @param id     The id to set to this Curve
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Required for Parceable objects. Must override and return 0
     * for our object to be parceable
     *
     * @return  0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Must write to the Parcel in the same order we read from it in order to correctly
     * pass Curve objects from activity to activity
     *
     * @param dest      The Parcel to write to
     * @param flags     The number of flags - not used
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    /**
     * Auto generated equals method
     *
     * @param o     Object to compare this Curve to
     * @return      True of Object o has matching attributes to this Curve
     *              False otherwise.
     */
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

    /**
     * Auto generated hashcode method
     *
     * @return  int     This Curve's hashcode
     */
    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (int) units;
        return result;
    }
}
