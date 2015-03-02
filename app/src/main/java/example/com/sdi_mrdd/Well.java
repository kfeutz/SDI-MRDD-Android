package example.com.sdi_mrdd;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kevin on 2/16/2015.
 */
public class Well implements Parcelable {
    private String name;
    private String wellId;

    /**
     * Constructore that makes regular wells
     * @param wellId
     * @param name
     */
    public Well (String wellId, String name) {
        this.name = name;
        this.wellId = wellId;
    }

    /**
     * Constructor that makes a parceable well. This allows us to pass wells from activity
     * to activity.
     * @param in
     */
    private Well(Parcel in) {
        this.name = in.readString();
        this.wellId = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(wellId);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Well> CREATOR = new Parcelable.Creator<Well>() {
        public Well createFromParcel(Parcel in) {
            return new Well(in);
        }

        public Well[] newArray(int size) {
            return new Well[size];
        }
    };
}
