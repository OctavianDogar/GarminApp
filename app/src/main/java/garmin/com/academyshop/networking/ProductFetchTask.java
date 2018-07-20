package garmin.com.academyshop.networking;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.List;

import garmin.com.academyshop.MainActivity;
import garmin.com.academyshop.ProductsLoader;
import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.persistence.local.AcademyShopDBManager;

/**
 * Created by Octavian on 5/15/2017.
 */

public class ProductFetchTask extends AsyncTask {

    private static final String TAG = ProductFetchTask.class.getSimpleName();

    private Context context;

    public ProductFetchTask(Context context){
        this.context = context;
    }

    @Override
    protected List<Product> doInBackground(Object... params) {


//        RestApi restApi = new RestApi();
//        try {
//            return restApi.getData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    protected void onPostExecute(List<Product> products) {
        if(products!=null){
            AcademyShopDBManager.getInstance(context).insertProducts(products);
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ProductsLoader.PRODUCTS_CHANGE));
        }
    }

}
