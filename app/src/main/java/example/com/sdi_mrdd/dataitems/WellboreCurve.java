package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents WellboreCurves as defined by SDI.
 * Subclass of curve
 *
 * Created by Kevin on 2/28/2015.
 */
public class WellboreCurve extends Curve {

    private String ivValue;
    private String dvValue;
    /**
     * Creates a new WellboreCurve
     *
     * @param id    The id to give to the WellboreCurve
     * @param name  The name to give to the WellboreCurve
     */
    public WellboreCurve(String id, String name, String ivName, String dvName, String ivUnit, String dvUnit) {
        super(id, name, ivName, dvName, ivUnit, dvUnit);
        this.ivValue = "0";
        this.dvValue = "0";
    }

    /**
     * Constructor that makes a parceable WellboreCurve. This allows us to pass WellboreCurves from activity
     * to activity.
     *
     * @param in
     */
    private WellboreCurve(Parcel in) {
        super(in);
    }

    /**
     * Used to regenerate WellboreCurves. All Parcelables must have a CREATOR that
     * implements these two methods
     */
    public static final Parcelable.Creator<Curve> CREATOR = new Parcelable.Creator<Curve>() {
        public Curve createFromParcel(Parcel in) {
            return new WellboreCurve(in);
        }

        public Curve[] newArray(int size) {
            return new Curve[size];
        }
    };

    public String getCurveType() {
        return "wellbore_curve";
    }

    public void setIvValue(String ivValue) {
        this.ivValue = ivValue;
    }

    public void setDvValue(String dvValue) { this.dvValue = dvValue; }

    public String getDvValue() {
        if(this.dvValue == null) {
            return "0";
        }
        else {
            if(this.dvValue.contains(".")
                    && this.dvValue.substring(this.dvValue.indexOf('.')).length() > 5) {
                return this.dvValue.substring(0, this.dvValue.indexOf('.'))
                        + this.dvValue.substring(this.dvValue.indexOf('.'), this.dvValue.indexOf('.') + 5);
            }
            else {
                return this.dvValue;
            }
        }
    }

    public String getIvValue() {
        if(this.ivValue == null) {
            return "0";
        }
        else if(this.ivValue.equals("0")) {
            return this.ivValue;
        }
        else {
            if(this.ivValue.contains(".")
                    && this.ivValue.substring(this.ivValue.indexOf('.')).length() > 5) {
                return this.ivValue.substring(0, this.ivValue.indexOf('.'))
                        + this.ivValue.substring(this.ivValue.indexOf('.'), this.ivValue.indexOf('.') + 3);
            }
            else {
                return this.ivValue;
            }
        }
    }

}
