package garmin.com.academyshop.networking;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

import garmin.com.academyshop.model.Product;

/**
 * Created by Octavian on 5/15/2017.
 */

public class ProductFetchJobService extends JobService {

    private static final int FETCH_JOB_ID=1024;
    private static final String TAG = ProductFetchJobService.class.getSimpleName();


    private static final JobInfo JOB_INFO;

    private static final long JOB_RUN_INTERVAL = 5*1000 ;

    private ProductFetchTask fetchTask;


    static{
        JobInfo.Builder builder = new JobInfo.
                Builder(FETCH_JOB_ID, new ComponentName("garmin.com.academyshop", ProductFetchJobService.class.getName()))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(JOB_RUN_INTERVAL);
        JOB_INFO = builder.build();
    }

    public static void startJob(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(JOB_INFO);
    }

    public static void stopJob(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(FETCH_JOB_ID);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"onStartJob");

        fetchTask = new ProductFetchTask(getApplicationContext()){

            @Override
            protected void onPostExecute(List<Product> products) {
                Log.d(TAG,"fetching data");
                super.onPostExecute(products);
                jobFinished(params,false);
            }
        };
        fetchTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"onStopJob");

        fetchTask.cancel(true);
        return false;
    }
}
