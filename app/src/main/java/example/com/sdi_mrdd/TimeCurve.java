package example.com.sdi_mrdd;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kevin on 2/28/2015.
 */
public class TimeCurve extends Curve {
    public TimeCurve(String id, String name) {
        super(id, name);
    }

    private TimeCurve(Parcel in) {
        super(in);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Curve> CREATOR = new Parcelable.Creator<Curve>() {
        public Curve createFromParcel(Parcel in) {
            return new TimeCurve(in);
        }

        public Curve[] newArray(int size) {
            return new Curve[size];
        }
    };
}
