package example.com.sdi_mrdd.asynctasks;

/**
 * Created by Kevin on 4/26/2015.
 */
public interface AsyncTaskCompleteListener<T> {
    public void onTaskComplete(T result);
}
