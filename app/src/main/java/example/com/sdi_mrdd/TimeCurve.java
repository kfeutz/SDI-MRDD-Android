package example.com.sdi_mrdd;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents TimeCurves as defined by SDI.
 * Subclass of curve
 *
 * Created by Kevin on 2/28/2015.
 */
public class TimeCurve extends Curve {

    /**
     * Creates a new TimeCurve
     *
     * @param id    The id to give to the curve
     * @param name  The name to give to the curve
     */
    public TimeCurve(String id, String name) {
        super(id, name);
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
}
