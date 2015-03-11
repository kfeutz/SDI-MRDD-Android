package example.com.sdi_mrdd.dataitems;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.junit.Before;
import org.junit.Test;

import example.com.sdi_mrdd.MyRobolectricTestRunner;

/**
 * Created by Olin on 3/11/2015.
 */
@Config(emulateSdk = 18, manifest = "src/main/AndroidManifest.xml")
@RunWith(MyRobolectricTestRunner.class)
public class WellTest {

    Well well;

    @Before
    public void setup() {
        well = new Well("1", "testWell");
    }

    @Test
    public void testClassFound() {
        Assert.assertNotNull(well);
    }

    @Test
    public void testWellName() {
        Assert.assertEquals("testWell", well.getName());

        well.setName("newTestWell");
        Assert.assertEquals("newTestWell", well.getName());
    }

    @Test
    public void testWellID() {
        Assert.assertEquals("1", well.getWellId());

        well.setWellId("2");
        Assert.assertEquals("2", well.getWellId());
    }
}