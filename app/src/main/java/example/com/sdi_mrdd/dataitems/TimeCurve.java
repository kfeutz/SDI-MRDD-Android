package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents TimeCurves as defined by SDI.
 * Subclass of curve
 *
 * Created by Kevin on 2/28/2015.
 */
public class TimeCurve extends Curve {

    private String ivValue;
    /* Number of 100ns between Jan 1. 1601 and Jan 1. 1970 */
    private final long NANOSECONDSBETWEENEPOCHS = 116444736000000000L;

    /**
     * Creates a new TimeCurve
     *
     * @param id    The id to give to the curve
     * @param name  The name to give to the curve
     */
    public TimeCurve(String id, String name, String ivName, String dvName, String ivUnit, String dvUnit) {
        super(id, name, ivName, dvName, ivUnit, dvUnit);
    }

    /**
     * Constructor that makes a parceable TimeCurve. This allows us to pass TimeCurves from activity
     * to activity.
     *
     * @param in
     */
    private TimeCurve(Parcel in) {
        super(in);
    }

    public void setIvValue(String ivValue) {
        this.ivValue = ivValue;
    }

    public String getIvValue() {
        long milisecondsFromEpoch;
        /* Converting current Unix epoch time to LDAP time format*/
        long currentTimeLdap = (System.currentTimeMillis() * 10000) + NANOSECONDSBETWEENEPOCHS;
        double timeDifSecs;
        long timeDifNanos;
        int days;
        int hours;
        int minutes;
        int seconds;
        if(this.ivValue == null) {
            return "0";
        }
        else if(this.ivValue.equals("0")) {
            return this.ivValue;
        }
        else {
            if(this.ivValue.equals("Loading data...")) {
                this.ivValue = "No data";
            }
            if(!this.ivValue.equals("No data")) {
                timeDifSecs = (double) (currentTimeLdap - Long.parseLong(this.ivValue)) / 10000000.0;
                days = (int) timeDifSecs / 86400;
                hours = (int) (timeDifSecs % 86400) / 3600;
                minutes = (int) ((timeDifSecs % 86400) % 3600) / 60;
                seconds = (int) ((timeDifSecs % 86400) % 3600) % 60;
                return days + "D " + hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
            }
            else {
                return this.ivValue;
            }
        }
    }
    /**
     * Used to regenerate TimeCurves. All Parcelables must have a CREATOR that
     * implements these two methods
     */
    public static final Parcelable.Creator<Curve> CREATOR = new Parcelable.Creator<Curve>() {
        public Curve createFromParcel(Parcel in) {
            return new TimeCurve(in);
        }

        public Curve[] newArray(int size) {
            return new Curve[size];
        }
    };

    public String getCurveType() {
        return "time_curve";
    }
}
