package example.com.sdi_mrdd.asynctasks;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import example.com.sdi_mrdd.MyRobolectricTestRunner;

/**
 * Created by Kevin on 5/14/2015.
 */
@Config(emulateSdk = 18, manifest = "src/main/AndroidManifest.xml")
@RunWith(MyRobolectricTestRunner.class)
public class LoadWellCurvesTaskTest {
    TestLoadCurvesForWellTask loadCurvesForWellTask;
    String result;

    @Before
    public void setup()  {
        loadCurvesForWellTask = new TestLoadCurvesForWellTask(new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                result = result;
            }
        }, "1234");
        Assert.assertNotNull(loadCurvesForWellTask);
    }

    @Test
    public void testClassFound() {
        Assert.assertNotNull(loadCurvesForWellTask);
    }

    @Test
    public void testExecution() {
        Assert.assertEquals("", loadCurvesForWellTask.doInBackground());
    }
}
