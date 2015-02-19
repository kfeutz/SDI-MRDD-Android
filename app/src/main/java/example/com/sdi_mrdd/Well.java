package example.com.sdi_mrdd;

/**
 * Created by Kevin on 2/16/2015.
 */
public class Well {
    private String name;
    private int wellId;

    public Well (String name, int wellId) {
        this.name = name;
        this.wellId = wellId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWellId() {
        return wellId;
    }

    public void setWellId(int wellId) {
        this.wellId = wellId;
    }
}
