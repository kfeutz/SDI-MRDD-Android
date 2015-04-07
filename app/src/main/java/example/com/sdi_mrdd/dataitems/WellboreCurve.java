package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents WellboreCurves as defined by SDI.
 * Subclass of curve
 *
 * Created by Kevin on 2/28/2015.
 */
public class WellboreCurve extends Curve {

    /**
     * Creates a new WellboreCurve
     *
     * @param id    The id to give to the WellboreCurve
     * @param name  The name to give to the WellboreCurve
     */
    public WellboreCurve(String id, String name, String ivName, String dvName, String ivUnit, String dvUnit) {
        super(id, name, ivName, dvName, ivUnit, dvUnit);
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
}
