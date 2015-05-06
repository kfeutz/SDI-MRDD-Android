package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
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

    /* The name of the curve's independent variable */
    private String ivName;

    /* The name of the curve's dependent variable */
    private String dvName;

    private String ivUnit;

    private String dvUnit;

    private String ivValue;

    private String dvValue;

    /* The number of units. Set default to 0 */
    private double units = 0;

    private String nextStartUnit = "0";

    private String nextEndUnit = "0";

    private ArrayList<Double> dvValues = new ArrayList<>();

    private ArrayList<String> ivValues = new ArrayList<>();

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /**
     * Constructor to create a Curve
     *
     * @param id    The unique id belonging to the Curve
     * @param name  The name of the new curve
     */
    public Curve (String id, String name, String ivName, String dvName, String ivUnit, String dvUnit) {
        this.id = id;
        this.name = name;
        this.ivName = ivName;
        this.dvName = dvName;
        this.ivUnit = ivUnit;
        this.dvUnit = dvUnit;
        this.ivValue = "0.0";
        this.dvValue = "0.0";
    }

    public ArrayList<Double> getDvValues() {
        return dvValues;
    }

    public ArrayList<String> getIvValues() {
        return ivValues;
    }

    public void setIvValues(ArrayList<String> ivValueList) {
        this.ivValues.clear();
        for(int i = 0; i < ivValueList.size(); i++) {
            ivValues.add(ivValueList.get(i));
        }
    }

    public void setDvValues(ArrayList<String> dvValueList) {
        this.dvValues.clear();
        for(int i = 0; i < dvValueList.size(); i++) {
            dvValues.add(Double.parseDouble(dvValueList.get(i)));
        }
    }
    public String getNextStartUnit() {
        return nextStartUnit;
    }

    public String getNextEndUnit() {
        return nextEndUnit;
    }

    public void setNextStartUnit(String start) {
        this.nextStartUnit = start;
    }

    public void setNextEndUnit(String end) {
        this.nextEndUnit= end;
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
        this.ivName = in.readString();
        this.dvName = in.readString();
        this.ivUnit = in.readString();
        this.dvUnit = in.readString();
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

    public String getIvName() {
        return ivName;
    }

    public void setIvName(String ivName) {
        this.ivName = ivName;
    }

    public String getDvName() {
        return dvName;
    }

    public void setDvName(String dvName) {
        this.dvName = dvName;
    }

    public String getIvUnit() {
        return ivUnit;
    }

    public void setIvUnit(String ivUnit) {
        this.ivUnit = ivUnit;
    }

    public String getDvUnit() {
        return dvUnit;
    }

    public void setDvUnit(String dvUnit) {
        this.dvUnit = dvUnit;
    }

    public String getIvValue() {
        return ivValue;
    }

    public void setIvValue(String ivValue) {
        this.ivValue = ivValue;
    }

    public String getDvValue() {
        return dvValue;
    }

    public void setDvValue(String dvValue) {
        this.dvValue = dvValue;
    }

    public String getCurveType() {
        return "Curve";
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
        dest.writeString(ivName);
        dest.writeString(dvName);
        dest.writeString(ivUnit);
        dest.writeString(dvUnit);
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
