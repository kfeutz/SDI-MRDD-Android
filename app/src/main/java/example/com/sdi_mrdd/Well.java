package example.com.sdi_mrdd;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents Wells as defined by SDI. Wells contain a unique
 * well id and a string name.
 *
 * Created by Kevin on 2/16/2015.
 */
public class Well implements Parcelable {

    /* The well id as specified by SDI */
    private String wellId;

    /* The name of the well as specified by SDI */
    private String name;

    /**
     * Constructor that makes wells
     *
     * @param wellId    The id to assign to this well
     * @param name      The name to assign to this well
     */
    public Well (String wellId, String name) {
        this.name = name;
        this.wellId = wellId;
    }

    /**
     * Constructor that makes a parceable well. This allows us to pass wells from activity
     * to activity.
     *
     * @param in
     */
    private Well(Parcel in) {
        this.name = in.readString();
        this.wellId = in.readString();
    }

    /**
     * Retrieves the name belonging to this well
     *
     * @return String   The name belonging to this well
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name belonging to this well
     *
     * @param name   The name to assign to this well
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the id belonging to this well
     *
     * @return String   The id belonging to this well
     */
    public String getWellId() {
        return wellId;
    }

    /**
     * Sets the well id belonging to this well
     *
     * @param wellId   The well id to assign to this well
     */
    public void setWellId(String wellId) {
        this.wellId = wellId;
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
     * pass Well objects from activity to activity
     *
     * @param dest      The Parcel to write to
     * @param flags     The number of flags - not used
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(wellId);
    }

    /**
     * Used to regenerate Wells. All Parcelables must have a CREATOR that
     * implements these two methods
     */
    public static final Parcelable.Creator<Well> CREATOR = new Parcelable.Creator<Well>() {
        public Well createFromParcel(Parcel in) {
            return new Well(in);
        }

        public Well[] newArray(int size) {
            return new Well[size];
        }
    };
}
