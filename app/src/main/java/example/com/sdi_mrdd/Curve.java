package example.com.sdi_mrdd;

/**
 * Created by Kevin on 2/16/2015.
 */
public class Curve {
    private long id;
    private String name;
    private int units;

    public Curve (String name, int units) {
        this.name = name;
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
