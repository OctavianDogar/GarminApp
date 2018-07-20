package garmin.com.academyshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.persistence.local.AcademyShopDBManager;

/**
 * Created by Octavian on 5/4/2017.
 */

public class ProductsLoader extends AsyncTaskLoader<List<Product>> {

    public static final String PRODUCTS_CHANGE = "PRODUCTS_CHANGED";
    private List<Product> products;
    private ProductChangeReceiver productsChangeReceiver;

    private static class ProductChangeReceiver extends BroadcastReceiver{

        private ProductsLoader loader;

        public ProductChangeReceiver(ProductsLoader productsLoader){
            this.loader = productsLoader;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            loader.onContentChanged();
        }
    }


    public ProductsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Product> loadInBackground() {
        products = AcademyShopDBManager.getInstance(getContext()).getProducts(true);
        return products;
    }

    @Override
    public void deliverResult(List<Product> data) {
        if(isReset()){
            releaseResources(data);
            return;
        }

        List<Product> oldProducts = products;
        products = data;

        if(isStarted()){
            super.deliverResult(data);
        }

        if(oldProducts!=null && oldProducts!=data)
            releaseResources(oldProducts);
    }


    @Override
    protected void onStartLoading() {
        if(products!=null)
            deliverResult(products);

        if(productsChangeReceiver==null){
            productsChangeReceiver = new ProductChangeReceiver(this);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(productsChangeReceiver, new IntentFilter(PRODUCTS_CHANGE));
        }


        if(takeContentChanged() || products ==null)
            forceLoad();


    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<Product> data) {
        releaseResources(data);
    }

    @Override
    protected void onReset() {
        stopLoading();
        if(products!=null) {
            releaseResources(products);
            products=null;

        }
        if(productsChangeReceiver!=null){
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(productsChangeReceiver);
        }

    }

    private void releaseResources(List<Product> data) {
        data = null;
    }
}
