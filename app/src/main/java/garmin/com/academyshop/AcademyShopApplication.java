package garmin.com.academyshop;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import garmin.com.academyshop.networking.ProductFetchJobService;

/**
 * Created by Octavian on 5/4/2017.
 */

public class AcademyShopApplication extends Application {

    public static Context APPLICATION_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APPLICATION_CONTEXT = this;
        Stetho.initializeWithDefaults(this);
        ProductFetchJobService.startJob(this);
    }


}
