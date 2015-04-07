package example.com.sdi_mrdd.dataitems;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import example.com.sdi_mrdd.MyRobolectricTestRunner;

/**
 * Created by Allen on 3/11/2015.
 */

@Config(emulateSdk =  18, manifest = "src/main/AndroidManifest.xml")
@RunWith(MyRobolectricTestRunner.class)
public class PlotTest {
    Plot plot;
    Plot listPlot;
    List<Curve> curves = new ArrayList<Curve>();

    @Before
    public void setUp() {
        plot = new Plot(1, "test1", "plotId");
        listPlot = new Plot(curves, 2 ,"test2", "listPlotId");
    }

    @Test
    public void testClassFound() {
        Assert.assertNotNull(plot);
        Assert.assertNotNull(listPlot);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(1, plot.getId());
    }

    @Test
    public void testSetId() {
        plot.setId(10);
        Assert.assertEquals(10, plot.getId());
    }


    @Test
    public void testGetWellId() {
        Assert.assertEquals("plotId", plot.getWellId());
    }

    @Test
    public void testSetWellId() {
        plot.setWellId("new");
        Assert.assertEquals("new", plot.getWellId());
    }


    @Test
    public void testGetName() {
        Assert.assertEquals("test1", plot.getName());
    }

    @Test
    public void testSetName() {
        plot.setName("newName");
        Assert.assertEquals("newName", plot.getName());
    }

    @Test
    public void testEquals() {
        Plot plot1 = new Plot(0, "zero", "one");
        Plot plot2 = new Plot(0, "zero", "one");

        Assert.assertEquals(plot1, plot2);
    }



}
