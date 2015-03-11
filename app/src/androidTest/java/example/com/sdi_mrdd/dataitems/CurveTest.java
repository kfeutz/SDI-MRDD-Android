package example.com.sdi_mrdd.dataitems;

import android.os.Parcel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import example.com.sdi_mrdd.MyRobolectricTestRunner;

/**
 * Created by Kevin on 3/9/2015.
 */
@Config(emulateSdk = 18, manifest = "src/main/AndroidManifest.xml")
@RunWith(MyRobolectricTestRunner.class)
public class CurveTest {
    Curve timeCurve;
    Curve wellboreCurve;

    @Before
    public void setup()  {
        timeCurve = new TimeCurve("curveId", "curveName");
        wellboreCurve = new WellboreCurve("wellbore1", "wellbore");
    }

    @Test
    public void testClassFound() {
        Assert.assertNotNull(timeCurve);
        Assert.assertNotNull(wellboreCurve);
    }

    @Test
    public void testGetUnitFromRange() {
        Assert.assertTrue(timeCurve.getUnitFromRange(0, 1) == 0);
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("wellbore", wellboreCurve.getName());
    }

    @Test
    public void testSetName() {
        timeCurve.setName("change");
        Assert.assertEquals("change", timeCurve.getName());
    }

    @Test
    public void testGetUnits() {
        Assert.assertEquals(0.0, timeCurve.getUnits(), 0.01);
    }

    @Test
    public void testSetUnits() {
        timeCurve.setUnits(3.0);
        Assert.assertEquals(3.0, timeCurve.getUnits(), 0.01);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals("curveId", timeCurve.getId());
    }

    @Test
    public void testSetId() {
        timeCurve.setId("changedId");
        Assert.assertEquals("changedId", timeCurve.getId());
    }

    @Test
    public void testEquals() {
        Curve curve1 = new TimeCurve("curve", "curve");
        Curve curve2 = new TimeCurve("curve", "curve");
        Assert.assertEquals(curve1, curve2);
    }
}
