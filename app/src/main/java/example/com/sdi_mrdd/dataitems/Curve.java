package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    private String wellboreType;

    private String wellboreId;

    /* The number of units. Set default to 0 */
    private double units = 0;

    private String nextStartUnit = "0";

    private String nextEndUnit = "0";

    private ArrayList<Double> dvValues = new ArrayList<>();

    private ArrayList<String> ivValues = new ArrayList<>();

    private ArrayList<HashMap<String, String>> startEndIvValues = new ArrayList<>();

    private int plotStartIndexForMap = 0;
    private int plotEndIndexForMap = 1;

    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    final public int MAX_POINTS_FOR_PLOT = 3000;

    /* Size of each point interval, based on first api result */
    public int intervalSize = 0;

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

    public void addToStartEndMap(String startIv, String endIv) {
        HashMap<String, String> ivMap = new HashMap<>();
        boolean containsMapping = false;

        /* Initial point set start and end times are blank for api calls */
        if(this.startEndIvValues.size() == 0) {
            HashMap<String, String> initialHashMap = new HashMap<>();
            initialHashMap.put("startIv", "");
            initialHashMap.put("endIv", "");
            this.startEndIvValues.add(initialHashMap);
        }

        /* Traverse ArrayList of maps to check if start and end iv mappings were already added */
        for (HashMap<String, String> existingIvMap : startEndIvValues) {
            if(existingIvMap.get("startIv").equals(startIv)
                    && existingIvMap.get("endIv").equals(endIv)) {
                containsMapping = true;
            }
        }
        /* Only add to arraylist if it doesn't contain the start and end mappings */
        if(!containsMapping) {
            ivMap.put("startIv", startIv);
            ivMap.put("endIv", endIv);
            this.startEndIvValues.add(ivMap);
        }
    }

    public boolean isIvAtYoungestPoint() {
        if(this.plotStartIndexForMap > 1) {
            return false;
        }
        else {
            return true;
        }
    }

    public ArrayList<HashMap<String, String>> getStartEndIvValues() {
        return this.startEndIvValues;
    }

    public void setIvValues(ArrayList<String> ivValueList) {
        ivValues.clear();
        for(int i = 0; i < ivValueList.size(); i++) {
            ivValues.add(ivValueList.get(i));
        }
    }

    public void setDvValues(ArrayList<String> dvValueList) {
        dvValues.clear();
        for(int i = 0; i < dvValueList.size(); i++) {
            dvValues.add(Double.parseDouble(dvValueList.get(i)));
        }
    }

    public int getPlotStartIndexForMap() {
        return plotStartIndexForMap;
    }

    public int getPlotEndIndexForMap() {
        return plotEndIndexForMap;
    }

    public void setPlotStartIndexForMap(int plotStartIndexForMap) {
        this.plotStartIndexForMap = plotStartIndexForMap;
    }

    public void prependIvDvValues(ArrayList<String> ivValueList, ArrayList<String> dvValueList) {
        if(prependIvValues(ivValueList) && prependDvValues(dvValueList)) {
            plotStartIndexForMap--;
            plotEndIndexForMap--;
            /* Map containing the latest (largest) set of iv values for the curve.
                 * Contains startIv and endIv used to retrieve next set of data */
            HashMap<String, String> currentDataPosition
                    = startEndIvValues.get(plotEndIndexForMap - 1);
            setNextStartUnit(currentDataPosition.get("startIv"));
            setNextEndUnit(currentDataPosition.get("endIv"));
        }
    }

    private boolean prependIvValues(ArrayList<String> ivValueList) {
        boolean tooManyPoints = false;
        ArrayList<String> tempIvValues = new ArrayList<>();
        ArrayList<String> finalIvValues = new ArrayList<>();
        if(ivValues.size() > MAX_POINTS_FOR_PLOT) {
            tooManyPoints = true;
        }
        int ivValueSize = ivValueList.size();
        if (plotStartIndexForMap > 0) {
            for (int i = 0; i < ivValueList.size(); i++) {
                /* Remove last value first if there are too many values */
                if (tooManyPoints) {
                    ivValues.remove(ivValues.size() - 1);
                }
                tempIvValues.add(ivValueList.get(i));
            }
            /* Add prepended values to front of new list */
            finalIvValues.addAll(tempIvValues);
            finalIvValues.addAll(ivValues);
            this.ivValues = finalIvValues;
            return true;
        }
        return false;
    }

    private boolean prependDvValues(ArrayList<String> dvValueList) {
        boolean tooManyPoints = false;
        ArrayList<Double> tempDvValues = new ArrayList<>();
        ArrayList<Double> finalDvValues = new ArrayList<>();
        if (dvValues.size() > MAX_POINTS_FOR_PLOT) {
            tooManyPoints = true;
        }
        if (plotStartIndexForMap > 0) {
            for(int i = 0; i < dvValueList.size(); i++) {
            /* Remove last value first if there are too many values */
                if(tooManyPoints) {
                    dvValues.remove(dvValues.size() - 1);
                }
                tempDvValues.add(Double.parseDouble(dvValueList.get(i)));
            }
            /* Add prepended values to front of new list */
            finalDvValues.addAll(tempDvValues);
            finalDvValues.addAll(dvValues);
            this.dvValues = finalDvValues;
            return true;
        }
        return false;
    }

    public void appendIvDvValues(ArrayList<String> ivValueList, ArrayList<String> dvValueList) {
        if (ivValueList.size() > 0 && dvValueList.size() > 0) {
            /* Interval has not been set yet, set to first array list size */
            if(this.intervalSize == 0) {
                this.intervalSize = ivValueList.size();
            }
            appendIvValues(ivValueList);
            appendDvValues(dvValueList);
            /* Move plot set start pointer if we have more than MAX_PLOT_POINTS */
            if (this.intervalSize > 0 &&
                    ((plotEndIndexForMap - plotStartIndexForMap) > (MAX_POINTS_FOR_PLOT / this.intervalSize))) {
                plotStartIndexForMap++;
            }
            plotEndIndexForMap++;
        }
    }

    public void appendIvValues(ArrayList<String> ivValueList) {
        boolean tooManyPoints = false;
        if(ivValues.size() > MAX_POINTS_FOR_PLOT) {
            tooManyPoints = true;
        }
        for(int i = 0; i < ivValueList.size(); i++) {
            /* Remove last value first if there are too many values */
            if(tooManyPoints) {
                ivValues.remove(0);
            }
            ivValues.add(ivValueList.get(i));
        }
    }

    public void appendDvValues(ArrayList<String> dvValueList) {
        boolean tooManyPoints = false;
        if(dvValues.size() > MAX_POINTS_FOR_PLOT) {
            tooManyPoints = true;
        }
        for(int i = 0; i < dvValueList.size(); i++) {
            /* Remove last value first if there are too many values */
            if(tooManyPoints) {
                dvValues.remove(0);
            }
            dvValues.add(Double.parseDouble(dvValueList.get(i)));
        }
    }

    public int getMAX_POINTS_FOR_PLOT() {
        return this.MAX_POINTS_FOR_PLOT;
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
        if(this.dvValue.contains(".")
                && this.dvValue.substring(this.dvValue.indexOf('.')).length() > 5) {
            return this.dvValue.substring(0, this.dvValue.indexOf('.'))
                    + this.dvValue.substring(this.dvValue.indexOf('.'), this.dvValue.indexOf('.') + 5);
        }
        else {
            return this.dvValue;
        }
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
